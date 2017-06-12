package com.ecoapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverwatchFragment extends Fragment implements View.OnClickListener {

    static ListView listView;
    static OverwatchAdapter overwatchAdapter;

    static LinearLayout stampLayout;
    static ImageView stampImageView;

    static String currentUsername;
    static String currentName;
    static String currentObjectId;

    static ProgressBar progressBar;
    static ImageView noImgsMsg;

    static int i;
    static List<ParseObject> recivedImages;

    public OverwatchFragment() {
        // Required empty public constructor
    }

    public static void updateList() {
        listView.setAdapter(overwatchAdapter);
    }


    public static void addMore() {
        listView.setAdapter(null);
        progressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> owQuery = ParseQuery.getQuery("images");
        owQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getObjectId());

        owQuery.whereLessThan("likes", 6);
        owQuery.whereLessThan("dislikes", 6);

        owQuery.whereNotEqualTo("likeUsers", ParseUser.getCurrentUser().getObjectId());
        owQuery.whereNotEqualTo("dislikeUsers", ParseUser.getCurrentUser().getObjectId());
        owQuery.whereNotEqualTo("skipUsers", ParseUser.getCurrentUser().getObjectId());

        owQuery.setLimit(5);


        owQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                recivedImages = objects;
                overwatchAdapter.removeAllObjectInList();

                i = 0;
                SetData();
            }
        });
    }

    private static void SetData() {
        if (recivedImages.size() != 0) {
            ParseQuery<ParseUser> uQuery = new ParseUser().getQuery();

            uQuery.whereEqualTo("objectId", recivedImages.get(i).get("username"));
            uQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    currentUsername = object.getUsername();
                    currentName = object.get("name") + " " + object.get("surname");
                    currentObjectId = recivedImages.get(i).getObjectId();
                    ParseFile imageParse = (ParseFile) recivedImages.get(i).get("image");

                    Log.i("EcoApp", "URL: " + imageParse.getUrl());
                    Log.i("EcoApp", "i: " + i);

                    DownloadImage task = new DownloadImage();
                    Bitmap imageBitmap = null;
                    try {
                        imageBitmap = task.execute(imageParse.getUrl().toString()).get();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    } catch (ExecutionException e1) {
                        e1.printStackTrace();
                    }
                    OverwatchDataProvider overwatchDataProvider = new OverwatchDataProvider(currentName, currentUsername, imageBitmap, currentObjectId);
                    overwatchAdapter.add(overwatchDataProvider);

                    if (i < recivedImages.size() - 1) {
                        i++;
                        SetData();
                    } else {
                        listView.setAdapter(overwatchAdapter);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            noImgsMsg.setVisibility(View.VISIBLE);
        }
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
        return inflater.inflate(R.layout.fragment_overwatch, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        overwatchAdapter = new OverwatchAdapter(getContext(), R.layout.overwatch_item);
        listView = (ListView) getActivity().findViewById(R.id.overwatchListView);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        noImgsMsg = (ImageView) getActivity().findViewById(R.id.noImgsMsg);

        stampLayout = (LinearLayout) getActivity().findViewById(R.id.stampLayout);
        stampImageView = (ImageView) getActivity().findViewById(R.id.stampImageView);
        stampLayout.setTranslationY(200);

        addMore();
        Log.i("EcoApp", "Load more First time");
        EcoWall.changingFragments = false;
    }

    @Override
    public void onClick(View view) {
        return;
    }

    public static class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }
}
