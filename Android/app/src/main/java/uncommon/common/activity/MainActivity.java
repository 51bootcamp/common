package uncommon.common.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import uncommon.common.R;
import uncommon.common.utils.FacebookLoginCallback;

public class MainActivity extends AppCompatActivity {

    private Context context = this;
    private static final String AUTH_TYPE = "rerequest";
    private CallbackManager callbackManager;
    private FacebookCallback LoginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

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
}