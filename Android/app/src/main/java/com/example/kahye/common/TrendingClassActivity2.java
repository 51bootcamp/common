package com.example.kahye.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kahye.common.api_interface.ApiInterface;
import com.example.kahye.common.models.ClassList;
import com.example.kahye.common.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendingClassActivity2 extends AppCompatActivity {

    Adapter adapter;
    ViewPager classViewPager;
    DatePicker datePicker;
    ImageButton classButton;
    String selectedDate;
    Context context;

    @SuppressLint("UseValueOf")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_class2);

        Bundle bundle = getIntent().getExtras();
        ClassList classList = bundle.getParcelable("_classList");
        selectedDate = bundle.getString("_date");

        classViewPager = (ViewPager) findViewById(R.id.classViewPager);

        //initialize adapter
        adapter = new Adapter(this, classList, selectedDate);
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
                        final String msg = String.format("%d-%d-%d", year,
                                monthOfYear+1, dayOfMonth);

                        ApiInterface service = RetrofitInstance
                                .getRetrofitInstance()
                                .create(ApiInterface.class);

                        Call<ClassList> request = service.getClassList(msg);
                        request.enqueue(new Callback<ClassList>() {
                            @Override
                            public void onResponse(Call<ClassList> call,
                                                   Response<ClassList> response)
                            {
                                ClassList selectedClassList = response.body();
                                adapter = new Adapter
                                        (context, selectedClassList, msg);
                                classViewPager.setAdapter(
                                        (PagerAdapter) adapter);
                            }

                            @Override
                            public void onFailure(Call<ClassList> call,
                                                  Throwable t) {

                            }
                        });
                    }
                });
    }

}