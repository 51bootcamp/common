from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse, JsonResponse
from api.models import * #import models

#for csrf_exempt, not sure what is csrf
from django.views.decorators.csrf import csrf_exempt

from rest_framework import status, viewsets
from rest_framework.decorators import api_view
from rest_framework.response import Response

#not know about what is sesrialization
from api.serializers import UserSerializer



import json

def check_http_mehtod(request, type):
	if request.type == type:
		return True
	else: 
		return False




def index(request):
	return HttpResponse("Hello woRld! you're at the polls index.")


@csrf_exempt
def signup(request):
	if request.method != 'POST':
		return HttpResponse("Bad Request", status = 400)

	t = json.loads(request.body)
	print(t)

	try:
		User.objects.get(pk = t['userEmail'])
	except User.DoesNotExist:
		u = User(userEmail = t['userEmail'], userName = t['userName'], thirdPartySignUp = int(t['thirdPartySignUp']))
		print(u.thirdPartySignUp)
		u.save()
		return HttpResponse("hi");
	else:
		return HttpResponse("duplication error", status = 300)

@csrf_exempt
def login(request):
	if request.method != 'POST':
		return HttpResponse("Bad Request", status = 400)

	t = json.loads(request.body)
	print(t)

	try:
		u = User.objects.get(pk = t['userEmail'])
	except User.DoesNotExist:
		return HttpResponse("PLz signup first", status = 300)
	else:
		#checking password routine in here, if we have our own signup feature
		return JsonResponse({'userEmail':u.userName})


def getClass(request):
	t = json.loads(request.body)
	try:

		s = TimeTable.objects.filter(date = t['date'], isAvailable = True).values("classID").distinct()
		s.distinct("classID")
		# s = Class.objects.filter(TimeTable__date = t['date'], TimeTable__isAvailable = True)
		# s = T
		print(s)
		# c = Class.objects.get(pk = s.)

	except (TimeTable.objects.DoesNotExist, Class.objects.DoesNotExist) as e:
		return HttpResponse("No class")
	else:
		return JsonResponse({'foo':'bars'})
	# return HttpResponse("hi")









