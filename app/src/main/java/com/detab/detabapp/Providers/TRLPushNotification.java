package com.detab.detabapp.Providers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.detab.detabapp.R;

/**
 * Created by thiago on 29/03/2017.
 */

public class TRLPushNotification
{
    Context _ctx;

    public TRLPushNotification(Context ctx)
    {
        _ctx = ctx;
    }

    public void Notify(String title, String content)
    {
        NotificationManager manager = (NotificationManager) _ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(_ctx)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        manager.notify(0, notification);
    }

    public void Remove()
    {
        //https://developer.android.com/reference/android/app/NotificationManager.html#cancelAll()
        NotificationManager manager = (NotificationManager) _ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

//    public NotifyForeground()
//    {
//        Notification notification = new Notification(R.drawable.common_google_signin_btn_icon_light, getText(R.string.ticker_text), System.currentTimeMillis());
//        Intent notificationIntent = new Intent(this, ExampleActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(_ctx, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(this, getText(R.string.notification_title), getText(R.string.notification_message), pendingIntent);
//        startForeground(ONGOING_NOTIFICATION_ID, notification);
//    }
}

