from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse, JsonResponse
from api.models import * #import models
#csrf except
from django.views.decorators.csrf import csrf_exempt

from rest_framework import status, viewsets
from rest_framework.decorators import api_view
from rest_framework.response import Response

import json

def check_http_mehtod(request, type):
	if request.type == type:
		return True
	else: 
		return False

def index(request):
	return HttpResponse("Hello woRld! you're at the api index.")

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
		return HttpResponse("Signup Success");
	else:
		return HttpResponse("you are already registered", status = 300)

@csrf_exempt
def login(request):
	if request.method != 'POST':
		return HttpResponse("Bad Request", status = 400)

	jsonBody = json.loads(request.body)

	try:
		user = User.objects.get(pk = jsonBody['userEmail'])
	except User.DoesNotExist:
		return HttpResponse("PLz signup first", status = 300)
	else:
		#checking password routine in here, if we have our own signup feature
		return JsonResponse({"userName":user.userName})

def getClass(request):
	jsonBody = json.loads(request.body)
	try:
		availableClassList = TimeTable.objects.filter(
			date = jsonBody['date'], isBooked = False)
		availableClassList = availableClassList.values("classID").distinct()
		
	except (TimeTable.objects.DoesNotExist, Class.objects.DoesNotExist) as e:
		return HttpResponse("No class")
	else:
		return JsonResponse({'foo':'bars'})

def getClassInfo(request):
	jsonBody = json.loads(request.body)
	try:
		c = Class.objects.get(pk = jsonBody["classID"])
	except Class.DoesNotExist:
		return HttpResponse("No class")
	else:
		return JsonResponse({"foo":"bar"})