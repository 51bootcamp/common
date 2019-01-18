package com.example.kahye.common;

import android.content.Intent;
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

public class TrendingClassActivity extends AppCompatActivity {

    Adapter adapter;
    ViewPager classViewPager;
    DatePicker datePicker;
    ImageButton classButton;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        ClassList classList = bundle.getParcelable("_classList");
        selectedDate = bundle.getString("_date");

        //set-up for adapter
        Integer listSize = classList.getClassList().size();

        setContentView(R.layout.activity_trending_class);

        classViewPager = (ViewPager) findViewById(R.id.classViewPager);

        //initialize adapter
        adapter = new Adapter(this, classList, selectedDate);
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
                        final String msg = String.format("%d-%d-%d", year,
                                monthOfYear+1, dayOfMonth);
                        final Intent trendingIntent2 = new Intent(
                                TrendingClassActivity.this,
                                TrendingClassActivity2.class);

                        ApiInterface service = RetrofitInstance
                                .getRetrofitInstance()
                                .create(ApiInterface.class);
                        Call<ClassList> request = service.getClassList(msg);
                        request.enqueue(new Callback<ClassList>() {
                            @Override
                            public void onResponse(Call<ClassList> call,
                                                   Response<ClassList>
                                                           response){
                                ClassList selectedClassList = response.body();
                                trendingIntent2.putExtra("_classList",
                                        selectedClassList);
                                trendingIntent2.putExtra("_date", msg);
                                startActivity(trendingIntent2);
                            }

                            @Override
                            public void onFailure(Call<ClassList> call,
                                                  Throwable t) {
                                //TODO (woongjin) error handling
                            }
                        });
                    }
                });
    }
}