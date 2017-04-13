package com.detab.detabapp.Providers;

import android.os.AsyncTask;
import com.detab.detabapp.Controllers.GeneralTests;
import com.detab.detabapp.Models.ReturnedObject;
import com.detab.detabapp.Models.TRLLocation;
import java.util.List;

public class GetPotholesTask extends AsyncTask<String, Void, List<TRLLocation>>
{
    public GeneralTests delegate;
    private double _lat, _lng;

    public GetPotholesTask(double lat, double lng)
    {
        _lat = lat;
        _lng = lng;
    }

    protected List<TRLLocation> doInBackground(String... urls)
    {
        TRLHttpClient client = new TRLHttpClient();
        List<TRLLocation> obj = client.TGet("http://educandoomundo.tk/api/pothole?lat=" + _lat + "&lng=" + _lng);
        return obj;
    }

    protected void onPostExecute(List<TRLLocation> list)
    {
        //delegate.UpdateScreen(retorno);
    }
}
