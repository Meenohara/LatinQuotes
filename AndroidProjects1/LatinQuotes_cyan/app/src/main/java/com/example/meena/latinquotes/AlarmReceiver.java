package com.example.meena.latinquotes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Meena on 03-08-2016.
 */
public class AlarmReceiver  extends BroadcastReceiver {
    public void onReceive(Context k1, Intent k2) {
        // TODO Auto-generated method stub
        Toast.makeText(k1, "Alarm received!", Toast.LENGTH_LONG).show();


        NotificationManager notificationManager;
        PendingIntent pendingIntent;
        int NOTIFICATION_ID = 1;
        Notification notification;
        Intent mIntent = new Intent(k1, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(k1, 0, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(k1);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        notification = new NotificationCompat.Builder(k1)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.lq)
                .setTicker("ticker value")
                .setAutoCancel(true)
                .setPriority(8)
                .setSound(soundUri)
                .setContentTitle("Your Daily quote")
                .setContentText("Be inspired!").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL |
                Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND |
                Notification.DEFAULT_VIBRATE;
        notification.ledARGB = 0xFFFFA500;
        notification.ledOnMS = 800;
        notification.ledOffMS = 1000;
        notificationManager = (NotificationManager)k1.getSystemService(k1.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        Log.i("notif","Notifications sent.");







    }

}
