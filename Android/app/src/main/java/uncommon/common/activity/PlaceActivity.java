package uncommon.common.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.ClassList;
import uncommon.common.models.Reservation;
import uncommon.common.network.RetrofitInstance;

public class PlaceActivity extends AppCompatActivity {

    ImageButton placeimgButton;
    TextView resNotificationTextView;
    TextView placeTextView;
    String selectedClass;
    String selectedDate;
    ImageView peopleImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        //Reservation Notification
        resNotificationTextView = (TextView) this.findViewById(R.id.resNotificationText);
        peopleImgView = (ImageView) this.findViewById(R.id.peopleImgView);
        placeimgButton = (ImageButton) findViewById(R.id.cafeImgButton);
        placeTextView = (TextView) findViewById(R.id.placeTextView);

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

        ApiInterface service = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Reservation> request = service.getReservation();
        request.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                //upcoming notification
                if (response.body().isValid()) {
                    Reservation res = response.body();

                    //convert date format yyyy-MM-dd into dd-MMM-yyyy
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                    String inputDateStr= res.getDate();
                    Date date = null;
                    try {
                        date = inputFormat.parse(inputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(date);

                    resNotificationTextView.setText(outputDateStr + " · " + res.getClassName() +
                            " · " + res.getExpertName());
                    resNotificationTextView.setSelected(true);
                }
                //no reservation
                else {
                    peopleImgView.setVisibility(View.GONE);
                    resNotificationTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                //TODO (kahye) : error handling
            }
        });

        //TODO(gayeon) : change button position to navigation bar
        Button reviewButton = (Button) findViewById(R.id.reviewbutton);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(PlaceActivity.this, MyReservationActivity.class);
                startActivity(reviewIntent);
            }
        });
    }

    public void click(View view) {
        Intent confirmResIntent = new Intent(PlaceActivity.this,
                ConfirmReservationActivity.class);

        Bundle bundle = new Bundle();
        confirmResIntent.putExtra("_classInfo", selectedClass);
        confirmResIntent.putExtra("_date", selectedDate);
        confirmResIntent.putExtras(bundle);

        startActivity(confirmResIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        return true;
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
