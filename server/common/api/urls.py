from django.urls import path
from django.conf.urls import url, include
from django.contrib.auth.models import User
from rest_framework import routers, serializers, viewsets
from rest_framework_swagger.views import get_swagger_view

from . import views

schema_view = get_swagger_view(title='Common API')

urlpatterns = [

	path('', views.index, name='index'),
    path('signup/', views.signup, name = 'signup'),
    path('login/', views.login, name = 'login'),
    path('class/<str:date>/', views.getClassList, name ='getClassList'),
    path('class/<str:date>/<int:classID>', views.getClassInfo, 
    	name = 'getClassInfo'),
    path('reserve/', views.makeReservation, name = 'makeReservation'),
    path('reserve/<str:userEmail>', views.getReservation, 
    	name = 'getReservation'),
    path('upload/', views.imageUpload, name = 'imageUpload'),
    url(r'^api-auth/', include('rest_framework.urls')),
    url(r'^docs/', schema_view)

]