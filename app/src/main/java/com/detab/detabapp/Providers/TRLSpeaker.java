package com.detab.detabapp.Providers;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

/**
 * Created by thiago on 21/03/2017.
 */

public class TRLSpeaker
{
    private Context _ctx;

    public TRLSpeaker(Context ctx)
    {
        _ctx = ctx;
    }

    public void PlaySound()
    {
        try
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(_ctx, notification);
            r.play();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
