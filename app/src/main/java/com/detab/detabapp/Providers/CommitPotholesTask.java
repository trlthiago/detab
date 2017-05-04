package com.detab.detabapp.Providers;

import android.os.AsyncTask;
import android.util.Log;

import com.detab.detabapp.Controllers.GeneralTests;
import com.detab.detabapp.Models.BulkPotholeModel;
import com.detab.detabapp.Models.TRLPothole;

import java.util.List;

public class CommitPotholesTask extends AsyncTask<String, Void, Void>
{
    public GeneralTests delegate;
    private List<TRLPothole> potholes;

    public CommitPotholesTask(List<TRLPothole> p)
    {
        potholes = p;
    }

    protected Void doInBackground(String... urls)
    {
        TRLHttpClient client = new TRLHttpClient();
        Log.d("detab", urls[0]);
        client.Post("http://educandoomundo.tk/api/bulkpothole", new BulkPotholeModel(potholes));
        return null;
    }

    protected void onPostExecute(List<TRLPothole> list)
    {

    }
}
