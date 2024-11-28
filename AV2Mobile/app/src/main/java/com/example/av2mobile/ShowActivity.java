package com.example.av2mobile;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.av2mobile.databinding.ActivityShowBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends FragmentActivity implements OnMapReadyCallback, LocationConsumer {

    private GoogleMap mMap;
    private ActivityShowBinding binding;
    LocationFactory locationFactory;

    private Marker marker;
    private LatLng mCurrentLocation =  new LatLng(0,0);
    private float bearing;
    private SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    private int mMapType;
    private int mNavigationType;
    private float currentZoom = 15;
    private Polyline polyline;

    TrackDB trackDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();

        mMapType = sharedPreferences.getInt("map_type", 1);
        mNavigationType = sharedPreferences.getInt("navigation_type", 1);
        locationFactory = new LocationFactory(this, this,  this);

        trackDB = new TrackDB(ShowActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {mMap = googleMap;
        mMap.setMapType(mMapType);

        List<LatLng> waypoints = getWaypointsFromDatabase();
        //new LatLng(-12.949116, -38.442277), new LatLng(-12.947881, -38.407034)

        PolylineOptions polylineOptions = new PolylineOptions().addAll(waypoints).width(10)
                .color(Color.RED)
                .geodesic(true);
        polyline = mMap.addPolyline(polylineOptions);
        polyline.setZIndex(10);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(mCurrentLocation)
                .title("Minha Localização");
        marker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, currentZoom));}

    private List<LatLng> getWaypointsFromDatabase() {
        List<Waypoint> waypoints = trackDB.getAll();
        List<LatLng> latLngs = new ArrayList<>();
        for (Waypoint waypoint : waypoints) {
            latLngs.add(new LatLng(waypoint.getLatitude(), waypoint.getLongitude()));
            Log.d("App:", "lat long:" +new LatLng(waypoint.getLatitude(), waypoint.getLongitude()) );
        }
        return latLngs;
    }

    @Override
    public void currentLocation(Location location) {
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        bearing = mNavigationType == 2 ? location.getBearing() : 0.0f;

        if (mMap != null) {
            marker.setPosition(mCurrentLocation);
            currentZoom = mMap.getCameraPosition().zoom;

            CameraPosition cameraPosition =  new CameraPosition.Builder()
                    .target(mCurrentLocation)
                    .zoom(currentZoom)
                    .bearing(bearing)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        }

    }
}