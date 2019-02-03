package uncommon.common.utils;

import android.content.Context;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookLoginCallback implements  FacebookCallback<LoginResult>{
    private Context context;
    private String userEmail = "";
    private String userName = "";
    private AccountManager accountManager;

    public FacebookLoginCallback(Context context){
        this.context = context;
        this.accountManager = new AccountManager(context);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        requestMe(AccessToken.getCurrentAccessToken());
    }

    @Override
    public void onCancel() { }

    @Override
    public void onError(FacebookException e) {
        // TODO(kahye): Handle exception
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
