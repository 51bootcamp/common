from django.contrib import admin

from .models import *

admin.site.register(User)
admin.site.register(Class)
admin.site.register(Reservation)
admin.site.register(TimeTable)
admin.site.register(Image)