package uncommon.common.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.adapter.MyReservationAdapter;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Reservation;
import uncommon.common.models.ReservationList;
import uncommon.common.network.RetrofitInstance;
import uncommon.common.utils.ListDynamicViewUtil;

public class MyReservationActivity extends AppCompatActivity {

    private ApiInterface service = RetrofitInstance.getRetrofitInstance()
            .create(ApiInterface.class);
    private Context context;
    private List<Reservation> reservations;
    private ListView reservationListView;
    private ReservationList reservationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);
        context = this;

        reservationListView = (ListView) findViewById(R.id.myReservationListView);

        // get data from server
        Call<ReservationList> request = service.getReservationList();
        request.enqueue(new Callback<ReservationList>() {
            @Override
            public void onResponse(Call<ReservationList> call, Response<ReservationList> response) {
                reservationList = response.body();
                reservations = new ArrayList<Reservation>();
                int reservationListSize = reservationList.getReservationList().size();

                // if there is no reservation
                if(reservationListSize == 0){
                    reservationListView.setVisibility(View.INVISIBLE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MyReservationActivity.this);
                    // msg
                    TextView msg = new TextView(MyReservationActivity.this);
                    msg.setGravity(Gravity.LEFT);
                    msg.setLineSpacing(2,1);
                    msg.setText("There is no reservations.");
                    msg.setTextSize(20);
                    msg.setPadding(50, 60, 10, 10);
                    builder.setView(msg);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            Intent placeIntent = new Intent(MyReservationActivity.this,
                                    PlaceActivity.class);
                            startActivity(placeIntent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                for(int reserveIdx = 0; reserveIdx < reservationListSize; reserveIdx++){
                    reservations.add(reservationList.getReservationList().get(reserveIdx));
                }

                MyReservationAdapter reservationAdapter = new MyReservationAdapter(context,
                        reservations);
                reservationListView.setAdapter(reservationAdapter);
                ListDynamicViewUtil.setListViewHeightBasedOnChildren(reservationListView);
            }

            @Override
            public void onFailure(Call<ReservationList> call, Throwable t) {

            }
        });
    }
}
