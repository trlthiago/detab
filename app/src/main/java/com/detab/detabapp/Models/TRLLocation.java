package com.detab.detabapp.Models;

/**
 * Created by thiago on 03/04/2017.
 */

public class TRLLocation
{
    public double Lat;
    public double Lng;
    public double Distance;
    public float[] results;

    public TRLLocation(double lng, double lat)
    {
        Lat = lat;
        Lng = lng;
    }
}
