package com.example.kahye.common;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kahye.common.api_interface.ApiInterface;
import com.example.kahye.common.models.ClassList;
import com.example.kahye.common.network.RetrofitInstance;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceActivity extends AppCompatActivity {

    ImageButton placeimgButton;
    TextView placeTextView;
    String selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        placeimgButton = (ImageButton) findViewById(R.id.cafeImgButton);
        placeTextView = (TextView)findViewById(R.id.placeTextView);
        LoginButton LoginButton = findViewById(R.id.facebook_login_button);

        //TODO (gayeon): change text on Image gradation
        placeTextView.setBackgroundColor(Color.parseColor(
                "#9931343a"));

        placeimgButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ApiInterface service = RetrofitInstance.getRetrofitInstance()
                        .create(ApiInterface.class);

                //get current date
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd");
                selectedDate = sdf.format(date);

                Call<ClassList> request = service.getClassList(selectedDate);

                request.enqueue(new Callback<ClassList>() {
                    @Override
                    public void onResponse(Call<ClassList> call,
                                           Response<ClassList> response) {
                        ClassList classList = response.body();

                        Intent trendingClassActivity = new Intent(
                                PlaceActivity.this,
                                TrendingClassActivity.class);

                        trendingClassActivity.putExtra("_classList",
                                classList);
                        trendingClassActivity.putExtra("_date",
                                selectedDate);

                        startActivity(trendingClassActivity);
                    }

                    @Override
                    public void onFailure(Call<ClassList> call, Throwable t) {
                        //TODO (woongjin) : how to deal with failure
                    }
                });
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();

                Intent mainIntent = new Intent(
                        PlaceActivity.this,
                        MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
