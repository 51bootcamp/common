package uncommon.common.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.network.RetrofitInstance;
import uncommon.common.utils.AccountManager;

public class InviteOnlyActivity extends AppCompatActivity {

    private Context context = this;
    private String userName;
    private String userEmail;
    private AccountManager accountManager = new AccountManager(context);

    TextView submitTextButton;
    EditText inviteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_only);

        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (currentAccessToken != null) { requestMe(currentAccessToken); }

        inviteEditText = (EditText) this.findViewById(R.id.inviteEditText);
        submitTextButton = (TextView) this.findViewById(R.id.submitTextButton);

        submitTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface service = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
                Call<Void> request = service.getInviteCode(inviteEditText.getText().toString());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Intent signupIntent = new Intent(InviteOnlyActivity.this,
                                    SignupActivity.class);
                            startActivity(signupIntent);
                        } else if(response.code() == 203) {
                            Toast.makeText(InviteOnlyActivity.this,
                                    "Invitation code is already expired", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(InviteOnlyActivity.this,
                                    "Please check your invitation code again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });
    }

    public void click(View view) {
        Intent loginIntent = new Intent(InviteOnlyActivity.this, MainActivity.class);
        startActivity(loginIntent);
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
