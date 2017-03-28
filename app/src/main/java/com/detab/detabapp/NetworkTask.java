package com.detab.detabapp;

import android.os.AsyncTask;

public class NetworkTask extends AsyncTask<String, Void, ReturnedObject>
{
    private Exception exception;
    public TestApi delegate;

    protected ReturnedObject doInBackground(String... urls)
    {
//        try
//        {
            TRLHttpClient client = new TRLHttpClient();
            ReturnedObject obj = client.TGet("https://jsonplaceholder.typicode.com/posts/1");
            return obj;
//        } catch (Exception e)
//        {
//            this.exception = e;
//            return null;
//        }
    }



    protected void onPostExecute(ReturnedObject retorno)
    {
        delegate.UpdateScreen(retorno);
    }
}
