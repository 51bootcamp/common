from django.db import models

class User(models.Model):
	userEmail = models.CharField(max_length = 40, primary_key = True)
	userName = models.CharField(max_length = 40)
	accountType = models.CharField(max_length = 40, default = "FACEBOOK")
	isLecturer = models.BooleanField(default = False)

	def __str__(self):
		return self.userName

class Class(models.Model):
	className = models.CharField(max_length = 40)
	minPersonnel = models.IntegerField(default = 4)
	maxPersonnel = models.IntegerField(default = 8)
	price = models.FloatField()
	userEmail = models.ForeignKey(User, on_delete = models.CASCADE)

	def __str__(self):
		return self.className

class TimeTable(models.Model):
	timeTableIdx = models.AutoField(primary_key = True)
	date = models.DateField(auto_now = False)
	startTime = models.IntegerField()
	endTime = models.IntegerField()
	timezone = models.IntegerField(default = -8) # san-francisco's timezone
	isBooked = models.BooleanField(default = False)
	classID = models.ForeignKey(Class, on_delete = models.CASCADE)

class Reservation(models.Model):
	resrvationID = models.AutoField(primary_key = True)
	numOfReserve = models.IntegerField()
	userEmail = models.ForeignKey(User, on_delete = models.CASCADE)
	timeTableIdx = models.ForeignKey(TimeTable, on_delete = models.CASCADE)


