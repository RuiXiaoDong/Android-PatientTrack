package com.motivus.ece.motivus;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


/**
 * Created by Jackie on 2015-03-25.
 */
public class UpdateService extends Service {


    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver(getApplicationContext());
        registerReceiver(mReceiver, filter);


    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}