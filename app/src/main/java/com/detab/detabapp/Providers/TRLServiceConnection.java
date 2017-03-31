package com.detab.detabapp.Providers;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class TRLServiceConnection implements ServiceConnection
{
    public TRLService trlService;
    public boolean isServiceBound = false;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        TRLService.MyBinder binder = (TRLService.MyBinder) service;
        trlService = binder.getService();
        isServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        isServiceBound = false;
    }
}
