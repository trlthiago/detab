package com.detab.detabapp;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by thiago on 27/03/2017.
 */

public class TRLTextToSpeech implements TextToSpeech.OnInitListener
{
    TextToSpeech tts;

    public TRLTextToSpeech(Context ctx)
    {
        tts = new TextToSpeech(ctx, this);
    }

    @Override
    public void onInit(int status)
    {
        if (status != TextToSpeech.ERROR)
        {
            tts.setLanguage(Locale.US);
        }
    }

    public void Speak(String text)
    {
        Bundle bundle = new Bundle();
        tts.setLanguage(Locale.getDefault());
        tts.setPitch(1.3f);
        tts.setSpeechRate(1f);
        int result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
