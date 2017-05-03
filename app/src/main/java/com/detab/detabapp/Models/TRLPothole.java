package com.detab.detabapp.Models;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by thiago on 03/04/2017.
 */

public class TRLPothole
{
    public double Lat;
    public double Lng;
    public double Deep;

    //[0] = Distance
    //[1] = Initial Bearing
    //[2] = Final Bearing
    float[] results;

    //Determine if this pothole is pinned on maps
    public boolean isPinned;

    //Determine if the user was notified about this pothole
    public boolean wasNotified;

    public Marker Marker;

    public TRLPothole(double lng, double lat)
    {
        Lat = lat;
        Lng = lng;
        results = new float[3];
    }

    public float GetDistance()
    {
        return results[0];
    }
}
