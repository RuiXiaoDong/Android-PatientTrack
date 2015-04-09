package com.motivus.ece.motivus;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class ScreenON_OFF_ACTIVITY extends Activity {
    long time;
    int thetime = 5;
    int transformedtime;
    int seconds;
    EditText e2;
    private NotificationManager mNotificationManager;
    Button btnSubmit;
    private TextView switchStatus;
    private Switch mySwitch;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onoff);

      switchStatus = (TextView) findViewById(R.id.switchStatus);
      mySwitch = (Switch) findViewById(R.id.mySwitch);

        //set the switch to ON

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    switchStatus.setText("Switch is currently ON");
                    onCreate();

                }else{
                    switchStatus.setText("Switch is currently OFF");
                }

            }
        });


        Button mapTrackButton = (Button) findViewById(R.id.button_onsubmit);
        mapTrackButton.setOnClickListener(

              new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent map = new Intent(v.getContext(), MainActivity.class);
                        startActivity(map);
                    }
                }
        );
    }

    public void onCreate() {


                    switchStatus.setText("Switch is currently ON");
                    System.out.println("onCreate1 ");
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
                    filter.addAction(Intent.ACTION_SCREEN_OFF);
                    BroadcastReceiver mReceiver = new ScreenReceiver(ScreenON_OFF_ACTIVITY.this);
                    registerReceiver(mReceiver, filter);
                    System.out.println("onCreate ");

       // setContentView(R.layout.activity_onoff);
  /*      TextView e2 = (TextView) findViewById(R.id.textView3);
        seconds = Integer.parseInt(e2.getText().toString());
        System.out.println(seconds);
        Intent intent = new Intent(getBaseContext(), ScreenReceiver.class);
        intent.putExtra("seconds", seconds);
        startActivity(intent);*/

    // initialize receiver

}

    /*    btnSubmit = (Button) findViewById(R.id.button);
        btnSubmit.setOnClickListener(ScreenON_OFF_ACTIVITY.this);*/




   /* @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ScreenReceiver.class);
        intent.putExtra("seconds", e2.getText().toString());
        startActivity(intent);
    }*/



    @Override
       protected void onPause() {
        // when the screen is about to turn off

        if (ScreenReceiver.screenOff) {
            // this is the case when onPause() is called by the system due to a screen state change
            System.out.println("SCREEN TURNED OFF");


        } else {
            // this is when onPause() is called when the screen state has not changed
            System.out.println("Still off ***");

        }

        super.onPause();

    }

    @Override
    protected void onResume() {
        // only when screen turns on
        if (!ScreenReceiver.screenOff) {
            // this is when onResume() is called due to a screen state change
            System.out.println("SCREEN TURNED ON");

        } else {
            // this is when onResume() is called when the screen state has not changed
            System.out.println("Still on ");
        }
        super.onResume();
    }

}

