package com.ecoapp;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StampsFragment extends Fragment {

    static LinearLayout stampLayout;
    static ImageView stampImageView;
    int i = 0;

    public StampsFragment() {
        // Required empty public constructor
    }

    public static void StampsCheck() {
        Log.i("EcoApp", "StampCheck");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("stamps", params, new FunctionCallback<ArrayList<Boolean>>() {
            public void done(ArrayList<Boolean> stamps, ParseException e) {
                Log.i("EcoApp", "StampsDone");
                Log.i("EcoApp", "Stamp exeption: " + e);
                if (e == null) {
                    Log.i("EcoApp", "Stamp Arr: " + stamps.toString());
                    ArrayList<Boolean> tempArray = (ArrayList) ParseUser.getCurrentUser().get("stamps");
                    for (int i = 0; i < stamps.size(); i++) {
                        if (stamps.get(i)) {
                            tempArray.set(i, true);
                            stampImageView.setImageResource(StampClass.getStampURL(i));
                            stampLayout.animate().alpha(1).translationY(-25).setDuration(1500).start();
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    stampLayout.animate().alpha(0).setDuration(500).start();
                                }
                            }, 2500);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    stampLayout.setTranslationY(25);
                                    stampLayout.setAlpha(0);
                                }
                            }, 500);
                        }
                    }
                    Log.i("EcoApp", "Adding stamps");
                    ParseUser.getCurrentUser().put("stamps", tempArray);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.i("EcoApp", "User saved");
                        }
                    });
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stamps, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List stampsList = (List) ParseUser.getCurrentUser().get("stamps");
        ImageView imageView;

        stampLayout = (LinearLayout) getActivity().findViewById(R.id.stampLayout);
        stampImageView = (ImageView) getActivity().findViewById(R.id.stampImageView);
        stampLayout.setTranslationY(200);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        WindowManager wm = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        wm.getDefaultDisplay().getMetrics(displayMetrics);
//        ViewGroup.LayoutParams params;
//        imageSize = (displayMetrics.widthPixels - 20 * (int) getResources().getDisplayMetrics().density) / 5;
//        //Testing width fix
//        Display d = ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        Log.i("EcoWall", "Display width: " + getActivity().getWindowManager().getDefaultDisplay().getWidth());
        ViewGroup.LayoutParams params;
        int imageSize = (int) Math.ceil((getActivity().getWindowManager().getDefaultDisplay().getWidth()-20*getResources().getDisplayMetrics().density) / 5);
//        Log.i("EcoApp", "Display width: " + getActivity().getWindowManager().getDefaultDisplay().getWidth());
//        Log.i("EcoApp", "Real value: " + (getActivity().getWindowManager().getDefaultDisplay().getWidth()-20*getResources().getDisplayMetrics().density) / 5);
//        Log.i("EcoApp", "ImageSize: " + imageSize);
        imageSize--;

        //EcoPoints Stamps

        imageView = (ImageView) getActivity().findViewById(R.id.stampEcoPoints1);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_ecopoints_1));

        //And manually setting other ImageViews below
        //...

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampEcoPoints5);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_ecopoints_5));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampEcoPoints10);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_ecopoints_10));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampEcoPoints25);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_ecopoints_25));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampEcoPoints50);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_ecopoints_50));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampEcoPoints100);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_ecopoints_100));
        //Streak Stamps

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampStreak7);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_streak_7));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampStreak30);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_streak_30));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampStreak122);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_streak_122));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampStreak183);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_streak_183));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampStreak365);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_streak_365));

        //Support points Stamps

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints1);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_1));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints5);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_5));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints10);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_10));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints25);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_25));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints50);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_50));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints100);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_100));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints250);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_250));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints500);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_500));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampSupportPoints1000);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_supportpoints_1000));

        //Total points Stamps

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints1);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_1));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints5);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_5));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints10);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_10));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints25);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_25));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints50);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_50));

        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints100);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_100));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints250);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_250));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints500);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_500));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints1000);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_1000));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints2500);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_2500));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints5000);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_5000));


        i++;
        imageView = (ImageView) getActivity().findViewById(R.id.stampTotalPoints10000);
        params = imageView.getLayoutParams();
        params.height = imageSize;
        params.width = imageSize;
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(getGrayscale((Boolean) stampsList.get(i), R.drawable.stamp_totalpoints_10000));

        EcoWall.changingFragments = false;

    }

    public Bitmap getGrayscale(boolean listItem, int index) {

        Drawable drawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getResources().getDrawable(index, getContext().getTheme());
        } else {
            drawable = getResources().getDrawable(index);
        }

        if (listItem) {
            Log.i("EcoApp", "Adding colored " + i);
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap colorBitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap grayscaleBitmap = Bitmap.createBitmap(
                    colorBitmap.getWidth(), colorBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(grayscaleBitmap);
            Paint p = new Paint();
            ColorMatrix cm = new ColorMatrix();

            cm.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
            p.setColorFilter(filter);
            c.drawBitmap(colorBitmap, 0, 0, p);
            return grayscaleBitmap;
        }
    }
}
