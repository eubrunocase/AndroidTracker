package com.example.av2mobile;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.av2mobile.database.TrackDB;
import com.example.av2mobile.database.Waypoint;
import com.example.av2mobile.location.LocationConsumer;
import com.example.av2mobile.location.LocationFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.av2mobile.databinding.ActivityTrackerBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class TrackerActivity extends FragmentActivity implements OnMapReadyCallback, LocationConsumer {

    private GoogleMap mMap;
    private ActivityTrackerBinding binding;

    private float speed;
    private float distance;
    private Location previewsLocation;

    private TextView mSpeedText;
    private TextView mDistanceText;
    private TextView mTimerText;

    private ImageView mSpeedImage;
    private ImageView mDistanceImage;
    private ImageView mTimerImage;

    private Marker marker;

    private int mMapType;
    private int mNavigationType;
    private float bearing;

    private Cronometro cronometro;


    private SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    private LatLng mCurrentLocation =  new LatLng(0,0);

    private Button startButton;
    private Button finishButton;

    TextView timer;

    TrackDB trackDB;

    boolean isStart;
    private Polyline polyline = null;


    private LocationFactory locationFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTrackerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSpeedText = (TextView) findViewById(R.id.speed_text);
        mDistanceText = (TextView) findViewById(R.id.distance_text);
        mTimerText = (TextView) findViewById(R.id.timer_text);

        mSpeedImage = (ImageView) findViewById(R.id.speed_icon);
        mTimerImage = (ImageView) findViewById(R.id.timer_icon);
        mDistanceImage = (ImageView) findViewById(R.id.distance_icon);

        cronometro = new Cronometro(mTimerText, 0);

        locationFactory = new LocationFactory(this, this, this);

        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();

        mMapType = sharedPreferences.getInt("map_type", 1);
        mNavigationType = sharedPreferences.getInt("navigation_type", 1);

        startButton = (Button) findViewById(R.id.start_button);
        finishButton = (Button) findViewById(R.id.finish_button);

        startButton.setOnClickListener(view -> handleStartButton(view));
        finishButton.setOnClickListener(view -> handleFinishButton(view));
        trackDB = new TrackDB(TrackerActivity.this);
    }

    private void handleStartButton(View view) {
      startButton.setVisibility(View.GONE);
      finishButton.setVisibility(View.VISIBLE);
      trackDB.delete();
      distance = 0;
      cronometro.stop();
      cronometro.start();
      isStart = true;
    }

    private void handleFinishButton(View view) {
        startButton.setVisibility(View.VISIBLE);
        finishButton.setVisibility(View.GONE);
        cronometro.pause();
        isStart = false;
    }

    private void addWaypoint(Location location) {
        Waypoint waypoint = new Waypoint(location);
        trackDB.save(waypoint);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mMapType);
        mCurrentLocation = mCurrentLocation.equals(new LatLng(0,0)) ? new LatLng(-34, 151) : mCurrentLocation;
        marker = mMap.addMarker(new MarkerOptions().position(mCurrentLocation).title("Minha Localização"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15));
    }

    @Override
    public void currentLocation(Location location) {
     speed = Math.round(location.getSpeed() * 3.6f);
     if(previewsLocation != null) {
         distance += Math.round(location.distanceTo(previewsLocation));
     }
     previewsLocation = location;

     mSpeedText.setText(speed + "Km/h");

        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        bearing = mNavigationType == 2 ? location.getBearing() : 0.0f;

        if (mMap != null) {
            marker.setPosition(mCurrentLocation);
            float currentZoom = mMap.getCameraPosition().zoom;

            CameraPosition cameraPosition =  new CameraPosition.Builder()
                    .target(mCurrentLocation )
                    .zoom(currentZoom)
                    .bearing(bearing)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        }

        if(isStart) {
            addWaypoint(location);
            mDistanceText.setText(distance + "m");
            }

    }
}