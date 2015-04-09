package com.motivus.ece.motivus;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean screenOff;
    long time;
    int thetime = 1;
    int transformedtime;

    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int numMessages = 0;
    private Context context;
    public ScreenReceiver(Context context){
        this.context=context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    //    transformedtime=intent.getExtras().getInt("seconds");
        System.out.println("onReceive ");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
            System.out.println("SCREEN TURNED OFF on BroadcastReceiver");

            transformedtime = thetime * 1000;
            CounterClass time = new CounterClass(transformedtime, 1000);
            time.start();


        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
            System.out.println("SCREEN TURNED ON on BroadcastReceiver");
        }
        Intent i = new Intent(context, UpdateService.class);
        i.putExtra("screen_state", screenOff);
        context.startService(i);


    }

}