package uncommon.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class SignupActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signup);

        callbackManager = CallbackManager.Factory.create();

        if (AccessToken.getCurrentAccessToken() != null) {
            Intent loginIntent = new Intent(SignupActivity.this,
                    PlaceActivity.class);
            startActivity(loginIntent);
        }

        LoginButton LoginButton = findViewById(R.id.facebook_login_button);
        // Set the initial permissions to request from the user while logging in
        LoginButton.setReadPermissions(Arrays.asList(EMAIL));
        LoginButton.setAuthType(AUTH_TYPE);

        // Register a callback to respond to the user
        LoginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        setResult(RESULT_OK);
                        Intent placeIntent = new Intent(SignupActivity.this, PlaceActivity.class);
                        startActivity(placeIntent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        setResult(RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void onError(FacebookException e) {
                        // TODO(kahye): Handle exception
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void click(View view) {
        Intent loginintent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(loginintent);
    }
}