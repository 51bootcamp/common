package uncommon.common.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import uncommon.common.R;
import uncommon.common.utils.AccountManager;
import uncommon.common.utils.FacebookLoginCallback;

public class MainActivity extends AppCompatActivity {

    private Context context = this;
    private static final String AUTH_TYPE = "rerequest";
    private String userName;
    private String userEmail;

    private AccountManager accountManager = new AccountManager(context);
    private CallbackManager callbackManager;
    private FacebookCallback LoginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (currentAccessToken != null) { requestMe(currentAccessToken); }

        LoginButton LoginButton = findViewById(R.id.facebook_login_button);
        // Set the initial permissions to request from the user while logging in
        LoginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        LoginButton.setAuthType(AUTH_TYPE);

        LoginCallback = new FacebookLoginCallback(context);
        LoginButton.registerCallback(callbackManager, LoginCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void click(View view) {
        Intent signupintent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(signupintent);
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