package com.ecoapp;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
public class EcoWallFragment extends Fragment {

    static RecyclerView recyclerView;
    static RecyclerView.LayoutManager layoutManager;
    static List<EcoWallDataProvider> list = new ArrayList<>();

    static LinearLayout stampLayout;
    static ImageView stampImageView;

    static EcoWallAdapter ecoWallAdapter;

    static int skippedFeed;

    static Context context;

    static String currentUsername;
    static String currentName;
    static String currentObjectId;

    static ProgressBar progressBar;

    static int i;
    static List<ParseObject> recivedImages;

    static ImageView noFeedMsg;

    public EcoWallFragment() {
        // Required empty public constructor
    }

    public static void loadNextFeed() {


        List<String> friendsList = (List) ParseUser.getCurrentUser().get("friends");
        if (friendsList == null) {
            Log.i("EcoApp", "friendsList = null");
            progressBar.setVisibility(View.GONE);
            noFeedMsg.setVisibility(View.VISIBLE);
        } else {
            Log.i("EcoApp", friendsList.toString());
            ParseQuery<ParseObject> iQuery = ParseQuery.getQuery("images");

            iQuery.whereContainedIn("username", friendsList);
            iQuery.orderByAscending("createdAt");
            iQuery.setLimit(20);
            iQuery.setSkip(20 * skippedFeed);

            iQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() == 0) {
                            progressBar.setVisibility(View.GONE);
                            noFeedMsg.setVisibility(View.VISIBLE);
                        } else {
                            recivedImages = objects;
                            i = 0;
                            skippedFeed++;
                            setData();
                        }
                    } else {
                        Toast.makeText(context, "Something went wrong while reciveing images", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public static void setData() {
        if (i < recivedImages.size()) {
            ParseQuery<ParseUser> uQuery = new ParseUser().getQuery();
            uQuery.whereEqualTo("objectId", recivedImages.get(i).get("username"));

            uQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {

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

                        list.add(new EcoWallDataProvider(imageBitmap, currentName, currentUsername, currentObjectId));
                        i++;
                        setData();
                    } else {
                        Toast.makeText(context, "Something went wrong while reciveing images", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            ecoWallAdapter = new EcoWallAdapter(list);
            recyclerView.setAdapter(ecoWallAdapter);
            ecoWallAdapter.setImagesHeight();
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
                    for (int j = 0; j < stamps.size(); j++) {
                        if (stamps.get(j)) {
                            tempArray.set(j, true);
                            stampImageView.setImageResource(StampClass.getStampURL(j));
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
        return inflater.inflate(R.layout.fragment_eco_wall, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.ecoWallRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        stampLayout = (LinearLayout) getActivity().findViewById(R.id.stampLayout);
        stampImageView = (ImageView) getActivity().findViewById(R.id.stampImageView);
        stampLayout.setTranslationY(200);

        noFeedMsg = (ImageView) getActivity().findViewById(R.id.noFeedMsg);
        skippedFeed = 0;
        loadNextFeed();

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) //check for scroll down
//                {
//                    loadNextFeed();
//                }
//            }
//        });
        EcoWall.changingFragments = false;
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
