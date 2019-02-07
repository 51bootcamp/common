package uncommon.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import uncommon.common.R;
import uncommon.common.utils.AccountManager;


public class SplashActivity<SPLASH_TIME_OUT> extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private String userName;
    private String userEmail;
    private AccountManager accountManager = new AccountManager(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
                if (currentAccessToken != null) { requestMe(currentAccessToken); }
                else{
                    Intent InviteOnlyIntent = new Intent(SplashActivity.this, InviteOnlyActivity.class);
                    startActivity(InviteOnlyIntent);
                    finish();
                }
            }
        },1200);

    }

    public void requestMe(AccessToken token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            userName = object.get("name").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            userEmail = object.get("email").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        accountManager.Login(userEmail, userName);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}