package com.detab.detabapp;

import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import cz.msebera.android.httpclient.NameValuePair;

public class TestApi extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test_api);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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

        new TRLTextToSpeech(this).Speak(text);
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

    public void btnPlaySound_Click(View v)
    {
        //final MediaPlayer mp = MediaPlayer.create(this, R.raw.soho);
        PlaySound();
    }
}