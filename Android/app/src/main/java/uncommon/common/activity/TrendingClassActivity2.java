package uncommon.common.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.facebook.login.LoginManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.adapter.Adapter;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.ClassList;
import uncommon.common.network.RetrofitInstance;

public class TrendingClassActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Adapter adapter;
    private ViewPager classViewPager;
    private DatePicker datePicker;
    private ImageButton classButton;
    private String selectedDate;
    private Context context = this;

    @SuppressLint("UseValueOf")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.drawer_trending2);

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

        Bundle bundle = getIntent().getExtras();
        ClassList classList = bundle.getParcelable("_classList");
        selectedDate = bundle.getString("_date");

        classViewPager = (ViewPager) findViewById(R.id.classViewPager);

        //initialize adapter
        adapter = new Adapter(this, classList, selectedDate);
        classViewPager.setAdapter((PagerAdapter) adapter);

        //for multiple images view
        classViewPager.setClipChildren(false);

        //calendar
        datePicker = (DatePicker)findViewById(R.id.datepicker);
        String[] dateTokens = selectedDate.split("-");

        datePicker.updateDate(new Integer(dateTokens[0]), new Integer
                        (dateTokens[1]) - 1, new Integer(dateTokens[2]));

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener(){
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth){
                        selectedDate = String.format("%d-%d-%d", year, monthOfYear+1, dayOfMonth);

                        ApiInterface service = RetrofitInstance.getRetrofitInstance()
                                .create(ApiInterface.class);

                        Call<ClassList> request = service.getClassList(selectedDate);
                        request.enqueue(new Callback<ClassList>() {
                            @Override
                            public void onResponse(Call<ClassList> call,
                                                   Response<ClassList> response)
                            {
                                ClassList selectedClassList = response.body();
                                adapter = new Adapter(context, selectedClassList, selectedDate);
                                classViewPager.setAdapter((PagerAdapter) adapter);
                            }

                            @Override
                            public void onFailure(Call<ClassList> call, Throwable t) {

                            }
                        });
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