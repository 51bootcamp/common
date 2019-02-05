#-*- coding: utf-8 -*-
from api.models import * #import models
from api.custombackend import BaseJSONWebTokenAuthentication

from datetime import datetime

from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt #csrf exempt

from rest_framework import status, viewsets
from rest_framework.response import Response

from rest_framework import authentication, permissions
from rest_framework.response import Response
from rest_framework.views import APIView

from django.core.mail import BadHeaderError, send_mail
from django.http import HttpResponse, HttpResponseRedirect

import hashlib, json, jwt, random, pytz, time

def index(request):
    return HttpResponse("Hello woRld! you're at the api index.")

def epochToLocalTime(startTime, endTime, timezone):
    tz = pytz.timezone(timezone)
    startlocalTime = datetime.fromtimestamp(startTime, tz)
    startlocalTime = startlocalTime.strftime("%I : %M %p")
    endlocalTime = datetime.fromtimestamp(endTime, tz)
    endlocalTime = endlocalTime.strftime("%I : %M %p")

    return startlocalTime, endlocalTime

@csrf_exempt
def signup(request):
    if request.method != 'POST':
        return HttpResponse("Bad Request", status = 400)

    jsonBody = json.loads(request.body)

    try:
        User.objects.get(email = jsonBody['userEmail'])

    except User.DoesNotExist:
        userEmail = jsonBody['userEmail']
        password = hashlib.sha256(userEmail.encode()).hexdigest()[:10]
        newUser = User.objects.create_user(username = jsonBody['userName'],
                                            email = userEmail,
                                            password = password)
        newUser.save()

        payload = {
            'userEmail' : newUser.email,
            'userName'  : newUser.username
        }
        return JsonResponse({
            'token' : jwt.encode(payload, "SECRET_KEY").decode('utf-8'),
            'isLecturer' : newUser.isLecturer
            })
    else:
        return HttpResponse("you are already registered", status = 409)

@csrf_exempt
def login(request):
    if request.method != 'POST':
        return HttpResponse("Bad Request", status = 400)

    jsonBody = json.loads(request.body)

    userEmail = jsonBody['userEmail']

    try:
        user = User.objects.get(email = userEmail)
    except User.DoesNotExist:
        return HttpResponse("Please signup first", status = 409)
    if user:
        payload = {
            'userEmail' : user.email,
            'userName'  : user.username
        }
        return JsonResponse({
            'token' : jwt.encode(payload, "SECRET_KEY").decode('utf-8'),
            'isLecturer' : user.isLecturer
            })
    else:
        return JsonResponse({"userName":user.userName}, status = 202)

def getClassList(request, date):
    authentication_classes = (BaseJSONWebTokenAuthentication, )

    try:
        availableClassList = TimeTable.objects.filter( date = date )

    except TimeTable.objects.DoesNotExist:
        return HttpResponse("No class")

    else:
        availableClassList = availableClassList.values("classID").distinct()

        li = []
        for query in availableClassList:
            jsondict = {}
            imageList = []

            availableClass = Class.objects.get(pk = query['classID'])
            expert = availableClass.expert
            jsondict["classID"] = availableClass.classID
            jsondict["className"] = availableClass.className
            jsondict["expertName"] = expert.username
            classImageList = Image.objects.filter(classID =
                                                  availableClass.classID)
            for img in classImageList :
                imageList.append(img.coverImage.url)
            jsondict["coverImage"] = imageList
            jsondict["classRating"] = availableClass.classRating
            li.append(jsondict) #append: O(1)

        li = sorted(li, key=lambda classList : classList["classRating"] ,
                    reverse = True)
        return JsonResponse({"classList" : li}, status = 200)

def getClassInfo(request, classID, date):
    selectedClass = Class.objects.get(pk = classID)
    expert = selectedClass.expert
    timeslot = TimeTable.objects.filter(classID = selectedClass, 
                                        date = date,
                                        isBooked = False)

    availableTimeTable = []
    for i in timeslot:
        jsondict = {}
        jsondict["timeTableIdx"] = i.timeTableIdx
        jsondict["epStartTime"] = i.startTime
        st, et = epochToLocalTime(i.startTime,
                                  i.endTime,
                                  i.timezone)
        jsondict["startTime"] = st
        jsondict["endTime"] = et
        jsondict["isBooked"] = i.isBooked
        availableTimeTable.append(jsondict)
    availableTimeTable = sorted(availableTimeTable, 
        key=lambda timeTableList : timeTableList["epStartTime"]);

    for slot in availableTimeTable :
        del slot["epStartTime"]
    return JsonResponse({
                            "classID"           : selectedClass.classID,
                            "className"         : selectedClass.className,
                            "expertName"        : expert.username,
                            "minGuestCount"     : selectedClass.minGuestCount,
                            "maxGuestCount"     : selectedClass.maxGuestCount,
                            "availableTimeTable": availableTimeTable,
                            "price"             : selectedClass.price,
                            "classRating": selectedClass.classRating,
    })

