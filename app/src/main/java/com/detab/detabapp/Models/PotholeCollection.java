package com.detab.detabapp.Models;

import android.location.Location;
import android.os.AsyncTask;

import com.detab.detabapp.Providers.GetPotholesTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by thiago on 14/04/2017.
 */

public class PotholeCollection
{
    private volatile List<TRLPothole> _potholes;
    private double currentLat;
    private double currentLng;

    public PotholeCollection()
    {

    }

    public PotholeCollection(double lat, double lng)
    {
        UpdateFromNetwork(lat, lng);
    }

    public List<TRLPothole> GetAll()
    {
        return _potholes;
    }

    public void ComputeDistance(double lat, double lng)
    {
        currentLat = lat;
        currentLng = lng;

        for (TRLPothole item : _potholes)
        {
            Location.distanceBetween(lat, lng, item.Lat, item.Lng, item.results);
        }
    }

    public List<TRLPothole> GetNearPotholes()
    {
        List<TRLPothole> nearPotholes = new ArrayList<>();

        for (TRLPothole x : _potholes)
        {
            if (!x.wasNotified && x.results[0] < 15.0)
            {
                nearPotholes.add(x);
            }
        }

        Collections.sort(nearPotholes, new Comparator<TRLPothole>()
        {
            @Override
            public int compare(TRLPothole a, TRLPothole b)
            {
                if (a.results[0] < b.results[0]) return -1;
                if (a.results[0] > b.results[0]) return 1;
                return 0;
            }
        });

        return nearPotholes;
    }

    private List<TRLPothole> UpdateFromNetwork(double lat, double lng)
    {
        currentLat = lat;
        currentLng = lng;

        if (_potholes == null)
        {
            GetPotholesTask j = new GetPotholesTask(lat, lng);
            //j.delegate = this;
            AsyncTask<String, Void, List<TRLPothole>> a = j.execute("http://educandoomundo.tk/api/pothole");
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

    public void DeclareNewPothole(double lat, double lng)
    {
        TRLPothole pothole = new TRLPothole(lng, lat);
        Location.distanceBetween(currentLat, currentLng, lat, lng, pothole.results);
        _potholes.add(pothole);
    }
}
