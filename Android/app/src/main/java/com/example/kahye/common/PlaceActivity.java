package com.example.kahye.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class PlaceActivity extends AppCompatActivity {

    ImageButton PlaceimgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        PlaceimgButton = (ImageButton) findViewById(R.id.cafeImgButton);

        PlaceimgButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent trendingClassActivity = new Intent(PlaceActivity.this,
                        TrendingClassActivity.class);
                startActivity(trendingClassActivity);
            }
        });
    }
}