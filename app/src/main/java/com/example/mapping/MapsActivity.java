package com.example.mapping;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST_CODE = 123;
    private boolean isPermissionsGranted = false;
    private GoogleMap googleMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager lm ;
    private EditText editSearch ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    editSearch = findViewById(R.id.search_address);

             lm =  (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        getLocationPermission();


   editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
       @Override
       public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
           if(i == EditorInfo.IME_ACTION_SEARCH){
               geoLocate(textView.getText().toString());
           }
           return false;
       }
   });


    }

    private void geoLocate(String text) {
        Geocoder geocoder=new Geocoder(this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(text,1);
            Log.d("count list", "geoLocate: "+list.size());

            if(list.size() != 0){
                String snippet = "Code : "+list.get(0).getCountryCode()+"\n"+
                        "Website : "+list.get(0).getUrl()+"\n"+
                        "Country : "+list.get(0).getCountryName()+"\n"+
                Log.i("Location", list.get(0).getCountryName());
                googleMap.addMarker(new MarkerOptions().position(new LatLng(list.get(0).getLatitude(),list.get(0).getLongitude())).title(list.get(0).getAddressLine(0)).snippet(snippet));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(list.get(0).getLatitude(),list.get(0).getLongitude()),16f));
                googleMap.setInfoWindowAdapter(new MapInformationAdapter(MapsActivity.this));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getDeviceLocation() {
        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            final Task location = fusedLocationProviderClient.getLastLocation();
             location.addOnCompleteListener(new OnCompleteListener() {
                 @Override
                 public void onComplete(@NonNull Task task) {
                     if (task.isSuccessful() && task.getResult() != null) {
                        Location location = (Location) task.getResult();
                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("here"));
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                     } else {
                         Log.w("eex", "getLastLocation:exception", task.getException());

                     }

                 }
             });

        } catch (SecurityException ex) {
            Log.e("security exceptions ", ex.getMessage());
        }


    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        // check if permission is granted . . . .;
        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermissionsGranted = true;
            initMap();
        } else {
            // if not granted request permission . . . ;
            ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isPermissionsGranted = false;

        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isPermissionsGranted = false;
                    return;
                }
            }
            isPermissionsGranted = true;
            // show map activity . . . . ;
            Toast.makeText(this, "permission Request result granted", Toast.LENGTH_SHORT).show();

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // get gps and network enabled . . . .;
        if (checkGpsEnabled()) {
            getDeviceLocation();

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            googleMap.setMyLocationEnabled(true);

        }
    }

    private boolean checkGpsEnabled(){
        try {
         boolean   gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
          boolean  network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(gps_enabled && network_enabled){
                return true ;
            }else {
                new AlertDialog.Builder(MapsActivity.this)
                        .setMessage("network or gps not enabled ")
                        .setPositiveButton("open location setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                recreate();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        }catch (Exception ex){
            Log.i("location exception",ex.getMessage());
        }
        return false;

    }
}
