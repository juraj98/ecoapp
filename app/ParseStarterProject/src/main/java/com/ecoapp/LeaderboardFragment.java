package com.ecoapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    static LinearLayout stampLayout;
    static ImageView stampImageView;
    Spinner globalOrFollowsSpinner;
    Spinner pointsSpinner;
    ListView listView;
    int globalOrFollows = 0;
    int points = 0;

    ArrayAdapter<CharSequence> globalOfFollowsAdapter;
    ArrayAdapter<CharSequence> pointsAdapter;

    String pointsTypeText = null;
    String pointsType = null;

    LeaderboardAdapter leaderboardAdapter;

    public LeaderboardFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        leaderboardAdapter = new LeaderboardAdapter(getContext(), R.layout.leaderboard_item);

        listView = (ListView) getActivity().findViewById(R.id.leaderboardListView);

        stampLayout = (LinearLayout) getActivity().findViewById(R.id.stampLayout);
        stampImageView = (ImageView) getActivity().findViewById(R.id.stampImageView);
        stampLayout.setTranslationY(200);

        globalOrFollowsSpinner = (Spinner) getActivity().findViewById(R.id.leaderboardGlobalOrFollow);
        pointsSpinner = (Spinner) getActivity().findViewById(R.id.leaderboardPoints);

        globalOfFollowsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.globalOrFollows, android.R.layout.simple_spinner_item);
        pointsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.pointsTypes, android.R.layout.simple_spinner_item);

        globalOfFollowsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        globalOrFollowsSpinner.setAdapter(globalOfFollowsAdapter);
        pointsSpinner.setAdapter(pointsAdapter);

        globalOrFollowsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                globalOrFollows = i;
                leaderboardQuery();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        pointsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                points = i;
                leaderboardQuery();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        final float width = metrics.widthPixels;
        LinearLayout.LayoutParams layoutParamsWidth50 = new LinearLayout.LayoutParams(
                (int) (width / 2), 60);

        globalOrFollowsSpinner.setLayoutParams(layoutParamsWidth50);
        pointsSpinner.setLayoutParams(layoutParamsWidth50);

        leaderboardQuery();

    }

    private void leaderboardSetup(List<ParseUser> objects, ParseException e) {
        if (e == null) {
            Log.i("EcoApp", "Leaderboard Setup");
            boolean userAdded = false;
            int number = 1;
            int i = 0;
            int beforePoints;
            leaderboardAdapter.removeAllObjectInList();
            if (objects == null) {
                LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(1, ParseUser.getCurrentUser().getUsername(), (int) ParseUser.getCurrentUser().get(pointsType));
                leaderboardAdapter.add(leaderboardDataProvider);
                getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
                listView.setAdapter(leaderboardAdapter);
            } else {
                if (objects.size() == 0) {
                    beforePoints = -1;
                } else {
                    beforePoints = (int) objects.get(i).get(pointsType);
                }
                int currentPoints = -1;


                //First Row Adding
                if (!userAdded && (int) ParseUser.getCurrentUser().get(pointsType) >= beforePoints) {
                    beforePoints = (int) ParseUser.getCurrentUser().get(pointsType);
                    LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(number, ParseUser.getCurrentUser().getUsername(), (int) ParseUser.getCurrentUser().get(pointsType));
                    leaderboardAdapter.add(leaderboardDataProvider);
                    userAdded = true;
                } else {
                    LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(number, objects.get(i).getUsername(), (int) objects.get(i).get(pointsType));
                    leaderboardAdapter.add(leaderboardDataProvider);
                    i++;
                }
                //Other Rows Adding
                for (; i < objects.size(); i++) {
                    ParseObject object = objects.get(i);
                    currentPoints = (int) object.get(pointsType);
                    if (!userAdded && (int) ParseUser.getCurrentUser().get(pointsType) >= currentPoints) {
                        if (beforePoints != currentPoints) {
                            LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(++number, ParseUser.getCurrentUser().getUsername(), (int) ParseUser.getCurrentUser().get(pointsType));
                            leaderboardAdapter.add(leaderboardDataProvider);
                            beforePoints = currentPoints;
                        } else {
                            LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(-1, ParseUser.getCurrentUser().getUsername(), (int) ParseUser.getCurrentUser().get(pointsType));
                            leaderboardAdapter.add(leaderboardDataProvider);
                        }

                        userAdded = true;
                        i--;
                    } else {
                        if (beforePoints != currentPoints) {
                            //resultsText += ++number + ".";
                            LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(++number, objects.get(i).getUsername(), (int) objects.get(i).get(pointsType));
                            leaderboardAdapter.add(leaderboardDataProvider);
                        } else {
                            LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(-1, objects.get(i).getUsername(), (int) objects.get(i).get(pointsType));
                            leaderboardAdapter.add(leaderboardDataProvider);
                        }
                    }
                }

                if (!userAdded) {
                    if (beforePoints != currentPoints) {
                        LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(++number, ParseUser.getCurrentUser().getUsername(), (int) ParseUser.getCurrentUser().get(pointsType));
                        leaderboardAdapter.add(leaderboardDataProvider);
                    } else {
                        LeaderboardDataProvider leaderboardDataProvider = new LeaderboardDataProvider(-1, ParseUser.getCurrentUser().getUsername(), (int) ParseUser.getCurrentUser().get(pointsType));
                        leaderboardAdapter.add(leaderboardDataProvider);
                    }

                }
                listView.setAdapter(leaderboardAdapter);
                getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
                Log.i("EcoApp", "ChangingFragments = " + EcoWall.changingFragments);
                EcoWall.changingFragments = false;
            }
        } else {
            Toast.makeText(getContext(), "Something went wrong with reciveing leaderboard. Error code " + e.getCode(), Toast.LENGTH_SHORT).show();
            getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
            EcoWall.changingFragments = false;
        }
    }

    private void leaderboardQuery() {

        ParseQuery<ParseUser> uQuery = new ParseUser().getQuery();

        switch (points) {
            case 0:
                uQuery.orderByDescending("ecoPoints");
                pointsTypeText = "Eco Points";
                pointsType = "ecoPoints";
                break;
            case 1:
                uQuery.orderByDescending("totalPoints");
                pointsTypeText = "Total Points";
                pointsType = "totalPoints";
                break;
            case 2:
                uQuery.orderByDescending("SupportPoints");
                pointsTypeText = "Support Points";
                pointsType = "SupportPoints";
                break;
        }
        List<String> listOfFollows = (List<String>) ParseUser.getCurrentUser().get("friends");
        if (listOfFollows == null && globalOrFollows == 1) {
            leaderboardSetup(null, null);
        } else {
            if (globalOrFollows == 1) {
                listOfFollows.add(ParseUser.getCurrentUser().getObjectId());
                uQuery.whereContainedIn("objectId", listOfFollows);
            }
            uQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
            uQuery.setLimit(9);
            uQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    leaderboardSetup(objects, e);
                }
            });
        }
    }

}
