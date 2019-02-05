package uncommon.common.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.adapter.MyReservationAdapter;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Reservation;
import uncommon.common.models.ReservationList;
import uncommon.common.network.RetrofitInstance;
import uncommon.common.utils.ListDynamicViewUtil;

public class MyReservationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ApiInterface service = RetrofitInstance.getRetrofitInstance()
            .create(ApiInterface.class);
    private Context context;
    private List<Reservation> reservations;
    private List<Reservation> passedReservations;
    private ListView reservationListView;
    private ListView passedReservationListView;
    private ReservationList reservationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.drawer_myres);

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

        reservationListView = (ListView) findViewById(R.id.myReservationListView);
        passedReservationListView = (ListView) findViewById(R.id.passedMyReservationListView);

        // get data from server
        Call<ReservationList> request = service.getReservationList();
        request.enqueue(new Callback<ReservationList>() {
            @Override
            public void onResponse(Call<ReservationList> call, Response<ReservationList> response) {
                reservationList = response.body();
                reservations = new ArrayList<Reservation>();
                passedReservations = new ArrayList<Reservation>();
                int reservationListSize = reservationList.getReservationList().size();

                // if there is no reservation
                if(reservationListSize == 0){
                    reservationListView.setVisibility(View.INVISIBLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyReservationActivity.this);
                    // msg
                    TextView msg = new TextView(MyReservationActivity.this);
                    msg.setGravity(Gravity.LEFT);
                    msg.setLineSpacing(2,1);
                    msg.setText("There is no reservations.");
                    msg.setTextSize(20);
                    msg.setPadding(50, 60, 10, 10);
                    builder.setView(msg);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                     @Override
                     public void onClick(DialogInterface dialog, int which){
                        Intent placeIntent = new Intent(MyReservationActivity.this,
                                PlaceActivity.class);
                         placeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                 | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(placeIntent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                for(int reserveIdx = 0; reserveIdx < reservationListSize; reserveIdx++){
                    Reservation resposition = reservationList.getReservationList().get(reserveIdx);
                    if(resposition.getIsPassed()) {
                        passedReservations.add(resposition);
                    }
                    else{
                        reservations.add(resposition);
                    }
                }

                // Upcoming Class ReservationList
                MyReservationAdapter reservationAdapter = new MyReservationAdapter(context,
                        reservations);
                reservationListView.setAdapter(reservationAdapter);
                ListDynamicViewUtil.setListViewHeightBasedOnChildren(reservationListView);

                // Past ReservationList
                MyReservationAdapter passedReservationAdapter = new MyReservationAdapter(context,
                        passedReservations);
                passedReservationListView.setAdapter(passedReservationAdapter);
                ListDynamicViewUtil.setListViewHeightBasedOnChildren(passedReservationListView);
            }

            @Override
            public void onFailure(Call<ReservationList> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home: {
                Intent navIntent = new Intent(context, PlaceActivity.class);
                startActivity(navIntent);
                navIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                break;
            }
            case R. id.nav_myres: {
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
        if (RetrofitInstance.isLecturer) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_createClass).setVisible(false);
        }
        return;
    }
}
