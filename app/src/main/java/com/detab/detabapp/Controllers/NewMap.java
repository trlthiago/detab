package com.detab.detabapp.Controllers;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.detab.detabapp.Models.TRLLocation;
import com.detab.detabapp.Providers.GPSTracker;
import com.detab.detabapp.Providers.GetPotholesTask;
import com.detab.detabapp.Providers.TCPServerService;
import com.detab.detabapp.Providers.TRLService;
import com.detab.detabapp.Providers.TRLServiceConnection;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by thiago on 06/04/2017.
 */

public class NewMap extends AppCompatActivity implements OnMapReadyCallback, LocationListener
{
    private GoogleMap _map;
    private GPSTracker gps;
    private TRLSpeaker _speaker;
    private TRLTextToSpeech _tts;
    private Marker _currLocationMarker;
    private List<TRLLocation> _potholes;
    private LatLng _coords;
    public TCPServerService _tcpService;
    boolean isBound;

    public ServiceConnection _tcpServiceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder binder)
        {
            Toast.makeText(getApplicationContext(), "onServiceConnected", Toast.LENGTH_SHORT).show();
            _tcpService = ((TCPServerService.TCPServerBinder) binder).GetService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName className)
        {
            _tcpService = null;
            isBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newmap);
        mapFragment.getMapAsync(this);

        AskPermissions();
//startService(new Intent(getBaseContext(), TCPServerService.class));
        Intent bindServiceIntent = new Intent(this, TCPServerService.class);
        bindService(bindServiceIntent, _tcpServiceConnection, Context.BIND_AUTO_CREATE);

        gps = new GPSTracker(this);
        _speaker = new TRLSpeaker(getApplicationContext());
        _tts = new TRLTextToSpeech(getApplicationContext());
    }

    @Override
    public void onDestroy()
    {
        Log.v("TRL", "onDestroy");
        unbindService(_tcpServiceConnection);
        super.onDestroy();
    }

    private void AskPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        _map = googleMap;

        _map.setOnMapClickListener(
                new GoogleMap.OnMapClickListener()
                {
                    @Override
                    public void onMapClick(LatLng arg0)
                    {
                        Toast.makeText(getApplicationContext(), arg0.latitude + "-" + arg0.longitude, Toast.LENGTH_SHORT).show();
                        _coords = arg0;
                    }
                }
        );

        //noinspection MissingPermission
        _map.setMyLocationEnabled(true);

        Location currentLocation = gps.getLocation();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 17);
        _map.moveCamera(cameraUpdate);

        _potholes = GetPotholes(currentLocation.getLatitude(), currentLocation.getLongitude());

        //TODO: Move it to when postion change. On ready the API return should contain the distance calculated!
        ComputeDistance(_potholes, currentLocation.getLatitude(), currentLocation.getLongitude());

        RenderPotholes(_potholes);

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        _currLocationMarker = _map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Current Position").rotation(0));
    }

    private void RenderPotholes(List<TRLLocation> potholes)
    {
        for (TRLLocation item : potholes)
        {
            _map.addMarker(new MarkerOptions()
                    .position(new LatLng(item.Lat, item.Lng))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    .title("Pothole " + item.results[0]));
        }
    }

    private List<TRLLocation> GetNearPotholes()
    {
        List<TRLLocation> nearPotholes = new ArrayList<>();

        for (TRLLocation x : _potholes)
        {
            if (x.results[0] < 15.0)
            {
                nearPotholes.add(x);
            }
        }

        Collections.sort(nearPotholes, new Comparator<TRLLocation>()
        {
            @Override
            public int compare(TRLLocation a, TRLLocation b)
            {
                if (a.results[0] < b.results[0]) return -1;
                if (a.results[0] > b.results[0]) return 1;
                return 0;
            }
        });

        return nearPotholes;
    }

    private void ComputeDistance(List<TRLLocation> potholes, double lat, double lng)
    {
        for (TRLLocation item : potholes)
        {
            item.results = new float[3];
            Location.distanceBetween(lat, lng, item.Lat, item.Lng, item.results);
        }
    }

    private List<TRLLocation> GetPotholes(double lat, double lng)
    {
        if (_potholes == null)
        {
            GetPotholesTask j = new GetPotholesTask(lat, lng);
            //j.delegate = this;
            AsyncTask<String, Void, List<TRLLocation>> a = j.execute("http://educandoomundo.tk/api/pothole");
            try
            {
                _potholes = a.get();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (ExecutionException e)
            {
                e.printStackTrace();
            }
        }
        return _potholes;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        ComputeDistance(_potholes, location.getLatitude(), location.getLongitude());

        List<TRLLocation> list = GetNearPotholes();

        if (list.size() > 0)
        {
            _speaker.PlaySound();
            _tts.Speak("Pothole in " + (int) list.get(0).results[0] + " meters");
            Toast.makeText(this, "Pothole in " + (int) list.get(0).results[0] + " meters", Toast.LENGTH_SHORT).show();
        }

        //_currentLocation = location;

        //place marker at current position
        //mGoogleMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

//        if (currLocationMarker != null)
//        {
//            currLocationMarker.setPosition(latLng);
//            //currLocationMarker.remove();
//        }

//        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        currLocationMarker = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
//                .title("Current Position").rotation(90));

        //zoom to current position:
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

        //noinspection MissingPermission
        _map.setMyLocationEnabled(true);
        _map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

    public void btnDefinePothole_Click(View v)
    {
        _tcpService.Listen();
        _tcpService.getLocalIpAddress();
        Toast.makeText(getApplicationContext(), _tcpService.Ping(), Toast.LENGTH_SHORT).show();
    }
}
