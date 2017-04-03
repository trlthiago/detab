package com.detab.detabapp.Controllers;

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

import com.detab.detabapp.Providers.GPSTracker;
import com.detab.detabapp.Providers.TRLSpeaker;
import com.detab.detabapp.Providers.TRLTextToSpeech;
import com.detab.detabapp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener
{
    private GoogleMap mMap;
    private GPSTracker gps;
    private Marker currLocationMarker;
    private Location mCurrentLocation;
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    private TRLSpeaker _speaker;
    private TRLTextToSpeech _tts;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(this);
        _speaker = new TRLSpeaker(getApplicationContext());
        _tts = new TRLTextToSpeech(getApplicationContext());
    }

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
    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mCurrentLocation = gps.getLocation(); //getLastKnownLocation();

        Toast.makeText(this, "Location Null", Toast.LENGTH_SHORT).show();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 17);
        //mMap.moveCamera(cameraUpdate);
        mMap.animateCamera(cameraUpdate);
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

//        currLocationMarker = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
//                .title("Current Position").rotation(0));


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
        _speaker.PlaySound();
        _tts.Speak("New position detected!");

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

    public ArrayList<Location> GetPotholes()
    {
        /*
        "-50.9682021","-29.9476628"
        "-50.9688887","-29.9477092"
        "-50.9689531","-29.9476999"
        "-50.9692106","-29.9475733"
        "-50.9706724","-29.947844"
        "-50.9685078","-29.948009"
        "-50.9684783","-29.948425"
        "-50.9692293","-29.9473955"
        "-50.9698704","-29.9477743"
        "-50.97027","-29.9483181"
        "-50.9717989","-29.9480276"
        "-50.9676254","-29.9476093"
        "-50.9669709","-29.9475535"
        "-50.9640956","-29.9473304"
        "-50.968591","-29.9471352"
        "-50.9684193","-29.9488457"
        "-50.9687197","-29.9459917"
        "-50.9693205","-29.9462241"
        "-50.9704471","-29.9467912"
        "-50.9753609","-29.9473211"
        "-50.9787512","-29.9466053"
        "-50.9695137","-29.9494871"
        "-50.9849739","-29.9468098"
        "-50.9667563","-29.9515881"
         */
        ArrayList<Location> list = new ArrayList<Location>(){

        };
        LatLng ll = new LatLng(1,1);
        return null;
    }
}
