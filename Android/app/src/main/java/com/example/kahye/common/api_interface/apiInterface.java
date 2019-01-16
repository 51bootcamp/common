package com.example.kahye.common.api_interface;

import com.example.kahye.common.models.Class;
import com.example.kahye.common.models.ClassList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface ApiInterface {
    @GET("class/{date}/")
    Call<ClassList> getClassList(@Path("date") String date);
    @GET("class/{date}/{classID}")
    Call<Class> getClassInfo(@Path("date") String date,
                             @Path("classID") Integer classID);

}
