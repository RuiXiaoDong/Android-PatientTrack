package com.motivus.ece.motivus;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jackie on 2015-03-29.
 */
public class CounterClass extends CountDownTimer {
    private Context context;

    //private PowerManager.WakeLock wakeLock;
    public CounterClass(long millisInFuture, long countDownInterval) {

        super(millisInFuture, countDownInterval);

    }

    @Override
    public void onFinish() {




        System.out.println("Completed.");

        String phoneNo = "+16472013635";
        String message = "Hi, this a message from Motivus. Remember, stay close to your phone";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        /*Toast.makeText(MyApplication.getAppContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();*/

        //* Creates an explicit intent for an Activity in your app *//*
        // NotificationCard note=new NotificationCard();
        //  note.displayNotification();


//Do whatever you need right here

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MyApplication.getAppContext())
                        .setSmallIcon(R.drawable.motivus)
                        .setContentTitle("Motivus: Notification")
                        .setContentText("Are you there?");
        NotificationManager mNotificationManager = (NotificationManager) MyApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, mBuilder.build());

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(MyApplication.getAppContext(), notification);
            r.play();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void onTick(long millisUntilFinished) {

        long millis = millisUntilFinished;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));


        System.out.println(hms);


        //    textViewTime.setText(hms);
    }

  /*  private int sendNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(CounterClass.this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("motivus: appointment remind")
                        .setContentText("yyy");
       /* Intent resultIntent = new Intent(CounterClass.this, ScreenON_OFF_ACTIVITY.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ScreenON_OFF_ACTIVITY.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);*/
  /*      NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, mBuilder.build());

    }*/


    /*  private void showNotification(String eventtext, Context ctx) {



          // Set the icon, scrolling text and timestamp
          Notification notification = new Notification(R.drawable.ic_launcher,
                  "hhh", System.currentTimeMillis());

          // The PendingIntent to launch our activity if the user selects this
          // notification
          PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                  new Intent(ctx, ScreenON_OFF_ACTIVITY.class), 0);

          // Set the info for the views that show in the notification panel.
          notification.setLatestEventInfo(ctx, "Title", eventtext,
                  contentIntent);

          // Send the notification.
          mNM.notify("Title", 0, notification);*/

}