package com.detab.detabapp;

import android.os.AsyncTask;

public class NetworkTask extends AsyncTask<String, Void, ReturnedObject>
{
    public TestApi delegate;

    protected ReturnedObject doInBackground(String... urls)
    {
        TRLHttpClient client = new TRLHttpClient();
        ReturnedObject obj = client.TGet("https://jsonplaceholder.typicode.com/posts/1");
        return obj;
    }

    protected void onPostExecute(ReturnedObject retorno)
    {
        delegate.UpdateScreen(retorno);
    }
}
