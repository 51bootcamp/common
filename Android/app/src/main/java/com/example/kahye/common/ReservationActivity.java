package com.example.kahye.common;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
    private String className;
    private String selectedDate;
    private String selectedTime;
    private TextView classNameView;
    private TextView expertNameView;
    private TextView numOfPeopleView;
    private TextView numTickets;
    private TextView priceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Bundle bundle = this.getIntent().getExtras();

        // classInfo
        selectedClass = bundle.getParcelable("_classInfo");
        setContentView(R.layout.activity_reservation);

        // class Img
        // Todo(woongjin) change the hardcoded url to read config file
        String imageURL = "http://52.8.187.167:8000" +
                bundle.getString("classImgURL");
        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);
        Picasso.get().load(imageURL).fit().into(classImgView);

        // class Info
        classNameView = (TextView)findViewById(R.id.classTextView);
        expertNameView = (TextView)findViewById(R.id.expertTextView);
        numOfPeopleView = (TextView) findViewById(R.id.numOfPeopleView);
        priceView = (TextView) findViewById(R.id.priceView);

        className = bundle.getString("className");
        classNameView.setText(selectedClass.getClassName());
        expertNameView.setText(selectedClass.getExpertName());
        numOfPeopleView.setText(
                selectedClass.getMinGuestCount().toString() + " - "
                        + selectedClass.getMaxGuestCount().toString());
        priceView.setText(selectedClass.getPrice().toString());

        classNameView.setBackgroundColor(Color.parseColor(
                "#9931343a"));
        expertNameView.setBackgroundColor(Color.parseColor(
                "#9931343a"));

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
            String timeString =
                    timeslot.get(timeListIdx).getStartTime().toString() + " ~ "
                    + timeslot.get(timeListIdx).getEndTime().toString();
            timeList.add(timeString);
        }

        timeListView.setAdapter(adapter);
        //TODO (gayeon) : change time slot to size dynamically
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
                    title.setTextSize(30);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setPadding(10, 20, 10, 10);
                    builder.setCustomTitle(title);

                    // msg
                    TextView msg = new TextView(
                            ReservationActivity.this);
                    msg.setText(selectedClass.getClassName() + "\n" +selectedDate + "\n" +
                            selectedTime + "\n" + " " +
                            ticketCount + " " + "tickets");
//                    msg.setGravity(Gravity.CENTER_HORIZONTAL);
                    msg.setGravity(Gravity.LEFT);
                    Typeface tf = getResources().getFont(R.font.rockb);
                    msg.setTypeface(tf);
                    msg.setLineSpacing(2,1);
                    msg.setTextSize(20);
                    msg.setPadding(50, 20, 10, 20);
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}