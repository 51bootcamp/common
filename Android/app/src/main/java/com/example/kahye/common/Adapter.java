package com.example.kahye.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kahye.common.api_interface.ApiInterface;
import com.example.kahye.common.models.Class;
import com.example.kahye.common.models.ClassList;
import com.example.kahye.common.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter extends PagerAdapter {
    private ClassList classList;
    private String selectedDate;
    private Context context;
    private LayoutInflater inflater;
    private Class selectedClass;

    ImageButton classButton;

    //TODO(woongjin) get images from server
    private Integer [] images = {R.drawable.coffee, R.drawable.cooking};
    private String [] classes = {};

    public Adapter(Context context, ClassList classList, String selectedDate) {
        this.context = context;
        this.classList = classList;
        this.selectedDate = selectedDate;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {  //setting the image ratio of screen
        return (0.7f);
    }

    @Override
    public Object instantiateItem(final ViewGroup container,
                                  final int position) {
        Integer listSize = classList.getClassList().size();
        classes = new String[listSize];

        //set class name
        for(int i = 0; i < listSize; i++){
            classes[i] = classList.getClassList().get(i).getClassName();
        }

        inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.class_viewpager, null);
        classButton = (ImageButton) view.findViewById(R.id.classButton);
        classButton.setImageResource(images[position]);


        classButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent reserveIntent = new Intent(
                        context, ReservationActivity.class);
                ApiInterface service = RetrofitInstance.getRetrofitInstance()
                        .create(ApiInterface.class);
                //TODO(woonjin): get the today's date and change the arguments
                Call<Class> request = service.getClassInfo("2019-01-07",
                        classList.getClassList().get(position).getClassID());
                request.enqueue(new Callback<Class>() {
                    @Override
                    public void onResponse(Call<Class> call, Response<Class> response) {
                        Context context = container.getContext();
                        selectedClass = response.body();

                        Bundle bundle = new Bundle();
                        bundle.putInt("classImg", images[position]);
                        reserveIntent.putExtra("_classInfo", selectedClass);
                        reserveIntent.putExtra("_date", selectedDate);
                        reserveIntent.putExtras(bundle);

                        context.startActivity(reserveIntent);
                    }

                    @Override
                    public void onFailure(Call<Class> call, Throwable t) {
                        //TODO (woongjin) : how to deal with failure
                    }
                });

            }
        });

        TextView classTextView = view.findViewById(R.id.classTextView);
        classTextView.setText(classes[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}