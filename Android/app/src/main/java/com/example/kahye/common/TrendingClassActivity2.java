package com.example.kahye.common;

import android.annotation.SuppressLint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kahye.common.models.ClassList;

public class TrendingClassActivity2 extends AppCompatActivity {

    Adapter adapter;
    ViewPager classViewPager;
    DatePicker datePicker;
    ImageButton classButton;
    String selectedDate;

    String[] imagesURL = {};
    String[] classes = {};
    String[] expertNameList = {};

    @SuppressLint("UseValueOf")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_class2);

        Bundle bundle = getIntent().getExtras();
        ClassList classList = bundle.getParcelable("_classList");
        selectedDate = bundle.getString("_date");

        //set-up for adapter
        Integer listSize = classList.getClassList().size();
        classes = new String[listSize];
        imagesURL = new String[listSize];
        expertNameList = new String[listSize];

        //set class name
        for(int i = 0; i < listSize; i++){
            classes[i] = classList.getClassList().get(i).getClassName();
            imagesURL[i] = classList.getClassList().get(i).getCoverImage()
                    .get(0);
            expertNameList[i] = classList.getClassList().get(i)
                    .getExpertName();
        }

        classViewPager = (ViewPager) findViewById(R.id.classViewPager);

        //initialize adapter
        adapter = new Adapter(this, classList, imagesURL,
                expertNameList, selectedDate);
        classViewPager.setAdapter((PagerAdapter) adapter);

        //for multiple images view
        classViewPager.setClipChildren(false);

        //calendar
        datePicker = (DatePicker)findViewById(R.id.datepicker);
        String[] dateTokens = selectedDate.split("-");

        datePicker.updateDate(new Integer(dateTokens[0]), new Integer
                        (dateTokens[1]) - 1, new Integer(dateTokens[2]));

        datePicker.init(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener(){

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth){
                        String msg = String.format("%d-%d-%d", year,
                                monthOfYear+1, dayOfMonth);
                        Toast.makeText(TrendingClassActivity2.this, msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}