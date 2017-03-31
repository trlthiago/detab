package com.detab.detabapp.Providers;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by thiago on 29/03/2017.
 */

public class TRLService extends Service
{
    private final IBinder myBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.v("TRL", "onBind");
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.v("TRL", "onStartCommand");
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public class MyBinder extends Binder
    {
        public TRLService getService()
        {
            return TRLService.this;
        }
    }

    public String GetString()
    {
        return "Thiago";
    }

    public void PlaySound()
    {
        try
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