@csrf_exempt
def imageUpload(request, classID):
    if request.method == 'POST':

        selectedClass = Class.objects.get(pk=classID)

        newImage = Image(coverImage=request.FILES['coverImage'],
                         ImageType=1,
                         classID=selectedClass
                         )
        newImage.save()

        return HttpResponse("upload image correctly", status=200)

class writeReviewView(APIView):
    @csrf_exempt
    def post(self, request, reservationID):
        bookingUser = request.user
        jsonBody = json.loads(request.body)

        classID = Class.objects.get(pk = jsonBody['classID'])

        selectedReservation = Reservation.objects.get(pk = reservationID)
        selectedReservation.isReviewed = True
        selectedReservation.save()

        newReview = Review(title = jsonBody['title'],
                           content = jsonBody['content'],
                           rating = jsonBody['rating'],
                           classID = classID,
                           userID = bookingUser
        )

        newReview.save()

        return JsonResponse({ "reviewIdx" : newReview.reviewIdx })

def getReviewList(request, classID):
    try:
        availableReviewList = Review.objects.filter(classID = classID)
    except Review.objects.DoesNotExist:
        return HttpResponse("No review")
    else:
        li = []
        for query in availableReviewList:
            jsondict = {}
            reviewer = query.userID

            jsondict["reviewIdx"] = query.reviewIdx
            jsondict["title"] = query.title
            jsondict["content"] = query.content
            jsondict["rating"] = query.rating
            jsondict["createdDate"] = query.createdDate
            jsondict["userName"] = reviewer.username

            li.append(jsondict)

        li = sorted(li, key=lambda reviewList: reviewList["createdDate"],
                    reverse=False)
        return JsonResponse({"reviewList": li}, status=200)

class upcomingView(APIView):
    def get(self, request):
        now = time.time()
        query = Reservation.objects.filter(user = request.user,
            timeTableIdx__startTime__gt=now)
        print(query)
        if not query:
            return JsonResponse({}, status = 200)
        else:
            # print(query)
            # query = query.objects.filter('timeTableIdx__startTime__gt=now')
            reservation = query.order_by('timeTableIdx__startTime').first()
            bookedTimeTable = reservation.timeTableIdx
            bookedClass = bookedTimeTable.classID
            expert = User.objects.get(email = bookedClass.expert.email)

            startTime, endTime = epochToLocalTime(bookedTimeTable.startTime,
                                         bookedTimeTable.endTime,
                                         bookedTimeTable.timezone) 
            return JsonResponse({
                        "reservationID" : reservation.reservationID,
                        "expertName"    : expert.username,
                        "className"     : bookedClass.className,
                        "date"          : bookedTimeTable.date,
                    })

class getReservationView(APIView):
    def get(self, request, reservationID):
        now = time.time()
        try: 
           reservation = Reservation.objects.get(pk = reservationID)
        except Reservation.DoesNotExist:
            return HttpResponse("check the reservation id again", status = 200)
        else:
            bookedTimeTable = reservation.timeTableIdx
            bookedClass = bookedTimeTable.classID
            expert = User.objects.get(email = bookedClass.expert.email)
            classImage = Image.objects.get(classID = bookedClass)
            isPassed = bookedTimeTable.endTime < now

            startTime, endTime = epochToLocalTime(bookedTimeTable.startTime,
                                         bookedTimeTable.endTime,
                                         bookedTimeTable.timezone)

        return JsonResponse({
                                "userEmail"     : request.user.email,
                                "expertName"    : expert.username,
                                "classID"       : bookedClass.classID,
                                "className"     : bookedClass.className,
                                "price"         : bookedClass.price,
                                "date"          : bookedTimeTable.date,
                                "startTime"     : startTime,
                                "endTime"       : endTime,
                                "guestCount"    : reservation.guestCount,
                                "coverImg"      : classImage.coverImage.url,
                                "isPassed"      : isPassed,
                                "isReviewed"    : reservation.isReviewed
                            }) 

