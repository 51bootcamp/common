package uncommon.common.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.facebook.login.LoginManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uncommon.common.adapter.Adapter;
import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.ClassList;
import uncommon.common.network.RetrofitInstance;

public class TrendingClassActivity extends AppCompatActivity {

    Adapter adapter;
    ViewPager classViewPager;
    DatePicker datePicker;
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
                        selectedDate = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                        final Intent trendingIntent2 = new Intent(TrendingClassActivity.this,
                                TrendingClassActivity2.class);

                        ApiInterface service = RetrofitInstance.getRetrofitInstance()
                                .create(ApiInterface.class);
                        Call<ClassList> request = service.getClassList(selectedDate);
                        request.enqueue(new Callback<ClassList>() {
                            @Override
                            public void onResponse(Call<ClassList> call,
                                                   Response<ClassList> response){
                                ClassList selectedClassList = response.body();
                                trendingIntent2.putExtra("_classList", selectedClassList);
                                trendingIntent2.putExtra("_date", selectedDate);
                                startActivity(trendingIntent2);
                            }

                            @Override
                            public void onFailure(Call<ClassList> call, Throwable t) {
                                //TODO (woongjin) error handling
                            }
                        });
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu) ;

        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LoginManager.getInstance().logOut();

        Intent mainIntent = new Intent(TrendingClassActivity.this,
                MainActivity.class);
        startActivity(mainIntent);
        finish();
        /*
        switch (item.getItemId()) {
            case R.id.action_settings :
                // TODO (kahye) : process the click event for action_search item.
                //when we need another actionbar item
                return true ;
            default :
                return super.onOptionsItemSelected(MenuItem ) ;
        }*/
        return true;
    }

}