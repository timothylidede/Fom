package com.savala.fom;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostLocation extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);

            init();
        }
    }

    private DatePickerDialog datePickerDialog;
    private String dayName;

    private long timeend, timestart;

    private String packId;
    private CardView mPostLocationCard;
    private EditText mTimePicker;
    private EditText mDatePicker;
    private CardView mPostLocation;
    private Button mPostLocationButton, mPostCancelButton;
    private TextView mLatitude, mLongitude;
    private Button mSelectDate, mSelectTime;

    private Button mMorning, mAfternoon, mEvening, mLateNight;
    private CardView mTimeCard;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;

    private EditText mMagnify;

    private HashMap<String, Object> postHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post_location);

        mPostLocationCard = (CardView) findViewById(R.id.post_location_card);
        mDatePicker = (EditText) findViewById(R.id.date_picker);
        mTimePicker = (EditText) findViewById(R.id.time_picker);
        mPostLocation = (CardView) findViewById(R.id.post_location);
        mPostLocationButton = (Button) findViewById(R.id.post_location_button);
        mPostCancelButton = (Button) findViewById(R.id.post_cancel_button);
        mMagnify = (EditText) findViewById(R.id.place_picker);
        mSelectDate =(Button) findViewById(R.id.select_date);
        mSelectTime = (Button) findViewById(R.id.select_time);

        mMorning = (Button) findViewById(R.id.morning_button);
        mAfternoon = (Button) findViewById(R.id.afternoon_button);
        mEvening = (Button) findViewById(R.id.evening_button);
        mLateNight = (Button) findViewById(R.id.late_night_button);
        mTimeCard = (CardView) findViewById(R.id.time_card);


        mLatitude = (TextView) findViewById(R.id.latitude);
        mLongitude = (TextView) findViewById(R.id.longitude);

        disableMagnify(mMagnify);
        disableEditText(mTimePicker);
        disableEditText(mDatePicker);

        mSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatePicker();
            }
        });

        mSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPostLocationCard.setVisibility(View.INVISIBLE);
                mPostLocationButton.setVisibility(View.INVISIBLE);
                mPostCancelButton.setVisibility(View.INVISIBLE);
                mTimeCard.setVisibility(View.VISIBLE);
            }
        });

        mMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.setText("Morning", TextView.BufferType.EDITABLE);
                mTimeCard.setVisibility(View.INVISIBLE);
                mPostLocationCard.setVisibility(View.VISIBLE);
                mPostLocationButton.setVisibility(View.VISIBLE);
                mPostCancelButton.setVisibility(View.VISIBLE);
            }
        });

        mAfternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.setText("Afternoon", TextView.BufferType.EDITABLE);
                mTimeCard.setVisibility(View.INVISIBLE);
                mPostLocationCard.setVisibility(View.VISIBLE);
                mPostLocationButton.setVisibility(View.VISIBLE);
                mPostCancelButton.setVisibility(View.VISIBLE);
            }
        });

        mEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.setText("Evening", TextView.BufferType.EDITABLE);
                mTimeCard.setVisibility(View.INVISIBLE);
                mPostLocationCard.setVisibility(View.VISIBLE);
                mPostLocationButton.setVisibility(View.VISIBLE);
                mPostCancelButton.setVisibility(View.VISIBLE);
            }
        });

        mLateNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker.setText("Late Night", TextView.BufferType.EDITABLE);
                mTimeCard.setVisibility(View.INVISIBLE);
                mPostLocationCard.setVisibility(View.VISIBLE);
                mPostLocationButton.setVisibility(View.VISIBLE);
                mPostCancelButton.setVisibility(View.VISIBLE);
            }
        });

        Intent intent = getIntent();
        packId = intent.getStringExtra("packId");

        mPostCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPostLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = mLatitude.getText().toString().trim();
                String longitude = mLongitude.getText().toString().trim();
                String timestamp = "" + System.currentTimeMillis();
                String place_name = mMagnify.getText().toString().trim();
                String date = mDatePicker.getText().toString().trim();
                String time = mTimePicker.getText().toString().trim();

                FirebaseDatabase.getInstance().getReference("packs")
                        .orderByChild("pack_id").equalTo(packId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds:snapshot.getChildren()) {
                                    String newTimestart = "" + ds.child("timestart").getValue();
                                    String newTimeend = "" + ds.child("timeend").getValue();

                                    timestart = Long.parseLong(newTimestart);
                                    timeend = Long.parseLong(newTimeend);

                                    if(TextUtils.isEmpty(place_name)){
                                        Toast.makeText(PostLocation.this, "Choose a valid location from the map", Toast.LENGTH_SHORT).show();
                                    }else if(TextUtils.isEmpty(latitude)){
                                        Toast.makeText(PostLocation.this, "Choose a valid location from the map", Toast.LENGTH_SHORT).show();
                                    }else if(TextUtils.isEmpty(longitude)){
                                        Toast.makeText(PostLocation.this, "Choose a valid location from the map", Toast.LENGTH_SHORT).show();
                                    }else if(TextUtils.isEmpty(date)){
                                        Toast.makeText(PostLocation.this, "Provide a desired date", Toast.LENGTH_SHORT).show();
                                    }else if(TextUtils.isEmpty(time)){
                                        Toast.makeText(PostLocation.this, "Provide the estimated time", Toast.LENGTH_SHORT).show();
                                    }else{
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("place_name", place_name);
                                        hashMap.put("place_latitude", latitude);
                                        hashMap.put("place_longitude", longitude);
                                        hashMap.put("date", date);
                                        hashMap.put("time", time);
                                        hashMap.put("post_id", timestamp);
                                        hashMap.put("timestamp", timestamp);
                                        hashMap.put("like_count", "0");
                                        hashMap.put("count", 0);
                                        hashMap.put("pack_id", packId);
                                        hashMap.put("timestart", timestart);
                                        hashMap.put("timeend", timeend);

                                        FirebaseDatabase.getInstance().getReference("posts")
                                                .child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PostLocation.this, "Post successful", Toast.LENGTH_SHORT).show();
                                                finish();
                                                mPostLocationButton.setEnabled(false);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PostLocation.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        FirebaseDatabase.getInstance().getReference("packs").child(packId)
                                                .child("posts").child(timestamp).setValue(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        getLocationPermission();

        Places.initialize(getApplicationContext(), "AIzaSyBnDxwT9pQwYXE0fHThK2oRwRQQLgbQcPI");
        mMagnify.setFocusable(false);
        mMagnify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List <Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                ,Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .setCountry("KE")
                        .build(PostLocation.this);

                startActivityForResult(intent, 100);
            }
        });

    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month = month + 1;
                SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date myDate = inFormat.parse(dayOfMonth+"-"+month+"-"+year);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                    dayName = simpleDateFormat.format(myDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String date = dayName + " " + makeDateString(dayOfMonth, month);
                mDatePicker.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        cal.add(Calendar.DAY_OF_MONTH, 7);

        int style = AlertDialog.THEME_DEVICE_DEFAULT_DARK;

        mDatePicker.setEnabled(false);
        mTimePicker.setEnabled(false);
        mMagnify.setEnabled(false);

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, dayOfMonth);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);

        datePickerDialog.show();
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void disableMagnify(EditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
    }

    private String makeDateString(int dayOfMonth, int month){
        return dayOfMonth + " " + getMonthFormat(month);
    }

    private String getMonthFormat(int month){
        if(month == 1)
            return "JANUARY";
        if(month == 2)
            return "FEBRUARY";
        if(month == 3)
            return "MARCH";
        if(month == 4)
            return "APRIL";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUNE";
        if(month == 7)
            return "JULY";
        if(month == 8)
            return "AUGUST";
        if(month == 9)
            return "SEPTEMBER";
        if(month == 10)
            return "OCTOBER";
        if(month == 11)
            return "NOVEMBER";
        if(month == 12)
            return "DECEMBER";

        return "JULY";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);

            String latitude = String.valueOf(place.getLatLng().latitude);
            String longitude = String.valueOf(place.getLatLng().longitude);

            mMagnify.setText(place.getName());
            mLatitude.setText(latitude);
            mLongitude.setText(longitude);

            moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());

        }
    }

    private void init(){
        mMagnify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction() == KeyEvent.ACTION_DOWN
                || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute method for searching
                    geoLocate();
                }

                return false;
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate(){
        String searchString = mMagnify.getText().toString();

        Geocoder geocoder = new Geocoder(PostLocation.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        if(list.size() >0){
            Address address = list.get(0);

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM
            , address.getAddressLine(0 ));
        }
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PostLocation.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map request


        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PostLocation.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "Unable to make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();

                            try {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
                                        , DEFAULT_ZOOM
                                        , "My Location");
                            }catch(Exception e){
                                Toast.makeText(PostLocation.this, "Kindly turn on your location to post", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }else{
                            Toast.makeText(PostLocation.this, "Unable to get your current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch(SecurityException e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(PostLocation.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}