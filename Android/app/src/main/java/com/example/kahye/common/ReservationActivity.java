package com.example.kahye.common;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kahye.common.models._class;
import com.example.kahye.common.models.timeTable;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    private int ticketCnt;
    TextView numTickets;
    ImageButton upButton;
    ImageButton downButton;
    Button alertButton;
    String selectedDate;
    _class selectedClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Bundle bundle = this.getIntent().getExtras();

        // classInfo
        selectedClass = bundle.getParcelable("classInfo");
        setContentView(R.layout.activity_reservation);

        // class Img
        int pic = bundle.getInt("classImg");
        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);
        classImgView.setImageResource(pic);

        selectedDate = bundle.getString("_date");
        TextView dateView = (TextView) findViewById(R.id.dateView);
        dateView.setText(selectedDate);

        // time list
        ListView timeListView = (ListView) findViewById(R.id.timeListView);
        final List<String> timeList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, timeList);

        List<timeTable>  timeslot = selectedClass.getAvailableTimeTable();
        for (int timeListIdx = 0; timeListIdx < timeslot.size(); timeListIdx++){
            String timeString = timeslot.get(timeListIdx).getStartTime().toString() + " ~ " +
                    timeslot.get(timeListIdx).getEndTime().toString();
            timeList.add(timeString);
        }

        timeListView.setAdapter(adapter);

        //count tickets
        numTickets = (TextView) findViewById(R.id.numOfTickets);
        upButton = (ImageButton) findViewById(R.id.upButton);
        downButton = (ImageButton) findViewById(R.id.downButton);

        //Set ticketcnt default value as minGuestCount
        ticketCnt = selectedClass.getminGuestCount();
        numTickets.setText(Integer.toString(ticketCnt));

        upButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(ticketCnt >= selectedClass.getmaxGuestCount()){
                    numTickets.setText(Integer.toString(selectedClass.getmaxGuestCount()));
                    Toast.makeText(ReservationActivity.this,
                            "Too many Tickets!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    numTickets.setText(Integer.toString(++ticketCnt));
                }
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ticketCnt <= selectedClass.getminGuestCount())
                    numTickets.setText(Integer.toString(selectedClass.getminGuestCount()));
                else
                    numTickets.setText(Integer.toString(--ticketCnt));
            }
        });

        // when click Get Tickets Button
        alertButton = (Button) findViewById(R.id.getTickets);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ReservationActivity.this);
                builder.setTitle("Make a Reservation")
                        .setMessage(selectedDate +  "  " + ticketCnt + " " +
                                "tickets \n Do you want to " +
                                "reserve a class?");
                builder.setPositiveButton("Yes", new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("No", new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // turn back to ReservationActivity
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}