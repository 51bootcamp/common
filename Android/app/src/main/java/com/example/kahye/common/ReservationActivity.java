package com.example.kahye.common;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // class Img
        Bundle bundle = this.getIntent().getExtras();
        int pic = bundle.getInt("classImg");
        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);
        classImgView.setImageResource(pic);

        // show present time
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = sdf.format(date);
        TextView dateView = (TextView) findViewById(R.id.dateView);
        dateView.setText(getTime);

        // time list
        ListView timeListView = (ListView) findViewById(R.id.timeListView);

        List<String> timeList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, timeList);

        timeListView.setAdapter(adapter);

        timeList.add("2:00PM ~ 4:00PM");
        timeList.add("4:00PM ~ 6:00PM");
        timeList.add("6:00PM ~ 8:00PM");
        timeList.add("8:00PM ~ 10:00PM");
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}