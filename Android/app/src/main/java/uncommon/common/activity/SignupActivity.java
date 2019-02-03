package uncommon.common.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import uncommon.common.R;
import uncommon.common.models.User;
import uncommon.common.utils.AccountManager;

public class SignupActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";

    private AccountManager accountManager = new AccountManager(this);
    private CallbackManager callbackManager;
    private String userName;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signup);

        callbackManager = CallbackManager.Factory.create();

        LoginButton LoginButton = findViewById(R.id.facebook_login_button);
        // Set the initial permissions to request from the user while logging in
        LoginButton.setReadPermissions(Arrays.asList(EMAIL));
        LoginButton.setAuthType(AUTH_TYPE);

        // Register a callback to respond to the user
        LoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestMe(AccessToken.getCurrentAccessToken());
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException e) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                User newUser = new User(userEmail, userName);

                accountManager.signUp(newUser);

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}