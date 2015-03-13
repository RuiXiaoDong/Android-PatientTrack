package com.motivus.ece.motivus;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.Date;

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
        //Incoming SMS
        IntentFilter smsFilter = new IntentFilter();
        smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, smsFilter);

        //Outgoing SMS
        Uri SMS_STATUS_URI = Uri.parse("content://sms");
        this.getContentResolver().registerContentObserver(SMS_STATUS_URI, true, smsObserver);

        //Incoming and out going phone call
        IntentFilter phoneFilter = new IntentFilter();
        phoneFilter.addAction("android.intent.action.PHONE_STATE");
        phoneFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(phoneReceiver, phoneFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
        unregisterReceiver(phoneReceiver);
    }

    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
                Toast.makeText(getApplicationContext(), "Incoming SMS",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final BroadcastReceiver phoneReceiver = new BroadcastReceiver() {
        private int lastState = TelephonyManager.CALL_STATE_IDLE;
        private Date callStartTime;
        private boolean isIncoming;
        private String savedNumber;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            }
            else{
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;
                if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    state = TelephonyManager.CALL_STATE_IDLE;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    state = TelephonyManager.CALL_STATE_RINGING;
                }
                onCallStateChanged(context, state, number);
            }
        }

        protected void onIncomingCallStarted(Context ctx, String number, Date start) {
            Toast.makeText(getApplicationContext(), "Incoming phone",
                    Toast.LENGTH_SHORT).show();
        }

        protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        }

        protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
            Toast.makeText(getApplicationContext(), "Outgoing phone",
                    Toast.LENGTH_SHORT).show();
        }

        protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        }

        protected void onMissedCall(Context ctx, String number, Date start) {
            Toast.makeText(getApplicationContext(), "Misiing phone",
                    Toast.LENGTH_SHORT).show();
        }

        public void onCallStateChanged(Context context, int state, String number) {
            if(lastState == state){
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    savedNumber = number;
                    onIncomingCallStarted(context, number, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                    if(lastState != TelephonyManager.CALL_STATE_RINGING){
                        isIncoming = false;
                        callStartTime = new Date();
                        onOutgoingCallStarted(context, savedNumber, callStartTime);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if(lastState == TelephonyManager.CALL_STATE_RINGING){
                        //Ring but no pickup-  a miss
                        onMissedCall(context, savedNumber, callStartTime);
                    }
                    else if(isIncoming){
                        onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    }
                    else{
                        onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                    }
                    break;
            }
            lastState = state;
        }
    };

    private final ContentObserver smsObserver = new ContentObserver(new Handler()) {
        public boolean deliverSelfNotifications() {
            return true;
        }

        public void onChange(boolean selfChange) {
            try{
                Toast.makeText(getApplicationContext(), "Outgoing SMS",
                        Toast.LENGTH_SHORT).show();
            }
            catch(Exception sggh){
            }
            super.onChange(selfChange);
        }
    };
}
