package com.ecoapp;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class StatusFragment extends Fragment {


    static LinearLayout stampLayout;
    static ImageView stampImageView;

    public StatusFragment() {
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

        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stampLayout = (LinearLayout) getActivity().findViewById(R.id.stampLayout);
        stampImageView = (ImageView) getActivity().findViewById(R.id.stampImageView);
        stampLayout.setTranslationY(200);

        TextView ecoPointsTextView = (TextView) getActivity().findViewById(R.id.statusEcoPoints);
        ecoPointsTextView.setText("Eco Points: " + ParseUser.getCurrentUser().get("ecoPoints").toString());

        TextView totalPointsTextView = (TextView) getActivity().findViewById(R.id.statusTotalPoints);
        totalPointsTextView.setText("Total Points: " + ParseUser.getCurrentUser().get("totalPoints").toString());

        TextView supportPointsTextView = (TextView) getActivity().findViewById(R.id.statusSupportPoints);
        supportPointsTextView.setText("Support Points: " + ParseUser.getCurrentUser().get("SupportPoints").toString());

        if (ParseUser.getCurrentUser().get("Streak") == "1") {
            TextView streakTextView = (TextView) getActivity().findViewById(R.id.statusStreak);
            streakTextView.setText(ParseUser.getCurrentUser().get("streak").toString() + " days streak");
        } else {
            TextView streakTextView = (TextView) getActivity().findViewById(R.id.statusStreak);
            streakTextView.setText(ParseUser.getCurrentUser().get("streak").toString() + " days streak");
        }

        Date date = (Date) ParseUser.getCurrentUser().get("lastPhotoDate");

        TextView dateText = (TextView) getActivity().findViewById(R.id.statusLastPhotoAdded);
        if (date == null) {
            dateText.setText("No photo was taken yet.");
        } else {
            SimpleDateFormat df = new SimpleDateFormat("d.M.yyyy - HH:mm");
            dateText.setText("Your last photo was taken " + df.format(date));
        }

        TextView rankTextView = (TextView) getActivity().findViewById(R.id.statusRank);
        rankTextView.setText("Rank: " + getRank());
        EcoWall.changingFragments = false;
    }

    private String getRank() {
        int ecoPoints = (int) ParseUser.getCurrentUser().get("ecoPoints");
        int supportPoints = (int) ParseUser.getCurrentUser().get("SupportPoints");
        int totalPoints = (int) ParseUser.getCurrentUser().get("totalPoints");
        int maxStreak = (int) ParseUser.getCurrentUser().get("maxStreak");

        if (ecoPoints >= 182 && supportPoints >= 100 && totalPoints >= 500 && maxStreak >= 182) {
            return "10";
        } else if (ecoPoints >= 50 && supportPoints >= 100 && totalPoints >= 75 && maxStreak >= 30) {
            return "9";
        } else if (ecoPoints >= 50 && supportPoints >= 75 && totalPoints >= 75 && maxStreak >= 7) {
            return "8";
        } else if (ecoPoints >= 30 && supportPoints >= 50 && totalPoints >= 50 && maxStreak >= 7) {
            return "Eco Legend(7)";
        } else if (ecoPoints >= 25 && supportPoints >= 10 && totalPoints >= 50 && maxStreak >= 7) {
            return "MR. Fusion(6)";
        } else if (ecoPoints >= 25 && supportPoints >= 10 && totalPoints >= 50) {
            return "Eco Fighter(5)";
        } else if (ecoPoints >= 10 && supportPoints >= 10) {
            return "Eco Beast(4)";
        } else if (ecoPoints >= 10 && supportPoints >= 5) {
            return "Trash Bully(3)";
        } else if (ecoPoints >= 10) {
            return "Tin Can Pulverizer(2)";
        } else {
            return "Sprout(1)";
        }
    }
}
