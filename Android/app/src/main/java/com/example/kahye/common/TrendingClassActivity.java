package com.example.kahye.common;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kahye.common.models.ClassList;
import com.squareup.picasso.Picasso;

public class TrendingClassActivity extends AppCompatActivity {

    Adapter adapter;
    ViewPager classViewPager;
    DatePicker datePicker;
    ImageButton classButton;
    String selectedDate;

    Integer[] images = {R.drawable.coffee, R.drawable.cooking};
    String[] imagesURL = {};
    String[] classes = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        ClassList classList = bundle.getParcelable("_classList");
        selectedDate = bundle.getString("_date");

        //set-up for adapter
        Integer listSize = classList.getClassList().size();
        classes = new String[listSize];
        imagesURL = new String[listSize];

        //set class name
        for(int i = 0; i < listSize; i++){
            classes[i] = classList.getClassList().get(i).getClassName();
            imagesURL[i] = classList.getClassList().get(i).getCoverImage().get(0);
        }

        setContentView(R.layout.activity_trending_class);

        classViewPager = (ViewPager) findViewById(R.id.classViewPager);

        //initialize adapter
        adapter = new Adapter(this, classList, imagesURL, selectedDate);
        classViewPager.setAdapter((PagerAdapter) adapter);

        //for multiple images view
        classViewPager.setClipChildren(false);

        //calendar
        datePicker = (DatePicker)findViewById(R.id.datepicker);
        datePicker.init(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener(){

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth){
                        String msg = String.format("%d-%d-%d", year,
                                monthOfYear+1, dayOfMonth);
                        Toast.makeText(TrendingClassActivity.this, msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}