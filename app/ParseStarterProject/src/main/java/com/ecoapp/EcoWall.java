package com.ecoapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EcoWall extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static public boolean changingFragments = false;
    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP = 2;
    int selectedFragment = 0;
    private Uri picUri;
    private Bitmap image;

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }

    void updateData() {
        Log.i("EcoApp", "UpdateData()");

        ParseQuery<ParseUser> firstQuery = ParseUser.getQuery();
        firstQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        firstQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("name", object.get("name"));
                    currentUser.put("SupportPoints", object.get("SupportPoints"));
                    currentUser.put("ecoPoints", object.get("ecoPoints"));
                    currentUser.put("location", object.get("location"));
                    currentUser.put("username", object.get("username"));
                    currentUser.put("website", object.get("website"));
                    currentUser.put("surname", object.get("surname"));
                    currentUser.put("email", object.get("email"));
                    currentUser.put("totalPoints", object.get("totalPoints"));

                    if (object.get("lastPhotoDate") != null) {
                        currentUser.put("lastPhotoDate", object.get("lastPhotoDate"));
                    }
                    if (object.get("friends") != null) {
                        currentUser.put("friends", object.get("friends"));
                    }
                    if (object.get("profilePic") != null) {
                        currentUser.put("profilePic", object.get("profilePic"));
                    }
                    if (object.get("emailVerified") != null) {
                        currentUser.put("emailVerified", object.get("emailVerified"));
                    }

                    currentUser.put("stamps", object.get("stamps"));
                    currentUser.put("profileVisible", object.get("profileVisible"));


                    if (object.get("lastPhotoDate") == null) {
                        Log.i("EcoApp", "Photo not taken");
                        currentUser.put("streak", 0);
                    } else {
                        Date currentDate = new Date();
                        Calendar cal1 = Calendar.getInstance();
                        Calendar cal2 = Calendar.getInstance();
                        cal1.setTime((Date) currentUser.get("lastPhotoDate"));
                        cal2.setTime(currentDate);
                        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                            Log.i("EcoApp", "This day was photo taken");
                            currentUser.put("streak", object.get("streak"));
                        } else if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && (cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR) == 1)) {
                            Log.i("EcoApp", "Photo was taken yesterday");
                            currentUser.put("streak", object.get("streak"));
                        } else {
                            Log.i("EcoApp", "Photo was taken long time ago");
                            currentUser.put("streak", 0);
                        }
                    }
                    switch (selectedFragment) {
                        case 0:
                            EcoWallFragment.StampsCheck();
                            break;
                        case 1:
                            StatusFragment.StampsCheck();
                            break;
                        case 2:
                            LeaderboardFragment.StampsCheck();
                            break;
                        case 3:
                            OverwatchFragment.StampsCheck();
                            break;
                        case 4:
                            StampsFragment.StampsCheck();
                            break;
                    }

                    currentUser.put("maxStreak", object.get("maxStreak"));

                    if (object.get("lastPhotoDate") != null) {
                        currentUser.put("lastPhotoDate", object.get("lastPhotoDate"));
                    }
                    if (object.get("friends") != null) {
                        currentUser.put("friends", object.get("friends"));
                    }
                    if (object.get("profilePic") != null) {
                        currentUser.put("profilePic", object.get("profilePic"));
                    }
                    if (object.get("emailVerified") != null) {
                        currentUser.put("emailVerified", object.get("emailVerified"));
                    }

                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e3) {
                            if (e3 == null) {
                                Date date = (Date) ParseUser.getCurrentUser().get("lastPhotoDate");
                                Date currentDate = new Date();
                                if (date == null) {

                                } else {
                                    long difference = currentDate.getTime() - date.getTime();
                                    if (difference > 86400000) {
                                        ParseUser.getCurrentUser().put("streak", 0);
                                        try {
                                            ParseUser.getCurrentUser().save();
                                        } catch (ParseException e2) {
                                            e2.printStackTrace();
                                        }
                                    }
                                }
                            } else {
                                ParseUser.logOut();
                                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(mainActivityIntent);
                            }
                        }
                    });

                } else {
                    ParseUser.logOut();
                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_CAPTURE:
                if (resultCode == RESULT_OK) {
                    picUri = data.getData();
                    performCrop();
                } else {
                    Toast.makeText(getApplicationContext(), "Whoops - something went wrong while capturing images!", Toast.LENGTH_SHORT).show();
                }
                break;
            case PIC_CROP:
                image = (Bitmap) data.getExtras().get("data");
                Bitmap parseImage = image;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                parseImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                final byte[] byteArray = stream.toByteArray();
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = df.format(c.getTime());

                ParseFile file = new ParseFile("image_" + ParseUser.getCurrentUser().getObjectId() + "_" + formattedDate.toString() + ".png", byteArray);

                ParseObject object = new ParseObject("images");
                object.put("username", ParseUser.getCurrentUser().getObjectId());
                object.put("image", file);
                object.put("likes", 0);
                object.put("dislikes", 0);

                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                acl.setPublicWriteAccess(true);

                object.setACL(acl);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Upload Succesful", Toast.LENGTH_LONG).show();
                            Date date = (Date) ParseUser.getCurrentUser().get("lastPhotoDate");
                            Date currentDate = new Date();
                            if (date == null) {
                                ParseUser.getCurrentUser().put("lastPhotoDate", currentDate);
                                ParseUser.getCurrentUser().put("streak", 1);
                                ParseUser.getCurrentUser().put("ecoPoints", 1);
                                ParseUser.getCurrentUser().put("totalPoints", 1);
                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        switch (selectedFragment) {
                                            case 0:
                                                EcoWallFragment.StampsCheck();
                                                break;
                                            case 1:
                                                StatusFragment.StampsCheck();
                                                break;
                                            case 2:
                                                LeaderboardFragment.StampsCheck();
                                                break;
                                            case 3:
                                                OverwatchFragment.StampsCheck();
                                                break;
                                            case 4:
                                                StampsFragment.StampsCheck();
                                                break;
                                        }
                                    }
                                });
                            } else {
                                Calendar cal1 = Calendar.getInstance();
                                Calendar cal2 = Calendar.getInstance();
                                cal1.setTime(date);
                                cal2.setTime(currentDate);
                                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                                    Log.i("EcoApp", "This day was photo taken");
                                } else if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && (cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR) == 1)) {
                                    Log.i("EcoApp", "Photo was taken yesterday");
                                    ParseUser.getCurrentUser().put("ecoPoints", (int) ParseUser.getCurrentUser().get("ecoPoints") + 1);
                                    ParseUser.getCurrentUser().put("streak", (int) ParseUser.getCurrentUser().get("streak") + 1);
                                    if ((int) ParseUser.getCurrentUser().get("streak") > (int) ParseUser.getCurrentUser().get("maxStreak"))
                                        ParseUser.getCurrentUser().put("maxStreak", (int) ParseUser.getCurrentUser().get("streak"));
                                } else {
                                    Log.i("EcoApp", "Photo was taken long time ago");
                                    ParseUser.getCurrentUser().put("ecoPoints", (int) ParseUser.getCurrentUser().get("ecoPoints") + 1);
                                    if ((int) ParseUser.getCurrentUser().get("streak") > (int) ParseUser.getCurrentUser().get("maxStreak"))
                                        ParseUser.getCurrentUser().put("maxStreak", (int) ParseUser.getCurrentUser().get("streak"));
                                    ParseUser.getCurrentUser().put("streak", 1);
                                }
                                ParseUser.getCurrentUser().put("totalPoints", (int) ParseUser.getCurrentUser().get("totalPoints") + 1);
                                ParseUser.getCurrentUser().put("lastPhotoDate", currentDate);
                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        switch (selectedFragment) {
                                            case 0:
                                                EcoWallFragment.StampsCheck();
                                                break;
                                            case 1:
                                                StatusFragment.StampsCheck();
                                                break;
                                            case 2:
                                                LeaderboardFragment.StampsCheck();
                                                break;
                                            case 3:
                                                OverwatchFragment.StampsCheck();
                                                break;
                                            case 4:
                                                StampsFragment.StampsCheck();
                                                break;
                                        }
                                    }
                                });
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
                break;
            default:
                Toast.makeText(getApplicationContext(), "Whoops - something went wrong!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void performCrop() {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult


            if (picUri == null) {
                Toast.makeText(getApplicationContext(), "Whoops - picUri == null", Toast.LENGTH_SHORT).show();

            } else {
                startActivityForResult(cropIntent, PIC_CROP);
            }

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            Toast.makeText(getApplicationContext(), "Whoops - your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_wall);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateData();

        EcoWallFragment fragment = new EcoWallFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ecoWallFrame, fragment).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException anfe) {
                    Toast.makeText(getApplicationContext(), "Whoops - your device doesn't support capturing images!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView profilePhotoView = (ImageView) findViewById(R.id.profilePhoto);
        ParseFile profilePhoto = (ParseFile) ParseUser.getCurrentUser().get("profilePic");
        if (profilePhoto == null) {
            Log.i("ProfilePic", "null");
            profilePhotoView.setImageResource(R.drawable.placeholderprofilepic);
        } else {
            Log.i("ProfilePic", "download");
            profilePhotoView.setImageBitmap(getBitmapFromURL(profilePhoto.getUrl()));
        }

        TextView nameTextView = (TextView) findViewById(R.id.headerName);
        TextView headerUsername = (TextView) findViewById(R.id.headerUsername);

        String name = ParseUser.getCurrentUser().get("name").toString() + " " + ParseUser.getCurrentUser().get("surname").toString();
        nameTextView.setText(name);

        headerUsername.setText(ParseUser.getCurrentUser().getUsername().toString());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.eco_wall, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if (!EcoWall.changingFragments) {
            int id = item.getItemId();
            EcoWall.changingFragments = true;

            if (id == R.id.nav_ecowall) {
                Log.i("EcoApp", "EcoWall fragment selected.");
                EcoWallFragment fragment = new EcoWallFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ecoWallFrame, fragment).commit();
                setTitle("EcoWall");
                selectedFragment = 0;
            } else if (id == R.id.nav_status) {
                Log.i("EcoApp", "Status fragment selected.");
                StatusFragment fragment = new StatusFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ecoWallFrame, fragment).commit();
                selectedFragment = 1;

                setTitle("Status");
            } else if (id == R.id.nav_leaderboard) {
                Log.i("EcoApp", "Leaderboard fragment selected.");
                LeaderboardFragment fragment = new LeaderboardFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ecoWallFrame, fragment).commit();
                setTitle("Leaderboard");
                selectedFragment = 2;
            } else if (id == R.id.nav_overwatch) {
                Log.i("EcoApp", "Overwatch fragment selected.");
                OverwatchFragment fragment = new OverwatchFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ecoWallFrame, fragment).commit();
                setTitle("Overwatch");
                selectedFragment = 3;
            } else if (id == R.id.nav_stamps) {
                Log.i("EcoApp", "Stamps fragment selected.");
                StampsFragment fragment = new StampsFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ecoWallFrame, fragment).commit();
                setTitle("Stamps");
                selectedFragment = 4;
            } else if (id == R.id.nav_getting_started) {
                Log.i("EcoApp", "Getting started fragment selected.");
                GettingStartedFragment fragment = new GettingStartedFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ecoWallFrame, fragment).commit();
                setTitle("Getting started");
                selectedFragment = 5;

            } else if (id == R.id.nav_settings) {
                Log.i("EcoApp", "Settings fragment selected.");
                SettingsFragment fragment = new SettingsFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ecoWallFrame, fragment).commit();
                setTitle("Settings");
                selectedFragment = 5;
            } else if (id == R.id.nav_logoff) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }
                });
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