class reservationView(APIView):
    def post(self, request):
        jsonBody = json.loads(request.body)

        bookingUser = request.user
        selectedTimeTable = TimeTable.objects.get(pk = jsonBody['timeTableIdx'])
        if selectedTimeTable.isBooked:
            return HttpResponse("Already booked", status = 409)
        newReservation = Reservation(timeTableIdx = selectedTimeTable,
                                    user = bookingUser,
                                    guestCount = jsonBody['guestCount'])
        selectedTimeTable.isBooked = True
        #TODO: think about how to deal with synchronization
        #mark the isbooked field first and than
        selectedTimeTable.save(update_fields = ['isBooked'])
        #made the reservation
        newReservation.save()
        selectedClass = selectedTimeTable.classID

        expert = selectedClass.expert
        startTime, endTime = epochToLocalTime(selectedTimeTable.startTime,
                                     selectedTimeTable.endTime,
                                     selectedTimeTable.timezone)

        return JsonResponse({
                                "reservationID" : newReservation.reservationID
                            })  

class getReservationList(APIView):
    def get(self, request):
        now = time.time()
        try:
            reservationList = Reservation.objects.filter(user = request.user)
        except Reservation.DoesNotExist:
            return HttpResponse("No reservation", status=203)
        else:
            li = []
            for query in reservationList:
                jsondict = {}
                
                reservation = Reservation.objects.get(pk=query.reservationID)
                bookedTimeTable = reservation.timeTableIdx
                bookedClass = bookedTimeTable.classID
                expert = bookedClass.expert
                classImage = Image.objects.get(classID = bookedClass)
                isPassed = bookedTimeTable.endTime < now

                startTime, endTime = epochToLocalTime(bookedTimeTable.startTime,
                                                      bookedTimeTable.endTime,
                                                      bookedTimeTable.timezone)
                
                jsondict["reservationID"] = reservation.reservationID
                jsondict["userEmail"] = request.user.email
                jsondict["expertName"] = expert.username
                jsondict["classID"] = bookedClass.classID
                jsondict["className"] = bookedClass.className
                jsondict["price"] = bookedClass.price
                jsondict["date"] = bookedTimeTable.date
                jsondict["startTime"] = startTime
                jsondict["endTime"] = endTime
                jsondict["guestCount"] = reservation.guestCount
                jsondict["coverImg"] = classImage.coverImage.url
                jsondict["isPassed"] = isPassed
                li.append(jsondict)  # append: O(1)

            li = sorted(li, key=lambda reservationList: reservationList["date"],
                reverse=False)
            return JsonResponse({"reservationList": li}, status=200)

class makeClassView(APIView):
    @csrf_exempt
    def makeClass(request):
        jsonBody = json.loads(request.body)
        selectedExpert = request.user

        newClass = Class(className = jsonBody['className'],
                         minGuestCount = jsonBody['minGuestCount'],
                         maxGuestCount = jsonBody['maxGuestCount'],
                         price = jsonBody['price'],
                         expertEmail = selectedExpert
                         )
        newClass.save()

        selectedDate = jsonBody['date']

        print(jsonBody['timeSlotList'])

        timeSlotList = jsonBody['timeSlotList']

        for timeSlot in timeSlotList:
            newTimeTable = TimeTable(date = selectedDate,
                                     startTime = timeSlot['startTime'],
                                     endTime = timeSlot['endTime'],
                                     classID = newClass)
            newTimeTable.save()

        return JsonResponse({
                    "classID": newClass.classID,
                    "timeTable" : newTimeTable.timeTableIdx
                })

def getInviteCode(request, inviteCode):
    try:
        availableCode = InviteCode.objects.get(randomCode = inviteCode)
    except InviteCode.DoesNotExist:
        return HttpResponse("Invite Code mismatched", status = 201)
    else:
        if (availableCode.isExpired == True):
            return HttpResponse("Invite Code mismatched", status = 203)
        else :
            availableCode.isExpired = True
            availableCode.save()
            return HttpResponse("Invite Code is existed", status=200)

class sendMailView(APIView):
    @csrf_exempt
    def post(self, request):

        jsonBody = json.loads(request.body)

        string = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"

        code = ""

        for i in range(6):
            code += random.choice(string)

        subject = "Common Invite Code"
        message = "This is Applicaion Common Invite Code \n \n" + code
        from_email = "51bootcamp.common@gmail.com"

        if subject and message and from_email:
            try:
                send_mail(subject, message, from_email, [jsonBody['email']], fail_silently=False)
                newInviteCode = InviteCode(
                                randomCode = code
                )

                newInviteCode.save()
                
            except BadHeaderError:
                return HttpResponse('Invalid header found.')
            return HttpResponseRedirect('/contact/thanks/')
        else:
            return HttpResponse('Make sure all fields are entered and valid.')