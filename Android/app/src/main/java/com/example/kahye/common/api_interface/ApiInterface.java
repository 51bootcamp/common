package com.example.kahye.common.api_interface;

import com.example.kahye.common.models.Class;
import com.example.kahye.common.models.ClassList;
import com.example.kahye.common.models.Reservation;
import com.example.kahye.common.models.User;

import org.json.simple.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("class/{date}/")
    Call<ClassList> getClassList(@Path("date") String date);

    @GET("class/{date}/{classID}")
    Call<Class> getClassInfo(@Path("date") String date,
                             @Path("classID") Integer classID);

    @POST("login/")
    Call<User> login(@Body JSONObject userEmail);

    @POST("signup/")
    Call<Void> signup(@Body User user);

    @POST("reserve/")
    Call<Reservation> makeReservation(@Body Reservation reservation);

    @GET("reserve/{userEmail}/")
    Call<Reservation> getReservation(@Path("userEmail") String userEmail);
}