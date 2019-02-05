package uncommon.common.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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

public class MakeClassActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_class);

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
//            epochTime = new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate).getTime();
            epochTime = today.parse(selectedDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        epochTime /= 1000;
        Toast.makeText(getApplicationContext(),"First selected date : " + selectedDate + "epochTime : \n " + epochTime ,Toast.LENGTH_LONG).show();

        //datePicker
        datePicker = (DatePicker)findViewById(R.id.datepicker);

        datePicker.init(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),

                new DatePicker.OnDateChangedListener(){

                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth){

                        selectedDate = String.format("%d-%d-%d:%d", year, monthOfYear + 1, dayOfMonth,0);
                        thisDate = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);

                        DateFormat selectedDateFormat = new SimpleDateFormat("yyyy-MM-dd:HH");
                        selectedDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

                        try {
                            epochTime = selectedDateFormat.parse(selectedDate).getTime();
                            epochTime /= 1000;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"Second selected date : " + selectedDate + "epochTime : " + epochTime ,Toast.LENGTH_LONG).show();
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
//        ListDynamicViewUtil.setListViewHeightBasedOnChildren(timeListView);
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
//                Object o = timeListView.getItemAtPosition(position);
//                selectedTime = o.toString();

                if(timeSlotArrayList.get(position).getSeleced() == true){
                    timeSlotArrayList.get(position).setSelected(false);
                }
                else{
                    timeSlotArrayList.get(position).setSelected(true);
                }

//                TimeSlot timeslot = new TimeSlot(timeListView.getItemAtPosition(position).toString() , true);
//                Toast.makeText(getApplicationContext(),"In listView : " + selectedTime,Toast.LENGTH_LONG).show();

                selectedTimeSlotIdx = position;
//                Toast.makeText(getApplicationContext(),"In listView : " + selectedTimeSlotIdx,Toast.LENGTH_LONG).show();
//                ListViewEpochStartTime = getEpochTime(epochTime, selectedTimeSlotIdx);
//                ListViewEpochEndTime = getEpochTime(epochTime + 7200, selectedTimeSlotIdx);
                Toast.makeText(getApplicationContext(),"selected date : " + selectedDate +
                        "\n StartepochTime : " + ListViewEpochStartTime+
                        "\n EndepochTime : " + ListViewEpochEndTime,Toast.LENGTH_LONG).show();
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

        if(!isTimeSlotChecked){
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
//                            uploadImage();
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
                    imageShow = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    coverImage.setImageBitmap(imageShow);
                    coverImage.setVisibility(View.VISIBLE);
                    getRealPath(selectedImage);

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
            cursor = getContentResolver().query(imageUri, proj, null, null, null);

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
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }else{
            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                Toast.makeText(getApplicationContext(),"Create Image",Toast.LENGTH_LONG).show();
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
//                        requestBody.put("expertEmail", expertName.getText());
        requestBody.put("minGuestCount", minGuestCount.getText().toString());
        requestBody.put("maxGuestCount", maxGuestCount.getText().toString());
        requestBody.put("price", price.getText().toString());
//        requestBody.put("startTime", Long.toString(ListViewEpochStartTime));
//        requestBody.put("endTime", Long.toString(ListViewEpochEndTime));

        requestBody.put("timeSlotList",timeSlotObjectList);
        requestBody.put("date", thisDate);

        Call<Class> call = makeClassService.makeClass(requestBody);
        call.enqueue(new Callback<Class>() {
            @Override
            public void onResponse(Call<Class> call, Response<Class> response) {

                classID = response.body().getClassID();

                uploadImage(classID);

                Toast.makeText(getApplicationContext(),"Create New Class",Toast.LENGTH_LONG).show();
                Intent submitIntent = new Intent(MakeClassActivity.this, MainActivity.class);
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

}