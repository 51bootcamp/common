package com.example.kahye.common;

import org.json.simple.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostTest {
    public interface CommonService {
        @Headers("Content-Type: application/json")
        @POST("api/login/")
        Call<User> sendPost(@Body JSONObject userEmail);
    }

    class User {
        @Expose
        public String userEmail;
        @SerializedName("userName")
        @Expose
        public String userName;
        public String accountType;
        public boolean isLecturer;
    }

    public static void main(String[] args) {
        System.out.println("program start\n================================");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.8.187.167:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final JSONObject email = new JSONObject();
        email.put("userEmail","jmj@kookmin.ac.kr");

        CommonService service = retrofit.create(CommonService.class);
        Call<User> request = service.sendPost(email);

        request.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User rb = response.body();
                System.out.println("# Succeeded to receive Response");
                System.out.println("Call : "+call);
                System.out.println("Response : "+response);
                System.out.println("ResponseBody : "+rb);
                System.out.println("UserName : "+rb.userName);
                System.out.println("UserEmail : "+rb.userEmail);
                System.out.println("AccountType:  "+rb.accountType);
                System.out.println("isLecturer : "+rb.isLecturer);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("# Failed to receive Response");
                System.out.println("Cause : "+t.getCause());
                System.out.println("JSON : "+call);
            }
        });
        System.out.println("program finished\n");
    }
}