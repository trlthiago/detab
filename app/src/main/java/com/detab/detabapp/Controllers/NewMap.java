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
import android.widget.EditText;
import android.widget.Toast;

import com.detab.detabapp.Models.PermissionHelper;
import com.detab.detabapp.Models.PotholeCollection;
import com.detab.detabapp.Models.TRLPothole;
import com.detab.detabapp.Providers.GPSTracker;
import com.detab.detabapp.Providers.PotholeRenderProvider;
import com.detab.detabapp.Providers.TCPServerService;
import com.detab.detabapp.Providers.TRLSpeaker;
import com.detab.detabapp.Providers.TRLTextToSpeech;
import com.detab.detabapp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by thiago on 06/04/2017.
 */

public class NewMap extends AppCompatActivity implements OnMapReadyCallback, LocationListener
{
    //region Providers Variables
    private GoogleMap _map;
    private GPSTracker _gps;
    private TRLSpeaker _speaker;
    private TRLTextToSpeech _tts;
    public TCPServerService _tcpService;
    private PotholeRenderProvider _potholeRender;
    //endregion

    //region Local Helper Variables
    public static String LOG_TAG = "detabapp";
    boolean isBound;
    private LatLng _coords;
    private Marker _currLocationMarker;
    private PotholeCollection _potholesCollection;
    private AppCompatActivity _self;
    //endregion

    //region Service Connectors
    public ServiceConnection _tcpServiceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder binder)
        {
            Log.i(LOG_TAG, "onServiceConnected");
            Toast.makeText(getApplicationContext(), "onServiceConnected", Toast.LENGTH_SHORT).show();
            _tcpService = ((TCPServerService.TCPServerBinder) binder).GetService();
            _tcpService.Listen(_potholesCollection, _gps, _tolerance, _checks, _readsInterval);
            _self.setTitle(_tcpService.GetLocalIpAddress());

            isBound = true;
        }

        public void onServiceDisconnected(ComponentName className)
        {
            _tcpService = null;
            isBound = false;
            Log.d(LOG_TAG, "TCP Service disconnected!");
        }
    };
    //endregion

    //region Events

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        _self = this;

        LoadExtras();

        _potholeRender = new PotholeRenderProvider(); //neste momento a var ta null, tem que ver se isso funciona ou nao...

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newmap);
        mapFragment.getMapAsync(this);

        PermissionHelper.AskPermissions(this);

        //tem que instanciar o _gps antes de dar bindind no servico. vide "ServiceConnection.onServiceConnected()"
        _gps = new GPSTracker(this);

        Log.d(LOG_TAG, "GPS instance on NewMap: " + _gps.toString());
        //startService(new Intent(getBaseContext(), TCPServerService.class));
        Intent bindServiceIntent = new Intent(this, TCPServerService.class);
        startService(bindServiceIntent);
        bindService(bindServiceIntent, _tcpServiceConnection, Context.BIND_AUTO_CREATE);

        _speaker = new TRLSpeaker(this);
        _tts = new TRLTextToSpeech(this);
        Log.d(LOG_TAG, "Fim do OnCreate");
    }

    private int _tolerance, _checks, _readsInterval;

    private void LoadExtras()
    {
        _checks = 2;
        _tolerance = 2;

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            String tmpTolerance = extras.getString("TOLERANCE");
            if (tmpTolerance != null && tmpTolerance.length() > 0)
                _tolerance = Integer.parseInt(tmpTolerance);

            String tmpChecks = extras.getString("CHECKS");
            if (tmpChecks != null && tmpChecks.length() > 0)
                _checks = Integer.parseInt(tmpChecks);

            String tmpReads = extras.getString("READSINTERVAL");
            if (tmpReads != null && tmpReads.length() > 0)
                _readsInterval = Integer.parseInt(tmpReads);
        }
    }

    @Override
    public void onDestroy()
    {
        Log.v(LOG_TAG, "onDestroy called!");

        unbindService(_tcpServiceConnection);
        Intent stopservice = new Intent(this, TCPServerService.class);
        //stopService(stopservice);
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        _map = googleMap;
        _potholeRender.SetMapProvider(_map);

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

        Location currentLocation = _gps.getLocation();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 17);
        _map.moveCamera(cameraUpdate);

        _potholesCollection = new PotholeCollection(this, _potholeRender, currentLocation.getLatitude(), currentLocation.getLongitude());

        //TODO: Move it to when postion change. On ready the API return should contain the distance calculated!
        _potholesCollection.ComputeDistance(currentLocation.getLatitude(), currentLocation.getLongitude());

        _potholeRender.Render(_potholesCollection.GetAll(), true);

        //LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//        _currLocationMarker = _map.addMarker(new MarkerOptions()
//                .position(latLng)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                .title("Current Position").rotation(0));
    }

    public void UpdateTxt(String distance)
    {
//        EditText editText = (EditText) findViewById(R.id.txtDistance);
//        editText.setText(distance);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        _gps.location = location; //CUIDADO, se tirar isso aqui, lá na tcpservice vai quebrar pois usa o gps.getlatitude e longetude... que vao ficar desatualizados
        _potholesCollection.CheckIfMustUpdate(location.getLatitude(), location.getLongitude());
        _potholesCollection.ComputeDistance(location.getLatitude(), location.getLongitude());

        _potholeRender.Render(_potholesCollection.GetAll());

        List<TRLPothole> list = _potholesCollection.GetNearPotholes();

        if (list.size() > 0)
        {
            _speaker.PlaySound();
            String text = list.size() == 1 ? String.format("Buraco em %d metros", (int) list.get(0).GetDistance()) : "Buracos à frente, cuidado!";
            _tts.Speak(text);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
        _potholeRender.Render(_potholesCollection.GetAll());
    }

    public void btnCommitPotholes_Click(View v)
    {
        _potholesCollection.CommitFoundPotholes();
        Toast.makeText(this, "Committed!", Toast.LENGTH_SHORT).show();
    }

    //endregion
}
