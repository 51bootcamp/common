package uncommon.common.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uncommon.common.R;
import uncommon.common.activity.PlaceActivity;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.User;
import uncommon.common.network.RetrofitInstance;

public class AccountManager {
    private Context context;
    private ApiInterface service = RetrofitInstance.getRetrofitInstance()
                                    .create(ApiInterface.class);

    public AccountManager(Context context){
        this.context = context;
    }

    public void Login(final String userEmail, final String userName){
        org.json.simple.JSONObject userEmailJsonObject = new org.json.simple.JSONObject();
        userEmailJsonObject.put("userEmail", userEmail);

        Call<User> request = service.login(userEmailJsonObject);
        request.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 202){
                    Intent placeIntent = new Intent(context, PlaceActivity.class);
                    context.startActivity(placeIntent);
                }
                else if(response.code() == 409){
                    alertSingUpBox(userEmail, userName);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //TODO(woongjin) handle onFailure
            }
        });
    }

    public void signUp(User newUser){
        Call<Void> request = service.signup(newUser);
        request.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201){
                    Intent placeIntent = new Intent(context, PlaceActivity.class);
                    context.startActivity(placeIntent);
                }
                else{
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //TODO(woongjin) handle onFailure
            }
        });
    }
    // an alert box that ask the user to sign-up or not with information from facebook graph api
    public void alertSingUpBox(final String userEmail, final String userName){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER);
        title.setText("SignUp");
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(10, 20, 10, 10);
        builder.setCustomTitle(title);

        TextView msg = new TextView(context);
        msg.setText("Welcome, " + userName + "!\nWould you like to sign up?");
        msg.setGravity(Gravity.LEFT);
        Typeface tf = context.getResources().getFont(R.font.rockb);
        msg.setTypeface(tf);
        msg.setLineSpacing(2,1);
        msg.setTextSize(20);
        msg.setPadding(50, 20, 10, 20);
        builder.setView(msg);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                User newUser = new User(userEmail, userName);
                signUp(newUser);
            }
        });
        builder.setNegativeButton("No", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                LoginManager.getInstance().logOut();
                dialog.dismiss(); // back to ReservationActivity
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    };
}
