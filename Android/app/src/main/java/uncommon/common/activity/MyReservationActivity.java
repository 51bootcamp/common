package uncommon.common.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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

        // get data from server
        Call<ReservationList> request = service.getReservationList("jmj@kookmin.ac.kr");
        request.enqueue(new Callback<ReservationList>() {
            @Override
            public void onResponse(Call<ReservationList> call, Response<ReservationList> response) {
                reservationList = response.body();
                reservations = new ArrayList<Reservation>();
                int reservationListSize = reservationList.getReservationList().size();
                for(int reserveIdx = 0; reserveIdx < reservationListSize; reserveIdx++){
                    reservations.add(reservationList.getReservationList().get(reserveIdx));
                }

                MyReservationAdapter reservationAdapter = new MyReservationAdapter(context,
                        reservations);
                reservationListView = (ListView)findViewById(R.id.myReservationListView);
                reservationListView.setAdapter(reservationAdapter);
                ListDynamicViewUtil.setListViewHeightBasedOnChildren(reservationListView);

                reservationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        view.setSelected(true);

                        Intent confirmReserveIntent = new Intent(MyReservationActivity.this,
                                ConfirmReservationActivity.class);
                        startActivity(confirmReserveIntent);
                    }
                });
            }

            @Override
            public void onFailure(Call<ReservationList> call, Throwable t) {

            }
        });
    }
}
