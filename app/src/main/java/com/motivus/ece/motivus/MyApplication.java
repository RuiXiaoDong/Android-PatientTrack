package com.motivus.ece.motivus;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jackie on 2015-03-30.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();

        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
