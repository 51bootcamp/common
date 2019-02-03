package uncommon.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import org.json.simple.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import retrofit2.Retrofit;
import uncommon.common.activity.PlaceActivity;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.User;
import uncommon.common.network.RetrofitInstance;

public class AccountManager {
    private Context context;
    private Retrofit retrofitInstance = RetrofitInstance.getLoginRetrofitInstance();
    private ApiInterface service = retrofitInstance.create(ApiInterface.class);

    public AccountManager(Context context){
        this.context = context;
    }

    public void Login(final String userEmail, final String userName){
        JSONObject userEmailJsonObject = new JSONObject();
        userEmailJsonObject.put("userEmail", userEmail);

        Call<JSONObject> request = service.login(userEmailJsonObject);
        request.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.code() == 200){
                    RetrofitInstance.JWT = "Bearer " + response.body().get("token").toString();
                    Intent placeIntent = new Intent(context, PlaceActivity.class);
                    context.startActivity(placeIntent);
                }
                else if(response.code() == 409){
                    alertSingUpBox(userName);
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                //TODO(woongjin) handle onFailure
            }
        });
    }

    public void signUp(User newUser){
        Call<JSONObject> request = service.signup(newUser);
        request.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.code() == 200){
                    RetrofitInstance.JWT = "Bearer " + response.body().get("token").toString();
                    Intent placeIntent = new Intent(context, PlaceActivity.class);
                    context.startActivity(placeIntent);
                }
                else{
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                //TODO(woongjin) handle onFailure
            }
        });
    }

    // an alert box that ask the user to sign-up or not with information from facebook graph api
    public void alertSingUpBox(final String userName){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER);
        title.setText("SignUp First");
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(10, 20, 10, 10);
        builder.setCustomTitle(title);

        TextView msg = new TextView(context);
        msg.setText("Sorry, " + userName + ",\nyou are not signed yet");
        msg.setGravity(Gravity.LEFT);
        msg.setLineSpacing(2,1);
        msg.setTextSize(20);
        msg.setPadding(50, 20, 10, 20);
        builder.setView(msg);

        AlertDialog dialog = builder.create();
        dialog.show();

    };
}
