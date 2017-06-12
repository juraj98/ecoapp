package com.ecoapp;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraj on 22.1.16.
 */
public class OverwatchAdapter extends ArrayAdapter implements View.OnClickListener {


    List list = new ArrayList();
    List namesIndexes = new ArrayList();
    List names = new ArrayList();
    OverwatchDataProvider overwatchDataProvider;
    View currentAnimateView;
    DataHandler dataHandler;

    public OverwatchAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
        namesIndexes.add(list.indexOf(object));
    }

    public void removeAllObjectInList() {
        list.clear();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.overwatch_item, parent, false);
            dataHandler = new DataHandler();
            dataHandler.name = (TextView) row.findViewById(R.id.overwatchName);
            dataHandler.username = (TextView) row.findViewById(R.id.overwatchUsername);
            dataHandler.image = (ImageView) row.findViewById(R.id.overwatchPhoto);
            dataHandler.okBtn = (Button) row.findViewById(R.id.overwatchOkButton);
            dataHandler.skipBtn = (Button) row.findViewById(R.id.overwatchSkipButton);
            dataHandler.notOkBtn = (Button) row.findViewById(R.id.overwatchNotOkButton);
            dataHandler.cardView = (CardView) row.findViewById(R.id.cardView);
            row.setTag(dataHandler);
        } else {
            dataHandler = (DataHandler) row.getTag();
        }

        overwatchDataProvider = (OverwatchDataProvider) this.getItem(position);

        names.add(position, overwatchDataProvider.getObjectId());

        dataHandler.name.setText(overwatchDataProvider.getName());
        dataHandler.username.setText(overwatchDataProvider.getUsername());
        dataHandler.image.setImageBitmap(overwatchDataProvider.getImage());

        dataHandler.okBtn.setTag(overwatchDataProvider.getObjectId());
        dataHandler.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAnimateView = view;

                Log.i("EcoApp", "Tag of clicked: " + currentAnimateView.getTag());
                Log.i("EcoApp", "Location of tag in list: " + names.indexOf(currentAnimateView.getTag()));

                String imageObjectId = currentAnimateView.getTag().toString();

                ParseQuery<ParseObject> iQuery = ParseQuery.getQuery("images");
                iQuery.whereEqualTo("objectId", imageObjectId);

                iQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        Log.i("EcoApp", "Image found");

                        List likes = (List) object.get("likeUsers");
                        List dislikes = (List) object.get("dislikeUsers");
                        List skips = (List) object.get("skipUsers");
                        boolean likesContains = true;
                        boolean skipsContains = true;
                        boolean dislikesContains = true;

                        if (likes != null) {
                            likesContains = !likes.contains(ParseUser.getCurrentUser().getObjectId());
                        }
                        if (dislikes != null) {
                            dislikesContains = !dislikes.contains(ParseUser.getCurrentUser().getObjectId());
                        }
                        if (skips != null) {
                            skipsContains = !skips.contains(ParseUser.getCurrentUser().getObjectId());
                        }


                        Log.i("EcoApp", "Likes: " + likes + "| dislikes: " + dislikes + "| skips: " + skips);

                        if (likesContains || dislikesContains || skipsContains) {
                            if (likes == null) {
                                List<String> tempList = new ArrayList();
                                tempList.add(ParseUser.getCurrentUser().getObjectId());
                                object.put("likeUsers", tempList);
                            } else {
                                likes.add(ParseUser.getCurrentUser().getObjectId());
                                object.put("likeUsers", likes);
                            }
                            object.put("likes", (int) object.get("likes") + 1);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //Post Parse
                                        CardView animateView = (CardView) currentAnimateView.getParent().getParent();
                                        animateView.animate().translationXBy(100).alpha(0).setDuration(250).start();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                list.remove(names.indexOf(currentAnimateView.getTag()));

                                                Log.i("EcoApp", "List size is " + list.size());

                                                if(list.size() == 0){
                                                    OverwatchFragment.addMore();
                                                    Log.i("EcoApp", "Load more First time");
                                                } else {
                                                    OverwatchFragment.updateList();
                                                }

                                                ParseUser.getCurrentUser().put("SupportPoints", (int) ParseUser.getCurrentUser().get("SupportPoints") + 1);
                                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e != null) {
                                                            Toast.makeText(getContext(), "Something went wrong while saving your points. Please go to settings and synch your account or you may lose some of your points", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Log.i("EcoApp", "Current user supportpoints: " + ParseUser.getCurrentUser().get("SupportPoints"));
                                                            OverwatchFragment.StampsCheck();
                                                        }
                                                    }
                                                });
                                            }
                                        }, 30);
                                    } else {
                                        e.printStackTrace();
                                        Toast.makeText(getContext(), "Something went wrong while saving your rating", Toast.LENGTH_SHORT).show();
                                        list.remove(names.indexOf(currentAnimateView.getTag()));
                                        OverwatchFragment.updateList();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Something went wrong while saving your rating", Toast.LENGTH_SHORT).show();
                            list.remove(names.indexOf(currentAnimateView.getTag()));
                            OverwatchFragment.updateList();
                        }
                    }
                });
            }
        });

        dataHandler.notOkBtn.setTag(overwatchDataProvider.getObjectId());
        dataHandler.notOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentAnimateView = view;

                Log.i("EcoApp", "Tag of clicked: " + currentAnimateView.getTag());
                Log.i("EcoApp", "Location of tag in list: " + names.indexOf(currentAnimateView.getTag()));

                String imageObjectId = currentAnimateView.getTag().toString();

                ParseQuery<ParseObject> iQuery = ParseQuery.getQuery("images");
                iQuery.whereEqualTo("objectId", imageObjectId);

                iQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        Log.i("EcoApp", "Image found");

                        List likes = (List) object.get("likeUsers");
                        List dislikes = (List) object.get("dislikeUsers");
                        List skips = (List) object.get("skipUsers");
                        boolean likesContains = true;
                        boolean skipsContains = true;
                        boolean dislikesContains = true;

                        if (likes != null) {
                            likesContains = !likes.contains(ParseUser.getCurrentUser().getObjectId());
                        }
                        if (dislikes != null) {
                            dislikesContains = !dislikes.contains(ParseUser.getCurrentUser().getObjectId());
                        }
                        if (skips != null) {
                            skipsContains = !skips.contains(ParseUser.getCurrentUser().getObjectId());
                        }


                        Log.i("EcoApp", "Likes: " + likes + "| dislikes: " + dislikes + "| skips: " + skips);

                        if (likesContains || dislikesContains || skipsContains) {
                            if (dislikes == null) {
                                List<String> tempList = new ArrayList();
                                tempList.add(ParseUser.getCurrentUser().getObjectId());
                                object.put("dislikeUsers", tempList);
                            } else {
                                dislikes.add(ParseUser.getCurrentUser().getObjectId());
                                object.put("dislikeUsers", dislikes);
                            }
                            object.put("dislikes", (int) object.get("dislikes") + 1);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //Post Parse
                                        CardView animateView = (CardView) currentAnimateView.getParent().getParent();
                                        animateView.animate().translationXBy(-100).alpha(0).setDuration(250).start();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                list.remove(names.indexOf(currentAnimateView.getTag()));

                                                Log.i("EcoApp", "List size is " + list.size());
                                                if(list.size() == 0){
                                                    OverwatchFragment.addMore();
                                                    Log.i("EcoApp", "Load more First time");
                                                } else {
                                                    OverwatchFragment.updateList();
                                                }


                                                ParseUser.getCurrentUser().put("SupportPoints", (int) ParseUser.getCurrentUser().get("SupportPoints") + 1);
                                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e != null) {
                                                            Toast.makeText(getContext(), "Something went wrong while saving your points. Please go to settings and synch your account or you may lose some of your points", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Log.i("EcoApp", "Current user supportpoints: " + ParseUser.getCurrentUser().get("SupportPoints"));
                                                            OverwatchFragment.StampsCheck();
                                                        }
                                                    }
                                                });
                                            }
                                        }, 300);
                                    } else {
                                        e.printStackTrace();
                                        Toast.makeText(getContext(), "Something went wrong while saving your rating", Toast.LENGTH_SHORT).show();
                                        list.remove(names.indexOf(currentAnimateView.getTag()));
                                        OverwatchFragment.updateList();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Something went wrong while saving your rating", Toast.LENGTH_SHORT).show();
                            list.remove(names.indexOf(currentAnimateView.getTag()));
                            OverwatchFragment.updateList();
                        }
                    }
                });

            }
        });

        dataHandler.skipBtn.setTag(overwatchDataProvider.getObjectId());
        dataHandler.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentAnimateView = view;

                Log.i("EcoApp", "Tag of clicked: " + currentAnimateView.getTag());
                Log.i("EcoApp", "Location of tag in list: " + names.indexOf(currentAnimateView.getTag()));

                String imageObjectId = currentAnimateView.getTag().toString();

                ParseQuery<ParseObject> iQuery = ParseQuery.getQuery("images");
                iQuery.whereEqualTo("objectId", imageObjectId);

                iQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        Log.i("EcoApp", "Image found");

                        List likes = (List) object.get("likeUsers");
                        List dislikes = (List) object.get("dislikeUsers");
                        List skips = (List) object.get("skipUsers");
                        boolean likesContains = true;
                        boolean skipsContains = true;
                        boolean dislikesContains = true;

                        if (likes != null) {
                            likesContains = !likes.contains(ParseUser.getCurrentUser().getObjectId());
                        }
                        if (dislikes != null) {
                            dislikesContains = !dislikes.contains(ParseUser.getCurrentUser().getObjectId());
                        }
                        if (skips != null) {
                            skipsContains = !skips.contains(ParseUser.getCurrentUser().getObjectId());
                        }


                        Log.i("EcoApp", "Likes: " + likes + "| dislikes: " + dislikes + "| skips: " + skips);

                        if (likesContains || dislikesContains || skipsContains) {
                            if (skips == null) {
                                List<String> tempList = new ArrayList();
                                tempList.add(ParseUser.getCurrentUser().getObjectId());
                                object.put("skipUsers", tempList);
                            } else {
                                skips.add(ParseUser.getCurrentUser().getObjectId());
                                object.put("skipUsers", likes);
                            }
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //Post Parse
                                        CardView animateView = (CardView) currentAnimateView.getParent().getParent();
                                        animateView.animate().translationYBy(-100).alpha(0).setDuration(250).start();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                list.remove(names.indexOf(currentAnimateView.getTag()));

                                                Log.i("EcoApp", "List size is " + list.size());
                                                if(list.size() == 0){
                                                    OverwatchFragment.addMore();
                                                    Log.i("EcoApp", "Load more First time");
                                                } else {
                                                    OverwatchFragment.updateList();
                                                }


                                            }
                                        }, 300);
                                    } else {
                                        e.printStackTrace();
                                        Toast.makeText(getContext(), "Something went wrong while saving your rating", Toast.LENGTH_SHORT).show();
                                        list.remove(names.indexOf(currentAnimateView.getTag()));
                                        OverwatchFragment.updateList();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Something went wrong while saving your rating", Toast.LENGTH_SHORT).show();
                            list.remove(names.indexOf(currentAnimateView.getTag()));
                            OverwatchFragment.updateList();
                        }
                    }
                });
            }
        });
        return row;
    }

    @Override
    public void onClick(View view) {
        return;
    }

    static class DataHandler {
        TextView name;
        TextView username;
        ImageView image;

        Button okBtn;
        Button skipBtn;
        Button notOkBtn;

        CardView cardView;
    }

}
