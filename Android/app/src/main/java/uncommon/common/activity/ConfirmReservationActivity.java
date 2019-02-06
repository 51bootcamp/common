package uncommon.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

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
import uncommon.common.models.Reservation;
import uncommon.common.network.RetrofitInstance;

public class ConfirmReservationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button reviewButton;
    private ImageView classImgView;
    private Integer reservationID;
    private TextView classTextView;
    private TextView expertTextView;
    private TextView resDateTextView;
    private TextView usdTextView;
    private TextView resTimeTextView;
    private TextView resUserEmailTextInfo;
    private Integer classID;
    private Context context = this;

    String base_image_url = "http://52.8.187.167:8000";
    ImageView facebookProfileView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.drawer_confirm);

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

        // Logo button to home
        ImageButton logoButton = (ImageButton) findViewById(R.id.common_logo);
        logoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent logoIntent = new Intent(context, PlaceActivity.class);
                logoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoIntent);
                finish();
                return;
            }
        });

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

        Bundle bundle = this.getIntent().getExtras();

        classImgView = (ImageView) this.findViewById(R.id.classImgView);
        classTextView = (TextView) this.findViewById(R.id.classTextView);
        expertTextView = (TextView) this.findViewById(R.id.expertTextView);
        resDateTextView = (TextView) this.findViewById(R.id.resDateTextView);
        resTimeTextView = (TextView) this.findViewById(R.id.resTimeTextView);
        resUserEmailTextInfo = (TextView) this.findViewById(R.id.resUserEmailTextInfo);
        reviewButton = (Button) this.findViewById(R.id.reviewButton);
        usdTextView = (TextView) this.findViewById(R.id.usdTextView);
        facebookProfileView = (ImageView) this.findViewById(R.id.facebookprofile);

        Picasso.get().load( "https://graph.facebook.com/" +
                AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large")
                .transform(new CropCircleTransformation())
                .into(facebookProfileView);

        // class Img
        reservationID = bundle.getInt("_reservationID");

        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);

        ApiInterface service = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Reservation> request = service.getReservation(reservationID);
        request.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {

                Reservation res = response.body();

                classTextView.setText(res.getClassName());
                expertTextView.setText(res.getExpertName());
                resTimeTextView.setText(res.getStartTime()+ " - " + res.getEndTime());
                resUserEmailTextInfo.setText(res.getUserEmail());
                usdTextView.setText("$ " + res.getTotalResPrice() * res.getGuestCount());
                Picasso.get()
                        .load(base_image_url + res.getCoverImg())
                        .fit()
                        .into(classImgView);

                //convert date format yyyy-MM-dd into E, MMM dd,  yyyy
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("MMM dd, E, yyyy");
                String inputDateStr= res.getDate();
                Date date = null;
                try {
                    date = inputFormat.parse(inputDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputDateStr = outputFormat.format(date);
                resDateTextView.setText(outputDateStr);
                classID = res.getClassId();
                if(!res.getIsPassed()){
                    reviewButton.setVisibility(View.GONE);
                }
                if(res.getIsReviewed()){
                    reviewButton.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                //TODO (kahye)
            }
        });

        //go to ReviewActivity
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(ConfirmReservationActivity.this, ReviewActivity
                        .class);
                Bundle bundle = new Bundle();
                reviewIntent.putExtra("_classID", classID);
                reviewIntent.putExtra("_reservationID", reservationID);
                reviewIntent.putExtras(bundle);
                startActivity(reviewIntent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home: {
                Intent navIntent = new Intent(context, PlaceActivity.class);
                navIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(navIntent);
                finish();
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