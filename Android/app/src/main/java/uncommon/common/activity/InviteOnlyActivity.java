package uncommon.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.network.RetrofitInstance;

public class InviteOnlyActivity extends AppCompatActivity {

    private Context context = this;
    TextView submitTextButton;
    EditText inviteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_only);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        inviteEditText = (EditText) this.findViewById(R.id.inviteEditText);
        submitTextButton = (TextView) this.findViewById(R.id.submitTextButton);

        submitTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface service = RetrofitInstance.getLoginRetrofitInstance()
                        .create(ApiInterface.class);
                Call<Void> request = service.getInviteCode(inviteEditText.getText().toString());
                request.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Intent signupIntent = new Intent(InviteOnlyActivity.this,
                                    SignupActivity.class);
                            startActivity(signupIntent);
                            finish();
                        } else if(response.code() == 203) {
                            Toast.makeText(InviteOnlyActivity.this,
                                    "Invitation code is already expired",
                                    Toast.LENGTH_LONG).show();
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
}
