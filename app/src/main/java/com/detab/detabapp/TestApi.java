package com.detab.detabapp;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.NameValuePair;

public class TestApi extends AppCompatActivity
{
    TRLTextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test_api);

        tts = new TRLTextToSpeech(getApplicationContext());
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
}