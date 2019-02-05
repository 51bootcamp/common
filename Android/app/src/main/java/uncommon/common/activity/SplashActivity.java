package uncommon.common.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent InviteOnlyIntent = new Intent(SplashActivity.this, InviteOnlyActivity.class);
        startActivity(InviteOnlyIntent);
        finish();
    }
}