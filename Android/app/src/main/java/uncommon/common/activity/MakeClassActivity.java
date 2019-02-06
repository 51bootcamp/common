package uncommon.common.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Class;
import uncommon.common.models.Image;
import uncommon.common.models.TimeSlot;
import uncommon.common.network.RetrofitInstance;

public class MakeClassActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final int numberOfTimeslot = 7;
    private static final int RESULT_LOAD_IMAGE = 1;

    EditText className, minGuestCount, maxGuestCount, price;
    ImageView coverImage;
    String selectedDate;
    String thisDate;
    DatePicker datePicker;

    private Bitmap imageShow;
    private File coverImageFile;

    Integer classID;
    Long epochTime = 0l;
    Long ListViewEpochStartTime = 0l;
    Long ListViewEpochEndTime = 0l;
    String selectedTime = "";
    int selectedTimeSlotIdx = 0;
    boolean isTimeSlotChecked = false;

    ArrayList<TimeSlot> timeSlotArrayList = new ArrayList<>();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.drawer_make_class);

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
                logoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoIntent);
                finish();
                return;
            }
        });

        className = (EditText) findViewById(R.id.class_name_edit_text);
        minGuestCount = (EditText) findViewById(R.id.min_edit_text);
        maxGuestCount = (EditText) findViewById(R.id.max_edit_text);
        price = (EditText) findViewById(R.id.price_edit_text);
        coverImage = (ImageView) findViewById(R.id.imageUpload);

        DateFormat today = new SimpleDateFormat("yyyy-MM-dd:HH");
        today.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        DateFormat todayDate = new SimpleDateFormat("yyyy-MM-dd");
        todayDate.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        Date date = new Date();

        selectedDate = today.format(date);
        thisDate = todayDate.format(date);
        selectedDate = thisDate + ":9";

        try {
            epochTime = today.parse(selectedDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        epochTime /= 1000;

        //datePicker
        datePicker = (DatePicker)findViewById(R.id.datepicker);

        datePicker.init(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),

                new DatePicker.OnDateChangedListener(){

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth){

                        selectedDate = String.format("%d-%d-%d:%d", year,
                                monthOfYear + 1, dayOfMonth,0);
                        thisDate = String.format("%d-%d-%d", year,
                                monthOfYear + 1, dayOfMonth);

                        DateFormat selectedDateFormat = new SimpleDateFormat
                                ("yyyy-MM-dd:HH");
                        selectedDateFormat.setTimeZone(TimeZone.getTimeZone
                                ("America/Los_Angeles"));

                        try {
                            epochTime = selectedDateFormat.parse(selectedDate).getTime();
                            epochTime /= 1000;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });

        // time list view
        final ListView timeListView = (ListView) findViewById(R.id.classTimeListView);
        final List<String> classTimeList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, classTimeList);

        int startTime = 9;
        int endTime = 11;

        for (int timeListIdx = 0; timeListIdx < numberOfTimeslot; timeListIdx++){

            String timeString = convertTimeToString(startTime) + " ~ "
                    + convertTimeToString(endTime);
            classTimeList.add(timeString);
            timeSlotArrayList.add(new TimeSlot(timeString, false));

            startTime += 2;
            endTime += 2;
        }

        timeListView.setAdapter(adapter);
        timeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        timeListView.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long l) {

                isTimeSlotChecked = true;
                ListViewEpochStartTime = epochTime;
                ListViewEpochEndTime = epochTime;
                view.setSelected(true);

                if(timeSlotArrayList.get(position).getSeleced() == true){
                    timeSlotArrayList.get(position).setSelected(false);
                }
                else{
                    timeSlotArrayList.get(position).setSelected(true);
                }

                selectedTimeSlotIdx = position;
            }
        });

    }

    public void imageChooseClick(View vies){

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);

    }

    public void submitClick(View view) {
        submitShow();
    }

    public void submitShow()
    {
        isTimeSlotChecked = timeSlotCheck();

        if (coverImageFile == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Upload Class Cover Image First!!");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.show();
        }

        else if(!isTimeSlotChecked){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Choose TimeTable First!!");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.show();
        }
        else if(className.getText().toString().equals("") ||
                minGuestCount.getText().toString().equals("")||
                maxGuestCount.getText().toString().equals("")||
                price.getText().toString().equals("")){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Need to complete all required fields.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.show();
        }
        else if(Integer.valueOf(minGuestCount.getText().toString()) >
                Integer.valueOf(maxGuestCount.getText().toString())){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Minimum number of guest cannot be larger than " +
                    "Maximum number of guest.");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.show();
        }

        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Really want to open a new class?");
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            makeClass();
                        }
                    });

            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data){
        super.onActivityResult(requestCode , resultCode, data);

        boolean isGrantStorage = grantExternalStoragePermission();

        if(isGrantStorage){
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data !=null ){
                Uri selectedImage = data.getData();

                try {
                    imageShow = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            selectedImage);
                    getRealPath(selectedImage);
                    imageShow = rotateImage(imageShow);
                    coverImage.setImageBitmap(imageShow);
                    coverImage.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //Uri Schema content:// to file://
    public void getRealPath(Uri imageUri){

        Cursor cursor = null;

        try {
            String[] proj = { MediaStore.Images.Media.DATA };

            assert imageUri != null;
            cursor = getContentResolver().query(imageUri, proj,
                    null, null, null);

            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            coverImageFile = new File(cursor.getString(column_index));

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean grantExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                return true;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }else{
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (Build.VERSION.SDK_INT >= 23) {
                    if(grantResults[0]== PackageManager.PERMISSION_GRANTED){

                    }
                }
    }

    public void uploadImage(Integer classID){
        ApiInterface service = RetrofitInstance.getRetrofitInstance()
                .create(ApiInterface.class);
        RequestBody requestImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "coverImage",
                        coverImageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"),
                                new File(coverImageFile.getAbsolutePath())))
                .addFormDataPart("title", "test_title")
                .build();

        Call<Image> imageCall = service.uploadImage(requestImage, classID);
        imageCall.enqueue(new Callback<Image>() {
            @Override
            public void onResponse(Call<Image> call, Response<Image> response) {

            }

            @Override
            public void onFailure(Call<Image> call, Throwable t) {

            }
        });
    }

    public void makeClass(){
        ArrayList<JSONObject> timeSlotObjectList = new ArrayList<>();

        for (int timeListIdx = 0 ; timeListIdx < numberOfTimeslot; timeListIdx++){
            JSONObject timeSlotObject = new JSONObject();
            if(timeSlotArrayList.get(timeListIdx).getSeleced() == true){
                ListViewEpochStartTime = getEpochTime(epochTime, timeListIdx);
                ListViewEpochEndTime = getEpochTime(epochTime + 7200, timeListIdx);
                timeSlotObject.put("startTime", Long.toString(ListViewEpochStartTime));
                timeSlotObject.put("endTime", Long.toString(ListViewEpochEndTime));

                timeSlotObjectList.add(timeSlotObject);
            }
        }

        ApiInterface makeClassService = RetrofitInstance.getRetrofitInstance()
                .create(ApiInterface.class);

        ListViewEpochStartTime = getEpochTime(epochTime, selectedTimeSlotIdx);
        ListViewEpochEndTime = getEpochTime(epochTime + 7200, selectedTimeSlotIdx);

        JSONObject requestBody = new JSONObject();
        requestBody.put("className", className.getText().toString());
        requestBody.put("minGuestCount", minGuestCount.getText().toString());
        requestBody.put("maxGuestCount", maxGuestCount.getText().toString());
        requestBody.put("price", price.getText().toString());
        requestBody.put("timeSlotList",timeSlotObjectList);
        requestBody.put("date", thisDate);

        Call<Class> call = makeClassService.makeClass(requestBody);
        call.enqueue(new Callback<Class>() {
            @Override
            public void onResponse(Call<Class> call, Response<Class> response) {

                classID = response.body().getClassID();

                uploadImage(classID);

                Toast.makeText(getApplicationContext(),
                        "Create New Class",Toast.LENGTH_LONG).show();
                Intent submitIntent = new Intent(MakeClassActivity.this,
                        PlaceActivity.class);
                submitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(submitIntent);
            }
            @Override
            public void onFailure(Call<Class> call, Throwable t) {
            }
        });
    }

    public boolean timeSlotCheck(){

        boolean check = false;

        for (int timeListIdx = 0; timeListIdx < numberOfTimeslot; timeListIdx++){

            if(timeSlotArrayList.get(timeListIdx).getSeleced() == true){
                check = true;
                break;
            }
        }
        return check;
    }

    public String convertTimeToString(int time){

        String output = "";
        String meridiem = "";

        if (time > 12){
            time -= 12;
            meridiem = "PM";
        }
        else{
            meridiem = "AM";
        }

        if (time<10){
            output ="0"+ Integer.toString(time) + " : 00 " + meridiem;
        }
        else if(time<=12){
            output = Integer.toString(time) + " : 00 " + meridiem;
        }

        return output;
    }

    public long getEpochPerHour(int hour){
        long epochTime = 0l;
        epochTime = hour * 3600;
        return epochTime;
    }

    public long getEpochTime(long date, int position){
        long epochTime = 0l;
        epochTime = date + (9 + position*2)*3600;
        return epochTime;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    public Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(coverImageFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }

        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        int rotationInDegrees = exifToDegrees(rotation);

        Matrix matrix = new Matrix();
        if (rotation != 0) {matrix.preRotate(rotationInDegrees);}

        Bitmap rotatedImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

        return rotatedImage;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home: {
                Intent navIntent = new Intent(context, PlaceActivity.class);
                navIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                navIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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