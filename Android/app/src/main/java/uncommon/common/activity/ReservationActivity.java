package uncommon.common.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.adapter.ReviewAdapter;
import uncommon.common.adapter.TimeSlotAdapter;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Class;
import uncommon.common.models.Reservation;
import uncommon.common.models.Review;
import uncommon.common.models.ReviewList;
import uncommon.common.models.TimeTable;
import uncommon.common.network.RetrofitInstance;
import uncommon.common.utils.GradientTransformation;
import uncommon.common.utils.ListDynamicViewUtil;

public class ReservationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TimeSlotAdapter timeslotAdapter;
    private ArrayList<Integer> timeSlotIdxList = new ArrayList<Integer>();
    private Button alertButton;
    private Context context;
    private Class selectedClass;
    private DatePicker datePicker;
    private int ticketCount;
    private Integer selectedClassID;
    private Integer selectedTimeSlotIdx;
    private ImageButton upButton;
    private ImageButton downButton;
    private List<Review> reviews;
    private List<TimeTable> timeslot;
    private ListView reviewListView;
    private RatingBar classRating;
    private ReviewList reviewList;
    private String selectedDate;
    private String selectedTime;
    private TextView changeTheDateView;
    private TextView classNameView;
    private TextView expertNameView;
    private TextView numOfPeopleView;
    private TextView numTickets;
    private TextView priceView;
    private ApiInterface service = RetrofitInstance.getRetrofitInstance()
            .create(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.drawer_reservation);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Logo button to home
        ImageButton logoButton = (ImageButton) findViewById(R.id.common_logo);
        logoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent logoIntent = new Intent(context, PlaceActivity.class);
                startActivity(logoIntent);
                finish();
                return;
            }
        });

        Bundle bundle = this.getIntent().getExtras();

        // classInfo
        selectedClass = bundle.getParcelable("_classInfo");

        // class Img
        String imageURL = bundle.getString("classImgURL");
        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);
        Picasso.get()
                .load(imageURL)
                .fit()
                .transform(new GradientTransformation())
                .into(classImgView);

        // class Info
        classNameView = (TextView) findViewById(R.id.classTextView);
        classRating = (RatingBar) findViewById(R.id.classRating);
        expertNameView = (TextView) findViewById(R.id.expertTextView);
        numOfPeopleView = (TextView) findViewById(R.id.numOfPeopleView);
        priceView = (TextView) findViewById(R.id.priceView);

        selectedClassID = selectedClass.getClassID();
        classNameView.setText(selectedClass.getClassName());
        classRating.setRating(selectedClass.getClassRating());
        expertNameView.setText(selectedClass.getExpertName());
        numOfPeopleView.setText(selectedClass.getMinGuestCount().toString() + " - "
                        + selectedClass.getMaxGuestCount().toString());
        priceView.setText(selectedClass.getPrice().toString());

        selectedDate = bundle.getString("_date");
        final TextView dateView = (TextView) findViewById(R.id.dateView);
        dateView.setText(selectedDate);

        // time list
        final ListView timeListView = (ListView) findViewById(R.id.timeListView);
        final List<String> timeList = new ArrayList<>();
        timeslot = selectedClass.getAvailableTimeTable();

        for (int timeListIdx = 0; timeListIdx < timeslot.size(); timeListIdx++){
            String timeString = timeslot.get(timeListIdx).getStartTime().toString() + " ~ "
                                + timeslot.get(timeListIdx).getEndTime().toString();
            timeList.add(timeString);
            timeSlotIdxList.add(timeslot.get(timeListIdx).getTimeTableIdx());
        }

        timeslotAdapter = new TimeSlotAdapter(this, timeList, timeslot);
        timeListView.setAdapter(timeslotAdapter);
        ListDynamicViewUtil.setListViewHeightBasedOnChildren(timeListView);
        timeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                view.setSelected(true);
                Object o = timeListView.getItemAtPosition(position);
                selectedTime = o.toString();
                selectedTimeSlotIdx = position;
            }
        });

        // change the date
        changeTheDateView = (TextView) findViewById(R.id.changeTheDateView);
        datePicker = (DatePicker) findViewById(R.id.datepicker);

        changeTheDateView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                datePicker.setVisibility(View.VISIBLE);

                String[] dateTokens = selectedDate.split("-");
                datePicker.updateDate(new Integer(dateTokens[0]),
                        new Integer(dateTokens[1]) - 1, new Integer(dateTokens[2]));

                datePicker.init(datePicker.getYear(), datePicker.getMonth(),
                        datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener(){
                            @Override
                            public void onDateChanged(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                // setting date
                                selectedDate = String.format("%d-%d-%d", year, monthOfYear + 1,
                                        dayOfMonth);
                                dateView.setText(selectedDate);

                                Call<Class> request = service.getClassInfo(selectedDate,
                                        selectedClassID);
                                request.enqueue(new Callback<Class>() {
                                    @Override
                                    public void onResponse(Call<Class> call,
                                                           Response<Class> response) {
                                        selectedClass = response.body();
                                        timeslot = selectedClass.getAvailableTimeTable();
                                        timeSlotIdxList.clear();
                                        timeList.clear();

                                        for (int idx = 0; idx < timeslot.size(); idx++){
                                            String timeString =
                                                    timeslot.get(idx).getStartTime().toString()
                                                            + " ~ "
                                                    + timeslot.get(idx).getEndTime().toString();
                                            timeList.add(timeString);
                                            timeSlotIdxList.add(timeslot.get(idx)
                                                    .getTimeTableIdx());
                                        }
                                        timeslotAdapter.notifyDataSetChanged();
                                        ListDynamicViewUtil.
                                                setListViewHeightBasedOnChildren(timeListView);
                                    }

                                    @Override
                                    public void onFailure(Call<Class> call, Throwable t) {
                                        //TODO (woongjin) : how to deal with failure
                                    }
                                });
                            }
                        });
            }
        });

        //count tickets
        numTickets = (TextView) findViewById(R.id.numOfTickets);
        upButton = (ImageButton) findViewById(R.id.upButton);
        downButton = (ImageButton) findViewById(R.id.downButton);

        //Set ticket count default value as minGuestCount
        ticketCount = selectedClass.getMinGuestCount();
        numTickets.setText(Integer.toString(ticketCount));

        upButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(ticketCount >= selectedClass.getMaxGuestCount()){
                    numTickets.setText(Integer.toString(selectedClass.getMaxGuestCount()));
                    upButton.setColorFilter(view.getContext().getResources().getColor(R.color
                            .reserved));
                    Toast.makeText(ReservationActivity.this, "Too many Tickets!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    numTickets.setText(Integer.toString(++ticketCount));
                    upButton.setColorFilter(view.getContext().getResources().getColor(R.color
                            .colorPrimaryText));
                }
                if (ticketCount > selectedClass.getMinGuestCount()) {
                    downButton.setColorFilter(view.getContext().getResources().getColor(R.color
                            .colorPrimaryText));
                }
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ticketCount <= selectedClass.getMinGuestCount()) {
                    numTickets.setText(Integer.toString(selectedClass.getMinGuestCount()));
                    downButton.setColorFilter(view.getContext().getResources().getColor(R.color
                            .reserved));
                }
                else {
                    numTickets.setText(Integer.toString(--ticketCount));
                    downButton.setColorFilter(view.getContext().getResources().getColor(R.color
                            .colorPrimaryText));
                }
                if(ticketCount < selectedClass.getMaxGuestCount()){
                    upButton.setColorFilter(view.getContext().getResources().getColor(R.color
                            .colorPrimaryText));
                }
            }
        });

        // when click Get Tickets Button
        alertButton = (Button) findViewById(R.id.getTickets);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationActivity.this);
                // TODO (woongjin) need to refactor this block
                //now its spaghetti code
                if(!(selectedTime == null || ticketCount == 0 ||
                        timeslot.get(selectedTimeSlotIdx).getIsBooked())){
                    // title
                    TextView title = new TextView(ReservationActivity.this);
                    title.setGravity(Gravity.CENTER);
                    title.setText("Reservation");
                    title.setTextSize(30);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setPadding(10, 20, 10, 10);
                    builder.setCustomTitle(title);

                    // msg
                    TextView msg = new TextView(
                            ReservationActivity.this);
                    msg.setText(selectedClass.getClassName() + "\n" + selectedDate + "\n"
                                + selectedTime + "\n" + ticketCount + " " + "tickets");
                    msg.setGravity(Gravity.LEFT);
                    msg.setLineSpacing(2,1);
                    msg.setTextSize(20);
                    msg.setPadding(50, 20, 10, 20);
                    builder.setView(msg);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            //TODO(gayeon):send reservation data to server
                            JSONObject requestBody = new JSONObject();
                            requestBody.put("timeTableIdx",
                                    timeSlotIdxList.get(selectedTimeSlotIdx));
                            requestBody.put("guestCount", ticketCount);

                            ApiInterface service = RetrofitInstance.getRetrofitInstance()
                                    .create(ApiInterface.class);
                            Call<Reservation> request = service.makeReservation(requestBody);
                            request.enqueue(new Callback<Reservation>() {
                                @Override
                                public void onResponse(Call<Reservation> call,
                                                       Response<Reservation> response) {
                                    Toast.makeText(ReservationActivity.this,
                                            "success",Toast.LENGTH_LONG).show();
                                    Reservation res = response.body();
                                    Intent confirmResIntent = new Intent(context,
                                            ConfirmReservationActivity.class);
                                    Bundle bundle = new Bundle();
                                    confirmResIntent.putExtra("_reservationID",
                                            res.getReservationID());
                                    confirmResIntent.putExtras(bundle);

                                    startActivity(confirmResIntent);
                                }

                                @Override
                                public void onFailure(Call<Reservation> call,
                                                      Throwable t) {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

        //review
        Call<ReviewList> request = service.getReviewList(selectedClassID);
        request.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
                reviewList = response.body();
                reviews = new ArrayList<Review>();
                int reviewListSize = reviewList.getReviewList().size();
                for(int reviewIdx = 0; reviewIdx < reviewListSize; reviewIdx++){
                    reviews.add(reviewList.getReviewList().get(reviewIdx));
                }

                ReviewAdapter reviewAdapter = new ReviewAdapter(context, reviews);
                reviewListView = (ListView)findViewById(R.id.reviewList);
                reviewListView.setAdapter(reviewAdapter);
                ListDynamicViewUtil.setListViewHeightBasedOnChildren(reviewListView);

                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ReviewList> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home: {
                Intent navIntent = new Intent(context, PlaceActivity.class);
                startActivity(navIntent);
                finish();
                break;
            }
            case R. id.nav_myres: {
                Intent navIntent = new Intent(context, MyReservationActivity.class);
                startActivity(navIntent);
                break;
            }
            case R. id.nav_createClass: {
                Intent navIntent = new Intent(context, MakeClassActivity.class);
                startActivity(navIntent);
                break;
            }
            case R. id.nav_invite: {
                Intent navIntent = new Intent(context, InviteFriendsActivity.class);
                startActivity(navIntent);
                break;
            }
            case R. id.nav_about: {
                Intent navIntent = new Intent(context, AboutActivity.class);
                startActivity(navIntent);
                break;
            }
            case R. id.nav_setting: {
                break;
            }
            case R. id.nav_logout: {
                LoginManager.getInstance().logOut();
                Intent navIntent = new Intent(context, InviteOnlyActivity.class);
                startActivity(navIntent);
                finish();
                break;
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}