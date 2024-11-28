//CONFIGURACAO
package com.example.av2mobile;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.example.av2mobile.databinding.ActivityMapsBinding;
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

public class MapsActivityGPA extends FragmentActivity implements OnMapReadyCallback, LocationConsumer {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LocationFactory locationFactory;

    private Marker marker;
    private LatLng mCurrentLocation =  new LatLng(0,0);
    private float bearing;
    private SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    private int mMapType;
    private int mNavigationType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();

        mMapType = sharedPreferences.getInt("map_type", 1);
        mNavigationType = sharedPreferences.getInt("navigation_type", 1);
        locationFactory = new LocationFactory(this, this,  this);
    }


    public void onSettingsClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings,  null);

        RadioGroup mapType = dialogView.findViewById(R.id.map_type);
        RadioGroup navigationType = dialogView.findViewById(R.id.navigation_type);

        mapType.check(sharedPreferences.getInt("map_type_option", R.id.vetorial_choice));
        navigationType.check(sharedPreferences.getInt("navigation_type_option",R.id.north_up_choice));

        builder.setView(dialogView);
        builder.setTitle("Configurar mapa");
        builder.setPositiveButton("Aplicar", (dialog, which) -> {
            int selectedMapTypeId = mapType.getCheckedRadioButtonId();
            int selectedNavigationTypeId = navigationType.getCheckedRadioButtonId();

            mMapType = selectedMapTypeId == R.id.vetorial_choice ? GoogleMap.MAP_TYPE_NORMAL : GoogleMap.MAP_TYPE_SATELLITE;
            editor.putInt("map_type", mMapType);
            editor.putInt("map_type_option", selectedMapTypeId);

            mNavigationType = selectedNavigationTypeId == R.id.course_up_choice ? 1 : 2;
            editor.putInt("navigation_type", mNavigationType);
            editor.putInt("navigation_type_option", selectedNavigationTypeId);


            editor.apply();

            if (mMap != null) {
                mMap.setMapType(mMapType);
            }

        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
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


    }
}