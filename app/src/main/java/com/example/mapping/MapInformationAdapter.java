package com.example.mapping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapInformationAdapter implements GoogleMap.InfoWindowAdapter {
    private View view ;
    private Context context ;

    public MapInformationAdapter(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.map_information_layout,null);
    }

    private void putInformation(Marker marker , View view){
        String title = marker.getTitle();
        String description = marker.getSnippet() ;
        TextView textTitle = view.findViewById(R.id.text_title);
        TextView textDescription = view.findViewById(R.id.text_description);

        if (title != null){
            textTitle.setText(title);
        }
        if(description != null){
            textDescription.setText(description);
        }




    }

    @Override
    public View getInfoWindow(Marker marker) {
        putInformation(marker,view);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        putInformation(marker,view);
        return view;
    }
}
