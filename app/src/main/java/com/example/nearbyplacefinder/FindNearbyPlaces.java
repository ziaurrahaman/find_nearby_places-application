package com.example.nearbyplacefinder;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindNearbyPlaces extends AsyncTask<Object, String, String> {

    String googleplacesData, url;
    GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleplacesData;
    }

    @Override
    protected void onPostExecute(String s) {

        showAllNearbyPlaces(s);

    }


    public void showAllNearbyPlaces(String jsonData)
    {

       List<HashMap<String, String>> nearbyPlaceList = new ArrayList<>();
       HashMap<String, String> hashMap;
       DataParser dataParser = new DataParser();
       nearbyPlaceList = dataParser.parse(jsonData);

        for(int i= 0; i<nearbyPlaceList.size();  i++)
        {
            hashMap = nearbyPlaceList.get(i);
            String placeName = hashMap.get("placename");
            String vicinity = hashMap.get("vicinity");
            double lat = Double.parseDouble(hashMap.get("latitude"));
            double lng = Double.parseDouble(hashMap.get("longitude"));

            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(placeName +":  "+vicinity);
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));





        }
    }


}
