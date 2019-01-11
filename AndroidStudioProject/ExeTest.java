package com.example.owner.myapplication;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class ExeTest {
    public interface GitHubService {
        @POST("api/login/{Email}")
        Call<JSONArray> getUserName();
                //@Path("Email") String userEmail);
    }

    public static void main(String[] args){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.8.187.167/")
                //.addConverterFactory()
                .build();

        GitHubService service = retrofit.create(GitHubService.class);
        Call<JSONArray> request = service.getUserName(/*"jmj@kookmin.ac.kr"*/);
        request.enqueue(new Callback<JSONArray>() {
            @Override
            public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
                System.out.println(call);
                System.out.println(response);
            }

            @Override
            public void onFailure(Call<JSONArray> call, Throwable throwable) {

            }
        });
    }

}
