package com.detab.detabapp;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.widget.Toast;
import java.util.Locale;
import java.util.Set;

/**
 * Created by thiago on 27/03/2017.
 */

public class TRLTextToSpeech implements TextToSpeech.OnInitListener
{
    TextToSpeech tts;
    Context _ctx;

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
        try
        {
            Bundle bundle = new Bundle();

            tts.setPitch(1.3f);
            tts.setSpeechRate(1f);
            int result = tts.speak(text, TextToSpeech.QUEUE_ADD, bundle, "123");
            Toast.makeText(_ctx, "Engine=" + tts.getDefaultEngine().toString(), Toast.LENGTH_SHORT).show();
            String ls = "";
            Set<Locale> languages = tts.getAvailableLanguages();

            if (languages != null)
            {
                for (Locale l : languages)
                {
                    ls += l.getDisplayName() + "; ";
                }
                Toast.makeText(_ctx, ls, Toast.LENGTH_SHORT).show();
            }
            Set<Voice> voices = tts.getVoices();
            ls = "";
            if (voices != null)
            {
                for (Voice v : voices)
                {
                    ls += v.getName() + "; ";
                }
                Toast.makeText(_ctx, ls, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e)
        {
            int a = 10;
        }
    }
}
