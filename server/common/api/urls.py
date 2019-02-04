from django.urls import path
from django.conf.urls import url, include
from django.contrib.auth.models import User
from rest_framework import routers, serializers, viewsets
from rest_framework_swagger.views import get_swagger_view
from rest_framework_jwt.views import obtain_jwt_token

from . import views

schema_view = get_swagger_view(title='Common API')

urlpatterns = [
	path('', views.index, name='index'),
    path('class/<str:date>', views.getClassList, name ='getClassList'),
    path('class/<str:date>/<int:classID>', views.getClassInfo, 
    	name = 'getClassInfo'),
    path('makeClass/', views.makeClass, name = 'makeClass'),
    path('invite/', views.getInviteCode, name = 'getInviteCode'),
    path('upload/', views.imageUpload, name = 'imageUpload'),
    path('login/', views.login, name = 'login'),
    path('invite/<str:inviteCode>', views.getInviteCode, name = 'getInviteCode'),
    path('review/', views.writeReview, name = 'writeReview'),
    path('review/<int:classID>', views.getReviewList, name = 'getReviewList'),
    path('reserve/', views.reservationView.as_view(), name = 'reservation'),
    path('reserve/<int:reservationID>', views.getReservationView.as_view(), 
        name = 'getReserve'),
    path('reserve/upcoming', views.upcomingView.as_view(), name = 'upcoming'),
    path('reserveList', views.getReservationList.as_view(), 
        name = 'getReservationList'),
    path('signup/', views.signup, name = 'signup'),
    path('upload/', views.imageUpload, name = 'imageUpload'),
    url(r'^api-auth/', include('rest_framework.urls')),

]