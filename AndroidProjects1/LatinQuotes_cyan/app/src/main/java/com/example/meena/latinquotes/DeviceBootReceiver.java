package com.example.meena.latinquotes;

/**
 * Created by Meena on 10-08-2016.
 */
/**
 * Created by Meena on 01-07-2016.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class DeviceBootReceiver extends BroadcastReceiver {

    SharedPreferences pref;
    long alarmtime;
    final static int RQS_1 = 1;
    Context co1;
    View v1;

    public DeviceBootReceiver(){}

    public DeviceBootReceiver(Context co, View v)
    {
        this.co1=co;
        this.v1 =v;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

        pref = context.getSharedPreferences(MainActivity.PREFKEY, context.MODE_PRIVATE);
        alarmtime = pref.getLong(MainActivity.ALMSTIME, alarmtime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.i("CheckVerify", "Alarm set " + sdf.format(alarmtime));


        //notif only to be coded for

        Intent almintent = new Intent(context, AlarmReceiver.class);//should this line be?

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 1, almintent, 0);
       /* PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        // alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
        //pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmtime,
                1000 * 60 * 60 *24, pendingIntent);


        if (pendingIntent != null) {
            //
            Toast.makeText(context, "Alarm reboot set!", Toast.LENGTH_LONG).show();
        }

        if(alarmManager==null){
            Toast.makeText(context, "Alarmmanager is null!", Toast.LENGTH_LONG).show();
        }
        //}
    }
}