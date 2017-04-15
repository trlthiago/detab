package com.detab.detabapp.Controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.detab.detabapp.Models.PermissionHelper;
import com.detab.detabapp.Models.PotholeCollection;
import com.detab.detabapp.Models.TRLPothole;
import com.detab.detabapp.Providers.GPSTracker;
import com.detab.detabapp.Providers.TCPServerService;
import com.detab.detabapp.Providers.TRLSpeaker;
import com.detab.detabapp.Providers.TRLTextToSpeech;
import com.detab.detabapp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by thiago on 06/04/2017.
 */

public class NewMap extends AppCompatActivity implements OnMapReadyCallback, LocationListener
{
    //region Providers Variables
    private GoogleMap _map;
    private GPSTracker gps;
    private TRLSpeaker _speaker;
    private TRLTextToSpeech _tts;
    public TCPServerService _tcpService;
    //endregion

    //region Local Helper Variables
    boolean isBound;
    private LatLng _coords;
    private Marker _currLocationMarker;
    private PotholeCollection _potholes;
    //endregion

    //region Service Connectors
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
    //endregion

    //region Events

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newmap);
        mapFragment.getMapAsync(this);

        PermissionHelper.AskPermissions(this);

        //startService(new Intent(getBaseContext(), TCPServerService.class));
        Intent bindServiceIntent = new Intent(this, TCPServerService.class);
        //startService(bindServiceIntent);
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
        Intent stopservice = new Intent(this, TCPServerService.class);
        //stopService(stopservice);
        super.onDestroy();
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

        _potholes = new PotholeCollection(currentLocation.getLatitude(), currentLocation.getLongitude());

        //TODO: Move it to when postion change. On ready the API return should contain the distance calculated!
        _potholes.ComputeDistance(currentLocation.getLatitude(), currentLocation.getLongitude());

        RenderPotholes(_potholes.GetAll(), true);

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        _currLocationMarker = _map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Current Position").rotation(0));
    }

    @Override
    public void onLocationChanged(Location location)
    {
        _potholes.ComputeDistance(location.getLatitude(), location.getLongitude());

        RenderPotholes(_potholes.GetAll());

        List<TRLPothole> list = _potholes.GetNearPotholes();

        if (list.size() > 0)
        {
            _speaker.PlaySound();
            _tts.Speak("Pothole in " + (int) list.get(0).GetDistance() + " meters");
            Toast.makeText(this, "Pothole in " + (int) list.get(0).GetDistance() + " meters", Toast.LENGTH_SHORT).show();
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
        _tcpService.Listen(_potholes, gps);
        Toast.makeText(getApplicationContext(), _tcpService.Ping(), Toast.LENGTH_SHORT).show();
    }

    //endregion

    private void RenderPotholes(List<TRLPothole> potholes)
    {
        RenderPotholes(potholes, false);
    }

    private void RenderPotholes(List<TRLPothole> potholes, boolean firstRender)
    {
        BitmapDescriptor markerType = firstRender
                ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
                : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);

        for (TRLPothole item : potholes)
        {
            if (item.isPinned)
            {
                item.Marker.setPosition(new LatLng(item.Lat, item.Lng));
                item.Marker.setTitle("Pothole " + item.GetDistance());
            } else
            {
                item.Marker = _map.addMarker(new MarkerOptions()
                        .position(new LatLng(item.Lat, item.Lng))
                        .icon(markerType)
                        .title("Pothole " + item.GetDistance()));
                item.isPinned = true;
            }

        }
    }
}
