package uncommon.common.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.ClassList;
import uncommon.common.network.RetrofitInstance;

public class PlaceActivity extends AppCompatActivity {

    ImageButton placeimgButton;
    TextView placeTextView;
    String selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        placeimgButton = (ImageButton) findViewById(R.id.cafeImgButton);
        placeTextView = (TextView)findViewById(R.id.placeTextView);

        placeTextView.setBackgroundColor(Color.parseColor("#9931343a"));

        placeimgButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ApiInterface service = RetrofitInstance.getRetrofitInstance()
                        .create(ApiInterface.class);

                //get current date
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                selectedDate = sdf.format(date);

                Call<ClassList> request = service.getClassList(selectedDate);

                request.enqueue(new Callback<ClassList>() {
                    @Override
                    public void onResponse(Call<ClassList> call, Response<ClassList> response) {
                        ClassList classList = response.body();

                        Intent trendingClassActivity = new Intent(PlaceActivity.this,
                                TrendingClassActivity.class);

                        trendingClassActivity.putExtra("_classList", classList);
                        trendingClassActivity.putExtra("_date", selectedDate);

                        startActivity(trendingClassActivity);
                    }

                    @Override
                    public void onFailure(Call<ClassList> call, Throwable t) {
                        //TODO (woongjin) : how to deal with failure
                    }
                });
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);

        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LoginManager.getInstance().logOut();

        Intent mainIntent = new Intent(PlaceActivity.this, MainActivity.class);
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
