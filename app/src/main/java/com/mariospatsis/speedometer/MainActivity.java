package com.mariospatsis.speedometer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,LocationListener{
    ConstraintLayout view;
    Button startbtn;
    Button listbtn;
    Button mapbtn;
    TextView speedText;
    final static int REQUESTCODE = 324;
    LocationManager locationManager;
    DbHelper db;
    SpeedLimit sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        checkPermision();
        view = (ConstraintLayout) findViewById(R.id.main_view);
        startbtn =(Button) findViewById(R.id.main_startbtn);
        listbtn = (Button) findViewById(R.id.main_list);
        mapbtn = (Button) findViewById(R.id.main_map);
        startbtn.setOnClickListener(this);
        listbtn.setOnClickListener(this);
        mapbtn.setOnClickListener(this);

        speedText = (TextView) findViewById(R.id.main_speedlbl);
        this.onLocationChanged(null);
        db = new DbHelper(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.en:
                System.out.println("EN");
                setLanguage("en");
                return true;
            case R.id.gr:
                System.out.println("EL");
                setLanguage("el");
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void setLanguage(String lang) {

        Locale locale = new Locale(lang);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
            this.createConfigurationContext(config);
        Intent updateAct = new Intent(this, MainActivity.class);
        startActivity(updateAct);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_startbtn:
                startSpeedometerHandler();
                break;
            case R.id.main_list:
                getListOfEvents();
                break;
            case R.id.main_map:
                    showMap();
                break;

        }
    }

    private void showMap() {

        Intent maps = new Intent(MainActivity.this,map_activity.class);
        startActivity(maps);
        finish();



    }

    private void getListOfEvents() {
        StringBuilder sb = new StringBuilder();

        List<Event> events = db.getAllEvents();

        if (events.size() > 0) {

            for (Event event : events) {

                int id = event.getId();
                String latitude = event.getLatitude();
                String longtitude = event.getLongtitude();
                String speed = event.getSpeed();
                String timestamp = event.getTimestamp();

                String text = getString(R.string.alert_list,id,latitude,longtitude,speed,timestamp);
                sb.append(text);

            }

            AlertDialog builder1 =new AlertDialog.Builder(this)
                    .setTitle("Events")
                    .setMessage(sb.toString())
                    .setPositiveButton(android.R.string.yes, null)
                    .setNegativeButton(android.R.string.no, null).show();

        }else{
            AlertDialog builder1 =new AlertDialog.Builder(this)
                    .setTitle("Events")
                    .setMessage(R.string.alert_list_empty).show();

        }




    }

    private void startSpeedometerHandler() {
        sp = db.getSpeedLimit();
        checkPermision();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this,"Yesss I have GPS",Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                System.out.println("test");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                        (LocationListener) this);
            } else
                Toast.makeText(this,"I need this permission!...",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("testtttttt");
        if(location==null){
            speedText.setText("-.-" +" " +getResources().getString(R.string.speedend));
        }else{

            float currentSpeed = (float) Math.round(((location.getSpeed()*3600)/1000));
//            System.out.println("loca :"+ location.hasSpeed());
            System.out.println("loca :"+ location.getSpeed());
            System.out.println("locationn :"+ currentSpeed);
            //show speed
            speedText.setText(currentSpeed + " "+ getResources().getString(R.string.speedend ));
            if(currentSpeed > Float.parseFloat(sp.getSlimit())){
                changeBgColor("alert");
                Event event = new Event();
                event.setLatitude(Double.toString(location.getLatitude()));
                event.setLongtitude(Double.toString(location.getLongitude()));
                event.setSpeed(Float.toString(currentSpeed));

                //insert the event into the database
                db.insertEvent(event);
            }else{
                changeBgColor("");
            }

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void changeBgColor(String limit) {
        switch (limit){
            case "alert":
                view.setBackgroundColor(Color.RED);
                break;
            default:
                view.setBackgroundColor(Color.WHITE);
        }

    }

    public void checkPermision(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUESTCODE);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,
                    (LocationListener) this);
        }
    }

//    private void speedUnit(long speed)
//    {
////here speed is the value extracted from speed=location.getSpeed();
//        //for km/hour
//        double a = 3.6 * (event);
//        int kmhSpeed = (int) (Math.round(speed));
//
//
//        //for mile/hour
//        double a = 2.23694 * (event);
//        int mileSpeed = (int) (Math.round(speed));
//
//
//    }
}


