from django.urls import path
# from django.conf.urls import patterns, url
from django.conf.urls import url, include
from django.contrib.auth.models import User
from rest_framework import routers, serializers, viewsets
from rest_framework_swagger.views import get_swagger_view

from . import views

schema_view = get_swagger_view(title='Pastebin API')

urlpatterns = [

	path('', views.index, name='index'),
	# ex: /polls/atmasdf@gmail.com
    path('signup/', views.signup, name = 'signup'),
    path('login/', views.login, name = 'login'),
    path('class/date', views.getClass, name ='getClass'),
	# path('<str:userEmail>/', views.detail, name = 'detail'),
    url(r'^api-auth/', include('rest_framework.urls')),
    url(r'^docs/', schema_view)

]