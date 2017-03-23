package com.detab.detabapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener
{

    private GoogleMap mMap;
    private Location mCurrentLocation;
    private GPSTracker gps;
    private Marker currLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(this);
    }

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    private static final LatLng SYDNEY = new LatLng(-33.88, 151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);

    private Location getLastKnownLocation()
    {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_PHONE_STATE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_PHONE_STATE);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers)
        {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null)
            {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
            {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mCurrentLocation = gps.getLocation(); //getLastKnownLocation();


        Toast.makeText(
                this,
                "Location Null", Toast.LENGTH_SHORT).show();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 10);
        //mMap.moveCamera(cameraUpdate);
        mMap.animateCamera(cameraUpdate);
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        currLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .title("Current Position").rotation(0));


        // Move the camera instantly to Sydney with a zoom of 15.
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));

        // Zoom in, animating the camera.
        //mMap.animateCamera(CameraUpdateFactory.zoomIn());

        /*
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);

        LatLng gti = new LatLng(location.getLatitude(), location.getLongitude());

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(gti)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10000, null);
*/
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        //CircleOptions circle = new CircleOptions();
        //circle.radius(10.0);
        //mMap.addCircle(circle);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mCurrentLocation = location;
        Toast.makeText(this, "Main: " + location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_SHORT).show();
        //place marker at current position
        //mGoogleMap.clear();
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        if (currLocationMarker != null)
        {
            currLocationMarker.setPosition(latLng);
            //currLocationMarker.remove();
        }

//        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        currLocationMarker = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
//                .title("Current Position").rotation(90));

        //zoom to current position:
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }
}
