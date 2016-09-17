package com.oldworld.meena.latinquotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.ResolveInfo;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    BufferedReader fileReader;
    int linenum = 0;
    String line;
    String mailQuote;
    int randomInteger;

    TimePicker myTimePicker;
    Button buttonstartSetDialog;
    Button cancelAlarm;
    Button noto;

    final static int RQS_1 = 1;
    //Uri notification ;
    Ringtone r ;
    boolean alarm = false;
    TextView textAlarmPrompt;
    TimePickerDialog timePickerDialog;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    SharedPreferences settings;
    String almtime = "Alarm not set";
    boolean notifonly = false;

    public static final String PREFKEY = "MYprefs Key";
    public static final String ALMTIME = "Alarmtime";
    public static final String ALMSTIME= "AlarmSettime";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.AppTheme_Dark);
        }

        TextView affirm = (TextView)findViewById(R.id.interpre);
        randomquote(affirm);
        String[] timearray;

        settings =  this.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
        almtime= settings.getString(ALMTIME, almtime);
        textAlarmPrompt = (TextView) findViewById(R.id.alarmprompt);
        timearray = almtime.split("\\s");
        /*textAlarmPrompt.setText( "Daily alert time "
                +timearray[3]+ "hours" + "\n" + "***\n");*/

        buttonstartSetDialog = (Button) findViewById(R.id.startalarm);
        buttonstartSetDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openTimePickerDialog(false);
            }
        });
        cancelAlarm = (Button) findViewById(R.id.cancelalarm);

        cancelAlarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

    }


    public void randomquote(View v) {
        try {

            fileReader = new BufferedReader(
                    new InputStreamReader(getAssets().open("latinquotes.txt")));

            while (fileReader.readLine() != null) {
                linenum++;
            }

            System.out.println("Total number of lines : " + linenum);

            fileReader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        Random random = new Random();

        TextView tv = (TextView) findViewById(R.id.text2);

        randomInteger = random.nextInt(linenum) + 1;

        linenum = 0;

        try {

            fileReader = new BufferedReader(
                    new InputStreamReader(getAssets().open("latinquotes.txt")));
            StringBuilder strBuilder = new StringBuilder();

            TextView intp = (TextView) findViewById(R.id.interpre);
            TextView engl = (TextView) findViewById(R.id.english);

            while ((line = fileReader.readLine()) != null) {
                linenum++;
                if (linenum == randomInteger) {
                    mailQuote=line;
                    String[] str_array = line.split("\\-\\(Meaning\\)");
                    String quote = str_array[0];
                    String meaning = str_array[1];
                    engl.setText(meaning);
                    intp.setText(quote);
                }
            }

            fileReader.close();
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
    public void email(View v) {

        List<Intent> intentShareList = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
//TODO try out android.intent.category.default in intent filter in Manifest file
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(shareIntent, 0);

        for (ResolveInfo resInfo : resolveInfoList) {
            String packageName = resInfo.activityInfo.packageName;
            String name = resInfo.activityInfo.name;
            Log.i("Email method", "Package Name : " + packageName);
            Log.i("Email method", "Name : " + name);

            if (packageName.contains("com.facebook") ||
                    packageName.contains("com.twitter.android") ||
                    packageName.contains("com.google.android.apps.plus") ||
                    packageName.contains("com.google.android.gm") ||
                    packageName.contains("com.whatsapp") ||
                    packageName.contains("chat") ||
                    packageName.contains("messenger") ||
                    packageName.contains("email"))
            {

                if (name.contains("com.twitter.android.DMActivity")) {
                    continue;
                }

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Your Quote for the day");
                intent.putExtra(Intent.EXTRA_TEXT, mailQuote);
                intentShareList.add(intent);
            }
        }

        if (intentShareList.isEmpty()) {
            Toast.makeText(MainActivity.this, "No apps to share !", Toast.LENGTH_SHORT).show();
        } else {
            Intent chooserIntent = Intent.createChooser(intentShareList.remove(0), "Share via");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentShareList.toArray(new Parcelable[]{}));
            startActivity(chooserIntent);
        }
    }


    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(MainActivity.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();
        Toast.makeText(getBaseContext(), "Time", Toast.LENGTH_LONG).show();

    }
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }

            Toast.makeText(getBaseContext(), "Time set!", Toast.LENGTH_LONG).show();
            setAlarm(calSet);

        }
    };

    private void setAlarm(Calendar targetCal) {

        String[] timearray;

        almtime = targetCal.getTime().toString();

        timearray = almtime.split("\\s");


       /* textAlarmPrompt.setText("\n\n***\n" + "Daily alert set at "
                + timearray[3] + " hours" + "\n" + "***\n");
*/
        long almstime =targetCal.getTimeInMillis();

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ALMTIME, almtime);
        editor.putLong(ALMSTIME,almstime );


        editor.commit();



        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("notification only",notifonly);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), RQS_1, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                1000 * 60 * 60 *24, pendingIntent);
        //TODO investigate :this is draining on the battery but using setinexactalarm seems to be too inexact proportionally to the interval specified

        cancelAlarm.setVisibility(View.VISIBLE);


        Toast.makeText(getBaseContext(), "In set alarm", Toast.LENGTH_LONG).show();
    }


    private void cancelAlarm() {

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), RQS_1, intent, PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
        //       pendingIntent);

        if (pendingIntent != null) {
            Log.e("ALARM", "------------STOPPED----------------");
            Toast.makeText(this, "Alarm canceled!", Toast.LENGTH_LONG).show();
            alarmManager.cancel(pendingIntent);
            PendingIntent.getBroadcast(
                    getBaseContext(), RQS_1, intent, 0).cancel();
            cancelAlarm.setVisibility(View.INVISIBLE);
            buttonstartSetDialog.setVisibility(View.VISIBLE);
        }
        else
        {
            Toast.makeText(this, "No Alarm to cancel!", Toast.LENGTH_LONG).show();
        }
        textAlarmPrompt.setText("Currently no daily alert set");

    }



}

//todo   <item name="android:textColorPrimary">@color/textColorPrimaryInverse</item>
//
/*<item name="android:textColorSecondary">@color/textColorSecondaryInverse</item>
<item name="android:textColorPrimaryInverse">@color/textColorPrimary</item>
<item name="android:textColorSecondaryInverse">@color/textColorSecondary</item>

<item name="android:background">@color/colorAccentInverse</item>*/
