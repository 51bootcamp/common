package uncommon.common.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.models.ClassList;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Class;
import uncommon.common.models.Reservation;
import uncommon.common.network.RetrofitInstance;

public class ConfirmReservationActivity extends AppCompatActivity {

    TextView classTextView;
    TextView expertTextView;
    TextView numOfPeopleView;
    TextView resDateTextView;
    TextView totalResPriceView;
    ImageView classImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reservation);

        Bundle bundle = this.getIntent().getExtras();

        classImgView = (ImageView) this.findViewById(R.id.classImgView);
        classTextView = (TextView) this.findViewById(R.id.classTextView);
        expertTextView = (TextView) this.findViewById(R.id.expertTextView);
        numOfPeopleView = (TextView) this.findViewById(R.id.numOfPeopleView);
        totalResPriceView = (TextView) this.findViewById(R.id.totalResPriceView);
        resDateTextView = (TextView) this.findViewById(R.id.resDateTextView);

        // class Img
        String imageURL = bundle.getString("classImgURL");
        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);
        Picasso.get().load(imageURL).fit().into(classImgView);

        ApiInterface service = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Reservation> request = service.getReservation("kahye5232@naver.com");
        request.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {

                Reservation res = response.body();

                classTextView.setText(res.getClassName());
                expertTextView.setText(res.getExpertName());
                numOfPeopleView.setText(res.getGuestCount().toString());
                totalResPriceView.setText(res.getTotalResPrice().toString());

                //convert date format yyyy-MM-dd into dd-MMM-yyyy
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("E dd MMM yyyy");
                String inputDateStr= res.getDate();

                Call<ClassList> request = service.getClassList(inputDateStr);
                request.enqueue(new Callback<ClassList>() {
                    @Override
                    public void onResponse(Call<ClassList> call, Response<ClassList> response) {
                        ClassList classList = response.body();
                        Class reservationClass = new Class();
                        for(int i = 0; i < classList.getClassList().size(); i++){
                            if(classList.getClassList().get(i).getClassID() == res.getClassId()){
                                reservationClass = classList.getClassList().get(i);
                            }
                        }
                        String imageURL =
                                "http://52.8.187.167:8000" + reservationClass.getCoverImage().get(0);
                        Picasso.get().load(imageURL).resize(2430, 1600).onlyScaleDown().into(classImgView);

                        Date date = null;
                        try {
                            date = inputFormat.parse(inputDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateStr = outputFormat.format(date);
                        resDateTextView.setText(outputDateStr);
                    }

                    @Override
                    public void onFailure(Call<ClassList> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {

            }
        });
    }
}