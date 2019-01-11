from django.db import models

class User(models.Model):
	userEmail = models.CharField(max_length = 40, primary_key = True)
	userName = models.CharField(max_length = 40)
	accountType = models.CharField(max_length = 40, default = "FACEBOOK")
	isLecturer = models.BooleanField(default = False)

class Class(models.Model):
	classID = models.IntegerField(primary_key = True)
	className = models.CharField(max_length = 40)
	minGuestCount = models.IntegerField(default = 4)
	maxGuestCount = models.IntegerField(default = 8)
	price = models.FloatField()
	expertEmail = models.ForeignKey(User, on_delete = models.CASCADE)

class TimeTable(models.Model):
	timeTableIdx = models.AutoField(primary_key = True)
	date = models.DateField(auto_now = False)
	startTime = models.IntegerField()
	endTime = models.IntegerField()
	timezone = models.CharField(max_length = 40, 
								default = "America/Los_Angeles")
	classID = models.ForeignKey(Class, on_delete = models.CASCADE)
	isBooked = models.BooleanField(default = False)

class Reservation(models.Model):
	reservationID = models.AutoField(primary_key = True)
	guestCount = models.IntegerField()
	userEmail = models.ForeignKey(User, on_delete = models.CASCADE)
	timeTableIdx = models.ForeignKey(TimeTable, on_delete = models.CASCADE)