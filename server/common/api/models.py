#-*- coding: utf-8 -*-
from django.core.validators import MaxValueValidator, MinValueValidator
from django.db import models
from django.contrib.auth.models import AbstractUser
from django.contrib.auth.validators import UnicodeUsernameValidator
from django.utils.translation import gettext as _


class MyValidator(UnicodeUsernameValidator):
	regex = r'^[\w.@+\- ]+$'

class User(AbstractUser):
	username_validator = MyValidator()
	username = models.CharField(
		_('username'),
		max_length=150,
		unique=True,
		help_text=_('Required. 150 characters or fewer.'),
		validators=[username_validator],
			error_messages={
			'unique': _("A user with that username already exists."),
		},
	)
	accountType = models.CharField(max_length = 40, default = "FACEBOOK")
	isLecturer = models.BooleanField(default = False)


class Class(models.Model):
	classID = models.AutoField(primary_key = True)
	className = models.CharField(max_length = 40)
	minGuestCount = models.IntegerField(default = 4)
	maxGuestCount = models.IntegerField(default = 8)
	price = models.FloatField()
	classRating = models.FloatField(default = 0.0,
					validators = [MaxValueValidator(5.0),
					MinValueValidator(0.0)])
	RatingCount = models.IntegerField(default=0)
	expert = models.ForeignKey(User, on_delete = models.CASCADE)

	def __str__(self):
		return self.className

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
	user = models.ForeignKey(User, on_delete = models.CASCADE)
	timeTableIdx = models.ForeignKey(TimeTable, on_delete = models.CASCADE)

class Image(models.Model):
	imageIdx = models.AutoField(primary_key = True)
	coverImage = models.ImageField(blank = True, null = True)

	CLASSIMAGE = 1
	PLACEIMAGE = 2
	IMAGE_CHOICES = (
		(CLASSIMAGE, 'classImage'),
		(PLACEIMAGE, 'placeImage'),
	)

	ImageType = models.IntegerField(default=1, choices=IMAGE_CHOICES)
	classID = models.ForeignKey(Class, on_delete = models.CASCADE)

class InviteCode (models.Model):
	inviteCodeID = models.AutoField(primary_key = True)
	randomCode = models.CharField(max_length = 40)
	isExpired = models.BooleanField(default = False)

class Review(models.Model):
    reviewIdx = models.AutoField(primary_key = True)
    title = models.CharField(max_length = 40)
    content = models.CharField(max_length = 500)
    rating = models.FloatField()
    createdDate = models.DateField(auto_now = True)
    classID = models.ForeignKey(Class, on_delete = models.CASCADE)
    userID = models.ForeignKey(User, on_delete = models.CASCADE)

    def __str__(self):
        return self.title

class InviteCode(models.Model):
    inviteCodeID = models.AutoField(primary_key = True)
    randomCode = models.CharField(max_length = 40)
    isExpired = models.BooleanField(default = False)
