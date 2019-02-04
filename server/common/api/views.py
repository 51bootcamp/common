#-*- coding: utf-8 -*-
from api.models import * #import models

from datetime import datetime
import pytz

from django.http import HttpResponse, JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt #csrf exempt

from rest_framework import status, viewsets
from rest_framework.decorators import api_view
from rest_framework.response import Response

import json
import random

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
        User.objects.get(pk = jsonBody['userEmail'])

    except User.DoesNotExist:
        newUser = User(userEmail =  jsonBody['userEmail'],
            userName = jsonBody['userName'],
            accountType = jsonBody['accountType'])
        newUser.save()

        return HttpResponse("Signup Success", status = 201);

    else:
        return HttpResponse("you are already registered", status = 409)

@csrf_exempt
def login(request):
    if request.method != 'POST':
        return HttpResponse("Bad Request", status = 400)

    jsonBody = json.loads(request.body)

    try:
        user = User.objects.get(pk = jsonBody['userEmail'])

    except User.DoesNotExist:
        return HttpResponse("Please signup first", status = 409)

    else:
        #TODO: Password Check routine
        return JsonResponse({"userName":user.userName}, status = 202)

def getClassList(request, date):
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
            jsondict["classID"] = availableClass.classID
            jsondict["className"] = availableClass.className
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
    expert = User.objects.get(pk = selectedClass.expertEmail_id)
    timeslot = TimeTable.objects.filter(classID = selectedClass, date = date)

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
    availableTimeTable = sorted(availableTimeTable, key=lambda
        timeTableList : timeTableList["startTime"]);

    for slot in availableTimeTable :
        del slot["epStartTime"]

    return JsonResponse({
                            "classID"           : selectedClass.classID,
                            "className"         : selectedClass.className,
                            "expertName"        : expert.userName,
                            "minGuestCount"     : selectedClass.minGuestCount,
                            "maxGuestCount"     : selectedClass.maxGuestCount,
                            "availableTimeTable": availableTimeTable,
                            "price"             : selectedClass.price
                        })

@csrf_exempt
def makeReservation(request):
    jsonBody = json.loads(request.body)

    bookingUser = User.objects.get(pk = jsonBody['userEmail'])
    selectedTimeTable = TimeTable.objects.get(pk = jsonBody['timeTableIdx'])

    if selectedTimeTable.isBooked:
        return HttpResponse("Already booked", status = 409)
    newReservation = Reservation(timeTableIdx = selectedTimeTable,
                                userEmail = bookingUser,
                                guestCount = jsonBody['guestCount'])
    selectedTimeTable.isBooked = True
    #TODO: think about how to deal with synchronization
    #mark the isbooked field first and than
    selectedTimeTable.save(update_fields = ['isBooked'])
    #made the reservation
    newReservation.save()
    selectedClass = selectedTimeTable.classID

    expert = User.objects.get(pk = selectedClass.expertEmail_id)
    startTime, endTime = epochToLocalTime(selectedTimeTable.startTime,
                                 selectedTimeTable.endTime,
                                 selectedTimeTable.timezone)

    return JsonResponse({
                            "expertName"    : expert.userName,
                            "className"     : selectedClass.className,
                            "date"          : selectedTimeTable.date,
                            "startTime"     : startTime,
                            "endTime"       : endTime
                        })

@csrf_exempt
def makeClass(request):
    if request.method == 'POST':
        jsonBody = json.loads(request.body)
        timezone = "America/Los_Angeles"

        # selectedExpert = User.objects.get(pk = jsonBody['userEmail'])
        selectedExpert = User.objects.get(pk="ywj@kookmin.ac.kr")

        newClass = Class(className = jsonBody['className'],
                         minGuestCount = jsonBody['minGuestCount'],
                         maxGuestCount = jsonBody['maxGuestCount'],
                         price = jsonBody['price'],
                         expertEmail = selectedExpert
                         )
        newClass.save()

        # startEpochTime = jsonBody['startTime']
        # endEpochTime = jsonBody['endTime']
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
                    "className": newClass.className,
                    "timeTable" : newTimeTable.timeTableIdx
                })


    # string = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"
    # code = random.choice(string)
    # code = code[0:6]
    # print(code)

