package uncommon.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Review;
import uncommon.common.network.RetrofitInstance;

public class ReviewActivity extends AppCompatActivity {

    private Button completeButton;
    private EditText reviewEditText;
    private EditText titleEditText;
    private RatingBar reviewRating;
    private Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        completeButton = (Button) findViewById(R.id.completeButton);
        reviewEditText = (EditText) findViewById(R.id.reviewEditText);
        reviewRating = (RatingBar) findViewById(R.id.reviewRating);
        titleEditText = (EditText) findViewById(R.id.titleEditText);

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO(gayeon): change userEmail and classID after making reservation list
                // new review goes to server
                Review newReview = new Review("jmj@kookmin.ac.kr", "jmj", 4,
                        titleEditText.getText().toString(), reviewEditText.getText().toString(),
                        reviewRating.getRating());

                ApiInterface service = RetrofitInstance.getRetrofitInstance()
                        .create(ApiInterface.class);
                Call<Review> request = service.writeReview(newReview);
                request.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        Toast.makeText(ReviewActivity.this,"success",
                                Toast.LENGTH_LONG).show();
                        Intent placeIntent = new Intent(ReviewActivity.this, PlaceActivity.class);
                        startActivity(placeIntent);
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {

                    }
                });
            }
        });
    }
}

