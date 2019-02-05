package uncommon.common.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Class;
import uncommon.common.network.RetrofitInstance;

public class InviteFriendsActivity extends AppCompatActivity {

    EditText emailText;
    TextView sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

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
                                InviteOnlyActivity.class);
                        startActivity(sendEmailIntent);
                    }
                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                    }
                });
            }
        });
    }
}