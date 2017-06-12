package com.ecoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Juraj on 3.2.16.
 */
public class StampClass {

    public static int getStampURL(int index) {
        switch (index) {
            case 0:
                return R.drawable.stamp_ecopoints_1;
            case 1:
                return R.drawable.stamp_ecopoints_5;
            case 2:
                return R.drawable.stamp_ecopoints_10;
            case 3:
                return R.drawable.stamp_ecopoints_25;
            case 4:
                return R.drawable.stamp_ecopoints_50;
            case 5:
                return R.drawable.stamp_ecopoints_100;
            case 6:
                return R.drawable.stamp_streak_7;
            case 7:
                return R.drawable.stamp_streak_30;
            case 8:
                return R.drawable.stamp_streak_122;
            case 9:
                return R.drawable.stamp_streak_183;
            case 10:
                return R.drawable.stamp_streak_365;
            case 11:
                return R.drawable.stamp_supportpoints_1;
            case 12:
                return R.drawable.stamp_supportpoints_5;
            case 13:
                return R.drawable.stamp_supportpoints_10;
            case 14:
                return R.drawable.stamp_supportpoints_25;
            case 15:
                return R.drawable.stamp_supportpoints_50;
            case 16:
                return R.drawable.stamp_supportpoints_100;
            case 17:
                return R.drawable.stamp_supportpoints_250;
            case 18:
                return R.drawable.stamp_supportpoints_500;
            case 19:
                return R.drawable.stamp_supportpoints_1000;
            case 20:
                return R.drawable.stamp_totalpoints_1;
            case 21:
                return R.drawable.stamp_totalpoints_5;
            case 22:
                return R.drawable.stamp_totalpoints_10;
            case 23:
                return R.drawable.stamp_totalpoints_25;
            case 24:
                return R.drawable.stamp_totalpoints_50;
            case 25:
                return R.drawable.stamp_totalpoints_100;
            case 26:
                return R.drawable.stamp_totalpoints_250;
            case 27:
                return R.drawable.stamp_totalpoints_500;
            case 28:
                return R.drawable.stamp_totalpoints_1000;
            default:
                return -1;
        }
    }

}