def getReservationList(request, userEmail):
    try:
        availableReservationList = Reservation.objects.filter(
            userEmail = userEmail)

    except Reservation.objects.DoesNotExist:
        return HttpResponse("No reservation", status=203)

    else:
        li = []
        for query in availableReservationList:
            jsondict = {}

            availableReservation = Reservation.objects.get(
                pk=query.reservationID)
            bookedTimeTable = availableReservation.timeTableIdx
            bookedClass = bookedTimeTable.classID
            expert = User.objects.get(pk = bookedClass.expertEmail_id)

            startTime, endTime = epochToLocalTime(bookedTimeTable.startTime,
                                         bookedTimeTable.endTime,
                                         bookedTimeTable.timezone)

            jsondict["reservationID"] = availableReservation.reservationID
            jsondict["userEmail"] = userEmail
            jsondict["expertName"] = expert.userName
            jsondict["classID"] = bookedClass.classID
            jsondict["className"] = bookedClass.className
            jsondict["price"] = bookedClass.price
            jsondict["date"] = bookedTimeTable.date
            jsondict["startTime"] = startTime
            jsondict["endTime"] = endTime
            jsondict["guestCount"] = availableReservation.guestCount

            li.append(jsondict)  # append: O(1)

        li = sorted(li, key=lambda reservationList: reservationList["date"],
                    reverse=False)
        return JsonResponse({"reservationList": li}, status=200)


def getReservation(request, userEmail):
    try:
        reservation = Reservation.objects.get(userEmail = userEmail)

    except Reservation.DoesNotExist:
        return HttpResponse("No upcoming class", status = 203)

    else:
        bookedTimeTable = reservation.timeTableIdx
        bookedClass = bookedTimeTable.classID
        expert = User.objects.get(pk = bookedClass.expertEmail_id)

        startTime, endTime = epochToLocalTime(bookedTimeTable.startTime,
                                     bookedTimeTable.endTime,
                                     bookedTimeTable.timezone)

    return JsonResponse({
                            "userEmail"     :userEmail,
                            "expertName"    : expert.userName,
                            "classID"       : bookedClass.classID,
                            "className"     : bookedClass.className,
                            "price"         : bookedClass.price,
                            "date"          : bookedTimeTable.date,
                            "startTime"     : startTime,
                            "endTime"       : endTime
                        })

@csrf_exempt
def imageUpload(request):
    if request.method == 'POST':
        # just for checking image upload properly
        selectedClass = Class.objects.get(pk = 4)

        newImage = Image(coverImage=request.FILES['coverImage'],
                         ImageType=1,
                         classID = selectedClass
                         )
        newImage.save()

        return HttpResponse("upload image correctly", status = 200)

# @csrf_exempt
# def imageUpload(request):
#     if request.method == 'POST':
#         jsonBody = json.loads(request.body)
#         selectedClass = Class.objects.get(pk = 4)
#
#         newImage = Image(coverImage=jsonBody['coverImage']['coverImage'],
#                          ImageType=1,
#                          classID = selectedClass
#                          )
#         newImage.save()
#
#         return HttpResponse("upload image correctly", status = 200)

@csrf_exempt
def writeReview(request):
    jsonBody = json.loads(request.body)

    bookingUser = User.objects.get(pk = jsonBody['userEmail'])
    classID = Class.objects.get(pk = jsonBody['classID'])

    newReview = Review(title = jsonBody['title'],
                       content = jsonBody['content'],
                       rating = jsonBody['rating'],
                       classID = classID,
                       userID = bookingUser
    )
    classID = Class.objects.get(pk = jsonBody['classID'])

    newReview = Review(title = jsonBody['title'],
    content = jsonBody['content'],

    rating = jsonBody['rating'],
    classID = classID,
    userID = bookingUser)
    newReview.save()

    return JsonResponse({
        "reviewIdx" : newReview.reviewIdx
    })

def getInviteCode(request, inviteCode):

    availableCode = InviteCode.objects.filter(randomCode = inviteCode)

    if availableCode.objects.DoesNotExist:
        if (availableCode.isExpired == True) :
            return HttpResponse("Invite Code is alreay expired", status=203)
        else:
            return HttpResponse("Invite Code mismatched", status = 203)
    else:
        availableCode.isExpired = True
        availableCode.save()

        return HttpResponse("Invite Code is existed", status=200)

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
            jsondict["userName"] = reviewer.userName

            li.append(jsondict)

        li = sorted(li, key=lambda reviewList: reviewList["createdDate"],
                    reverse=False)
        return JsonResponse({"reviewList": li}, status=200)

