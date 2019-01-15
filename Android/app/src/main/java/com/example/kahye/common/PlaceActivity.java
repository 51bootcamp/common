package com.example.kahye.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

public class PlaceActivity extends AppCompatActivity {

    ImageButton PlaceimgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        PlaceimgButton = (ImageButton) findViewById(R.id.cafeImgButton);
        LoginButton LoginButton = findViewById(R.id.facebook_login_button);

        PlaceimgButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(PlaceActivity.this, "It works",
                        Toast.LENGTH_LONG).show();
                Intent trendingClassActivity = new Intent(PlaceActivity.this,
                        TrendingClassActivity.class);
                startActivity(trendingClassActivity);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();

                Intent mainIntent = new Intent(PlaceActivity.this,
                        MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
