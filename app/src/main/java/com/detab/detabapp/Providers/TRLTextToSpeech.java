package com.detab.detabapp.Providers;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import java.util.Locale;

/**
 * Created by thiago on 27/03/2017.
 */

public class TRLTextToSpeech implements TextToSpeech.OnInitListener
{
    Context _ctx;
    TextToSpeech tts;

    public TRLTextToSpeech(Context ctx)
    {
        _ctx = ctx;
        tts = new TextToSpeech(_ctx, this);
    }

    @Override
    public void onInit(int status)
    {
        if (status != TextToSpeech.ERROR)
        {
            tts.setLanguage(Locale.US);
            //tts.setLanguage(Locale.getDefault());
        }
    }

    public void Speak(String text)
    {
        Bundle bundle = new Bundle();

        tts.setPitch(1.3f);
        tts.setSpeechRate(1f);
        int result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, "123");
        if(result != 0)
            Toast.makeText(_ctx, "An error has occurred to emmit the alert voice.", Toast.LENGTH_SHORT).show();
    }
}
