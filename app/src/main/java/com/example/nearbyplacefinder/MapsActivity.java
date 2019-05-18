package com.example.nearbyplacefinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLoacation;
    private Marker currentUserLocationMarker;
    private static final int Requst_User_Location_Code = 99;
    private double latitude, longitude;
    private int PROXIMITY_RADIOUS = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onClick(View view)
    {
        String hospital = "hospital", restaurant = "restaurant", school = "school", gas_stations = "Gas stations";
        Object googleData[] = new Object[2];
        FindNearbyPlaces findNearbyPlaces = new FindNearbyPlaces();
        String url;

        switch (view.getId())
        {
            case R.id.search_button_id:
                EditText adressField = (EditText)findViewById(R.id.search_text_id);
                String adress = adressField.getText().toString();
                List<Address> address_list_returned_by_geoCoder ;
                MarkerOptions userMarkerOption = new MarkerOptions();
                if(!TextUtils.isEmpty(adress))
                {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        address_list_returned_by_geoCoder = geocoder.getFromLocationName(adress,6);
                        if(address_list_returned_by_geoCoder!= null)
                        {
                            for(int i = 0; (i < address_list_returned_by_geoCoder.size()); i++)
                            {

                                Address userAddress = address_list_returned_by_geoCoder.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                userMarkerOption.position(latLng);
                                userMarkerOption.title(adress);
                                userMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mMap.addMarker(userMarkerOption);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                            }
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"Sorry location not found",Toast.LENGTH_SHORT).show();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else
                {
                    Toast.makeText(getApplicationContext(),"Please enter any location",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.hospital_button_id:

                mMap.clear();
                url = getUrl(latitude, longitude, hospital);
                googleData[0] = mMap;
                googleData[1] = url;
                findNearbyPlaces.execute(googleData);
                Toast.makeText(getApplicationContext(),"Searching nearby hospital....",Toast.LENGTH_SHORT).show();
                break;
            case R.id.restaurantbutton_id:

                mMap.clear();
                url = getUrl(latitude, longitude, restaurant);
                googleData[0] = mMap;
                googleData[1] = url;
                findNearbyPlaces.execute(googleData);

                Toast.makeText(getApplicationContext(),"Searching nearby restaurant....",Toast.LENGTH_SHORT).show();
                break;
            case R.id.schoolbutton_id:

                mMap.clear();
                url = getUrl(latitude, longitude, school);
                googleData[0] = mMap;
                googleData[1] = url;
                findNearbyPlaces.execute(googleData);

                Toast.makeText(getApplicationContext(),"Searching nearby school....",Toast.LENGTH_SHORT).show();
                break;
            case R.id.gasstation_button_id:

                mMap.clear();
                url = getUrl(latitude, longitude, gas_stations);
                googleData[0] = mMap;
                googleData[1] = url;
                findNearbyPlaces.execute(googleData);

                Toast.makeText(getApplicationContext(),"Searching nearby gas stations....",Toast.LENGTH_SHORT).show();
                break;






        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=" + latitude + "," + longitude);
        stringBuilder.append("&radious="+PROXIMITY_RADIOUS);
        stringBuilder.append("&type="+nearbyPlace);
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key=");

        return stringBuilder.toString();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }
    public boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Requst_User_Location_Code);
            }else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, Requst_User_Location_Code);
            }
            return false;
        }else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode)
        {
            case Requst_User_Location_Code:
                if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else
                {
                    Toast.makeText(getApplicationContext(),"Permission Denied.....",Toast.LENGTH_SHORT).show();
                }
                return;


        }
    }

    protected synchronized void buildGoogleApiClient(){

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLoacation = location;
        if(currentUserLocationMarker != null){
            currentUserLocationMarker.remove();
        }
        //Problem may occur in line 145
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentUserLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(getApplicationContext(),"There was an error connecting the client to the Server",Toast.LENGTH_SHORT).show();

    }
}
