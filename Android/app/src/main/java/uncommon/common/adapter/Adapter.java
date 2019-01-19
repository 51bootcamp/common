package uncommon.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uncommon.common.R;
import uncommon.common.activity.ReservationActivity;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Class;
import uncommon.common.models.ClassList;
import uncommon.common.network.RetrofitInstance;

public class Adapter extends PagerAdapter {
    private ClassList classList;
    private Context context;
    private String selectedDate;
    private LayoutInflater inflater;
    private Class selectedClass;
    //Todo(woongjin) change the hardcoded url to read config file and use it
    private String baseImgUrl= "http://52.8.187.167:8000";

    ImageButton classButton;
    TextView classTextView;
    TextView expertTextView;

    public Adapter(Context context, ClassList classList, String selectedDate) {
        this.context = context;
        this.classList = classList;
        this.selectedDate = selectedDate;
    }

    @Override
    public int getCount() { return classList.getClassList().size(); }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }

    @Override
    public float getPageWidth(int position) {  //setting the image ratio of screen
        return (0.7f);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        Class positionClass = classList.getClassList().get(position);
        final String ImgURL = baseImgUrl + positionClass.getCoverImage().get(0);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.class_viewpager, null);
        classButton = (ImageButton) view.findViewById(R.id.classButton);

        Picasso.get().load(ImgURL).resize(2048, 1600).onlyScaleDown().into(classButton);

        classButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent reserveIntent = new Intent(context, ReservationActivity.class);
                ApiInterface service = RetrofitInstance.getRetrofitInstance()
                        .create(ApiInterface.class);
                Integer selectedClassID = classList.getClassList().get(position).getClassID();
                Call<Class> request = service.getClassInfo(selectedDate, selectedClassID);
                request.enqueue(new Callback<Class>() {
                    @Override
                    public void onResponse(Call<Class> call, Response<Class> response) {
                        Context context = container.getContext();
                        selectedClass = response.body();

                        Bundle bundle = new Bundle();
                        bundle.putString("classImgURL", ImgURL);
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

        // put data on class Img
        classTextView = view.findViewById(R.id.classTextView);
        classTextView.setText(positionClass.getClassName());
        classTextView.setBackgroundColor(Color.parseColor("#9931343a"));

        expertTextView = view.findViewById(R.id.expertTextView);
        expertTextView.setText(positionClass.getExpertName());
        expertTextView.setBackgroundColor(Color.parseColor("#9931343a"));

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