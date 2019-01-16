package com.example.kahye.common.api_interface;

import com.example.kahye.common.models._class;
import com.example.kahye.common.models._classList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface apiInterface {
    @GET("class/{date}/")
    Call<_classList> getClassList(@Path("date") String date);
    @GET("class/{date}/{classID}")
    Call<_class> getClassInfo(@Path("date") String date,
                              @Path("classID") Integer classID);

}
