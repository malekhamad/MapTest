package com.example.mapping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
private  final String TAG = this.getClass().getSimpleName()+" Service ";
private final int ERROR_DIALOG_REQUEST = 9001 ;
Button mapButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isServiceOk()){
                   init();
        }
    }
    public void init(){
        mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }


    // check if google play service is available in android device . . . ;
    public boolean isServiceOk(){
        Log.i(TAG,"is Service Ok ");
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(availability == ConnectionResult.SUCCESS){
            // if google play service is available in device . . . . ;
            Log.i(TAG,"google play services success");

            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(availability)){
            //if google play has error in device but we can fix it . . . . ;
            Log.i(TAG,"error in google service but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,availability,ERROR_DIALOG_REQUEST);
            dialog.show();

        } else {
            Log.i(TAG , "We Cannot Connect to play services in your device ");
        }

        return  false ;
    }
}
