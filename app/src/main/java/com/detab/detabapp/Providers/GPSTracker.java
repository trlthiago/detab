package com.detab.detabapp.Providers;

/**
 * Created by thiago on 20/03/2017.
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class GPSTracker extends Service //implements LocationListener
{
    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    public Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000;

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;

    public GPSTracker(Context context)
    {
        this.mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled)
        {
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(callGPSSettingIntent);
        }
        isGPSEnabled = true;//força
        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        getLocation();
        //Toast.makeText(mContext, location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    public static void CheckIsGpsProviderEnabled(Context mContext)
    {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled)
        {
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            callGPSSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(callGPSSettingIntent);
        }
    }

    public boolean CheckPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return false;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return false;

        return true;
    }

    @SuppressWarnings({"MissingPermission"})
    public Location getLocation()
    {
        try
        {
            if (isGPSEnabled) // if GPS Enabled get lat/long using GPS Services
            {
                this.canGetLocation = true;

                if (location == null)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) mContext);
                    Thread.sleep(100);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) mContext);

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location == null)
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (location == null)
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    if (location == null)
                        location = new Location(LocationManager.GPS_PROVIDER); //Vai retornar 0,0 mas é só pra não estourar exception

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            } else if (isNetworkEnabled)
            {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) mContext);
                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null)
                        {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

            } else
            {
                // no network provider is enabled
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
//    public void stopUsingGPS()
//    {
//        if (locationManager != null)
//        {
//            locationManager.removeUpdates(GPSTracker.this);
//        }
//    }
    public double getLatitude()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude()
    {
        if (location != null)
        {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

//    @Override
//    public void onLocationChanged(Location location)
//    {
//        //Toast.makeText(mContext, location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(mContext, "Loc: " + location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onProviderDisabled(String provider)
//    {
//    }
//
//    @Override
//    public void onProviderEnabled(String provider)
//    {
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras)
//    {
//    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
}
