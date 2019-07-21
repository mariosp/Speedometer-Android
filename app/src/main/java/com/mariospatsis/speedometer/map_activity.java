package com.mariospatsis.speedometer;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class map_activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DbHelper(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent gotoMainActivity = new Intent(map_activity.this, MainActivity.class);
        startActivity(gotoMainActivity);
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<Event> events = db.getAllEvents();
        if (events.size() > 0) {

            for (Event event : events) {

//                int id = event.getId();
                String latitude = event.getLatitude();
                String longtitude = event.getLongtitude();
                String speed = event.getSpeed();
                String timestamp = event.getTimestamp();

                String text = getString(R.string.map_event, speed, timestamp);
                LatLng position = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longtitude));
                mMap.addMarker(new MarkerOptions().position(position).title(text));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));


            }

        }
    }
}
