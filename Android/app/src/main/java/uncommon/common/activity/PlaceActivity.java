package uncommon.common.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.ClassList;
import uncommon.common.models.Reservation;
import uncommon.common.network.RetrofitInstance;

public class PlaceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton placeimgButton;
    private TextView resNotificationTextView;
    private TextView placeTextView;
    private String selectedDate;
    private Integer reservationID;
    private ImageView peopleImgView;

    private ApiInterface service = RetrofitInstance.getRetrofitInstance()
            .create(ApiInterface.class);

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_place);

        hideItem();

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Navigation header info
        navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2,
                                       int i3, int i4, int i5, int i6, int i7) {

                navigationView.removeOnLayoutChangeListener( this );

                TextView nav_name = (TextView) navigationView.findViewById(R.id.nav_header_name);
                nav_name.setText(RetrofitInstance.username);

                ImageView header_image = (ImageView) findViewById(R.id.nav_haeader_image);

                Picasso.get().load( "https://graph.facebook.com/" +
                        AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large")
                        .transform(new CropCircleTransformation())
                        .into(header_image);
            }
        });

        //Reservation Notification
        resNotificationTextView = (TextView) this.findViewById(R.id.resNotificationText);
        peopleImgView = (ImageView) this.findViewById(R.id.peopleImgView);
        placeimgButton = (ImageButton) findViewById(R.id.cafeImgButton);
        placeTextView = (TextView) findViewById(R.id.placeTextView);

        placeimgButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

        Call<Reservation> request = service.getUpcoming();
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

                    reservationID = res.getReservationID();
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
    }

    public void click(View view) {
        Intent confirmResIntent = new Intent(PlaceActivity.this,
                ConfirmReservationActivity.class);

        Bundle bundle = new Bundle();
        confirmResIntent.putExtra("_reservationID", reservationID);
        confirmResIntent.putExtras(bundle);

        startActivity(confirmResIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home: {
                break;
            }
            case R. id.nav_myres: {
                Intent navIntent = new Intent(context, MyReservationActivity.class);
                startActivity(navIntent);
                break;
            }
            case R. id.nav_createClass: {
                Intent navIntent = new Intent(context, MakeClassActivity.class);
                startActivity(navIntent);
                break;
            }
            case R. id.nav_invite: {
                Intent navIntent = new Intent(context, InviteFriendsActivity.class);
                startActivity(navIntent);
                break;
            }
            case R. id.nav_about: {
                Intent navIntent = new Intent(context, AboutActivity.class);
                startActivity(navIntent);
                break;
            }
            case R. id.nav_setting: {
                break;
            }
            case R. id.nav_logout: {
                LoginManager.getInstance().logOut();
                Intent navIntent = new Intent(context, InviteOnlyActivity.class);
                navIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(navIntent);
                finish();
                break;
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideItem() {
        if (!RetrofitInstance.isLecturer) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_createClass).setVisible(false);
        }
        return;
    }
}
