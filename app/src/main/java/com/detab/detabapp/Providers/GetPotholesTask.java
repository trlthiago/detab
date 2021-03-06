package com.detab.detabapp.Providers;

import android.os.AsyncTask;
import android.util.Log;

import com.detab.detabapp.Controllers.GeneralTests;
import com.detab.detabapp.Models.PotholeCollection;
import com.detab.detabapp.Models.TRLPothole;

import java.util.List;

public class GetPotholesTask extends AsyncTask<String, Void, List<TRLPothole>>
{
    public GeneralTests delegate;
    private double _lat, _lng;
    PotholeCollection _potholeCollection;

    public GetPotholesTask(double lat, double lng, PotholeCollection potholeCollection)
    {
        _lat = lat;
        _lng = lng;
        _potholeCollection = potholeCollection;
    }

    protected List<TRLPothole> doInBackground(String... urls)
    {
        TRLHttpClient client = new TRLHttpClient();
        //List<TRLPothole> obj = client.Get("http://educandoomundo.tk/api/pothole?lat=" + _lat + "&lng=" + _lng);
        String url = String.format("%s?lat=%s&lng=%s", urls[0], _lat, _lng);
        Log.d("detab", url);
        List<TRLPothole> obj = client.Get(url);

        return obj;
    }

    protected void onPostExecute(List<TRLPothole> list)
    {
        _potholeCollection.UpdateFromNetworkCallBack(list);
    }
}

