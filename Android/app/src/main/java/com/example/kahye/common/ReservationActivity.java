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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // class Img
        Bundle bundle = this.getIntent().getExtras();
        int pic = bundle.getInt("classImg");
        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);
        classImgView.setImageResource(pic);

        final String getTime = getIntent().getStringExtra("Date");
        TextView dateView = (TextView) findViewById(R.id.dateView);
        dateView.setText(getTime);

        // time list
        ListView timeListView = (ListView) findViewById(R.id.timeListView);
        final List<String> timeList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, timeList);

        //TODO (gayeon) : load data from server
        timeList.add("2:00PM ~ 4:00PM");
        timeList.add("4:00PM ~ 6:00PM");
        timeList.add("6:00PM ~ 8:00PM");
        timeList.add("8:00PM ~ 10:00PM");

        timeListView.setAdapter(adapter);

        // count tickets
        numTickets = (TextView) findViewById(R.id.numOfTickets);
        upButton = (ImageButton) findViewById(R.id.upButton);
        downButton = (ImageButton) findViewById(R.id.downButton);

        ticketCnt = 0;
        numTickets.setText(Integer.toString(ticketCnt));

        upButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(ticketCnt >= 20){
                    numTickets.setText(Integer.toString(20));
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
                if (ticketCnt <= 0)
                    numTickets.setText(Integer.toString(0));
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
                        .setMessage(getTime +  "  " + ticketCnt + " " +
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