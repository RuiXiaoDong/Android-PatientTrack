package com.motivus.ece.motivus;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class PhotoUsageTracingService extends Service {
    public PhotoUsageTracingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
                Toast.makeText(getApplicationContext(), "SMS Received",
                        Toast.LENGTH_SHORT).show();
            }
            else if(action.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
                Toast.makeText(getApplicationContext(), "Phone Call Received",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
}
