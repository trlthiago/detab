package com.detab.detabapp.Controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.detab.detabapp.Models.ReturnedObject;
import com.detab.detabapp.Providers.NetworkTask;
import com.detab.detabapp.Providers.TRLPushNotification;
import com.detab.detabapp.Providers.TRLService;
import com.detab.detabapp.Providers.TRLServiceConnection;
import com.detab.detabapp.Providers.TRLTextToSpeech;
import com.detab.detabapp.R;

public class GeneralTests extends AppCompatActivity
{
    TRLTextToSpeech tts;
    private TRLServiceConnection _serviceConnection; //= new ServiceConnection();

//    {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name)
//        {
//            isServiceBound = false;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service)
//        {
//            TRLService.MyBinder binder = (TRLService.MyBinder) service;
//            trlService = binder.getService();
//            isServiceBound = true;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test_api);

        tts = new TRLTextToSpeech(getApplicationContext());

        _serviceConnection = new TRLServiceConnection();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
//        Intent intent = new Intent(this, TRLService.class);
//        startService(intent);
//        bindService(intent, _serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (_serviceConnection.isServiceBound)
        {
            unbindService(_serviceConnection);
            _serviceConnection.isServiceBound = false;
        }
    }

    public void UpdateScreen(ReturnedObject result)
    {
        Toast.makeText(this, result.body, Toast.LENGTH_SHORT).show();
    }

    public void btnCallWebApi_Click(View v)
    {
        NetworkTask j = new NetworkTask();
        j.delegate = this;
        j.execute("https://jsonplaceholder.typicode.com/posts/1");
    }

    public void btnSpeech_Click(View v)
    {
        String text = ((EditText) findViewById(R.id.txtSpeech)).getText().toString();

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        tts.Speak(text);
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

    public void btnPlaySoundFile_Click(View v)
    {
        try
        {
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setDataSource(getUrl());
//            mediaPlayer.prepare();
//            mediaPlayer.start();

            //Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/intro.mp3")
            MediaPlayer mpintro = MediaPlayer.create(getApplicationContext(), R.raw.selectorsound);
            mpintro.setLooping(false);
            mpintro.start();
        } catch (Exception e)
        {

        }
    }

    public void btnPlaySound_Click(View v)
    {
        //final MediaPlayer mp = MediaPlayer.create(this, R.raw.soho);
        PlaySound();
    }

    public void btnShowNotification_Click(View v)
    {
        new TRLPushNotification(getApplicationContext()).Notify("Titulo Legal", "Conteudo Legal");
    }

    public void startService(View view)
    {
        startService(new Intent(getBaseContext(), TRLService.class));
    }

    public void stopService(View view)
    {
        stopService(new Intent(getBaseContext(), TRLService.class));
    }

    public void btnBindService_Click(View view)
    {
        Log.v("TRL", "Bind service");
        Intent intent = new Intent(this, TRLService.class);
        startService(intent);
        bindService(intent, _serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void btnUnbindService_Click(View view)
    {
        if (_serviceConnection.isServiceBound)
        {
            unbindService(_serviceConnection);
            _serviceConnection.isServiceBound = false;
        }
        Intent intent = new Intent(getApplicationContext(), TRLService.class);
        //Intent intent = new Intent(getApplicationContext(), _serviceConnection.trlService);
        stopService(intent);
    }

    public void btnInvokeService_Click(View view)
    {
        String result = "";
        try
        {
            result = _serviceConnection.trlService.GetString();
            _serviceConnection.trlService.PlaySound();
        } catch (Exception e)
        {
            result = e.toString();
        }

        ((EditText) findViewById(R.id.txtSpeech)).setText(result);
    }
}