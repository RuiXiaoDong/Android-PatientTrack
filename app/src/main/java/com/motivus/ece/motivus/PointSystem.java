package com.motivus.ece.motivus;

/**
 * Created by dongx on 17/03/2015.
 */
public class PointSystem {
    public static int appointmentPoint(boolean done, boolean late){
        if(done) {
            if(late)
                return 50;
            else
                return 100;
        }
        else
            return 0;
    }
}
