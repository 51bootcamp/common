package com.example.kahye.common;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kahye.common.models.Class;
import com.example.kahye.common.models.TimeTable;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    private Button alertButton;
    private Class selectedClass;
    private int ticketCount;
    private ImageButton upButton;
    private ImageButton downButton;
    private String selectedDate;
    private String selectedTime;
    private TextView numTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Bundle bundle = this.getIntent().getExtras();

        // classInfo
        selectedClass = bundle.getParcelable("_classInfo");
        setContentView(R.layout.activity_reservation);

        // class Img
        //Todo(woongjin) change the hardcoded url to read config file
        // and use it
        String imageURL = "http://52.8.187.167:8000" + bundle.getString(
                "classImgURL");
        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);
        Picasso.get().load(imageURL)
                .fit()
                .into(classImgView);

        // class Info
        TextView classInfoView = (TextView)findViewById(R.id.classInfoView);
        String className = bundle.getString("className");
        classInfoView.setText("Class: " + selectedClass.getClassName() +
                "\nExpert: " + selectedClass.getExpertName() + "\nMin: "
                + selectedClass.getMinGuestCount().toString() +  "\nMax: "
                + selectedClass.getMaxGuestCount().toString());

        selectedDate = bundle.getString("_date");
        TextView dateView = (TextView) findViewById(R.id.dateView);
        dateView.setText(selectedDate);

        // time list
        final ListView timeListView = (ListView) findViewById(
                R.id.timeListView);
        final List<String> timeList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, timeList);

        List<TimeTable>  timeslot = selectedClass.getAvailableTimeTable();
        for (int timeListIdx = 0; timeListIdx < timeslot.size(); timeListIdx++){
            String timeString = timeslot.get(timeListIdx).getStartTime().
                    toString() + " ~ " + timeslot.get(timeListIdx).getEndTime
                    ().toString();
            timeList.add(timeString);
        }

        timeListView.setAdapter(adapter);
        timeListView.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long l) {
                view.setSelected(true);
                Object o = timeListView.getItemAtPosition(position);
                selectedTime = o.toString();
            }
        });

        //count tickets
        numTickets = (TextView) findViewById(R.id.numOfTickets);
        upButton = (ImageButton) findViewById(R.id.upButton);
        downButton = (ImageButton) findViewById(R.id.downButton);

        //Set ticketcnt default value as minGuestCount
        ticketCount = selectedClass.getMinGuestCount();
        numTickets.setText(Integer.toString(ticketCount));

        upButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(ticketCount >= selectedClass.getMaxGuestCount()){
                    numTickets.setText(Integer.toString(selectedClass.
                            getMaxGuestCount()));
                    Toast.makeText(ReservationActivity.this,
                            "Too many Tickets!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    numTickets.setText(Integer.toString(++ticketCount));
                }
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ticketCount <= selectedClass.getMinGuestCount())
                    numTickets.setText(Integer.toString(selectedClass.
                            getMinGuestCount()));
                else
                    numTickets.setText(Integer.toString(--ticketCount));
            }
        });

        // when click Get Tickets Button
        alertButton = (Button) findViewById(R.id.getTickets);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ReservationActivity.this);

                if(selectedTime != null && ticketCount != 0){
                    // title
                    TextView title = new TextView(
                            ReservationActivity.this);
                    title.setGravity(Gravity.CENTER);
                    title.setText("Reservation");
                    title.setTextSize(20);
                    title.setPadding(10, 20, 10, 10);
                    builder.setCustomTitle(title);

                    // msg
                    TextView msg = new TextView(
                            ReservationActivity.this);
                    msg.setText(selectedDate + "\n" + selectedTime + "\n" +
                            ticketCount + " " + "tickets \n Do you want to"
                            + " reserve a class?");
                    msg.setGravity(Gravity.CENTER_HORIZONTAL);
                    builder.setView(msg);

                    builder.setPositiveButton("Yes", new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            //TODO(gayeon):send reservation data to server
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss(); // back to ReservationActivity
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
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