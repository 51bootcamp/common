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

public class Adapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    ImageButton classButton;

    //TODO (gayeon) : data should be supplied from server
    private Integer [] images = {R.drawable.coffee, R.drawable.cooking,
            R.drawable.wine};
    private String [] classes = {"Coffee", "Cooking", "Wine tasting"};

    public Adapter(Context context) {
        this.context = context;
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

        inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.class_viewpager, null);
        classButton = (ImageButton) view.findViewById(R.id.classButton);
        classButton.setImageResource(images[position]);

        classButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = container.getContext();
                Intent reserveIntent = new Intent(
                        context, ReservationActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("classImg", images[position]);
                reserveIntent.putExtras(bundle);
                context.startActivity(reserveIntent);
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