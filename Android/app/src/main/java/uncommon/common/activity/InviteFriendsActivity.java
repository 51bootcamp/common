package uncommon.common.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.facebook.login.LoginManager;
import org.json.simple.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.network.RetrofitInstance;

public class InviteFriendsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    EditText emailText;
    TextView sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        context = this;
        setContentView(R.layout.drawer_invite);

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
        
        emailText = (EditText) findViewById(R.id.email_text);
        sendButton = (TextView) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApiInterface service = RetrofitInstance.getLoginRetrofitInstance()
                        .create(ApiInterface.class);

                JSONObject requestBody = new JSONObject();
                requestBody.put("email", emailText.getText().toString());

                Call<Integer> call = service.sendMail(requestBody);
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {

                        Toast.makeText(getApplicationContext(),
                                "Send Email",Toast.LENGTH_LONG).show();
                        Intent sendEmailIntent = new Intent(InviteFriendsActivity.this,
                                PlaceActivity.class);
                        sendEmailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(sendEmailIntent);
                    }
                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
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