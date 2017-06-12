package com.ecoapp;

import android.graphics.Bitmap;

/**
 * Created by Juraj on 24.1.16.
 */
public class EcoWallDataProvider {

    Bitmap image;
    String name;
    String username;
    String userObjectId;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    public EcoWallDataProvider(Bitmap image, String name, String username, String userObjectId) {
        this.image = image;
        this.name = name;
        this.username = username;
        this.userObjectId = userObjectId;
    }
}
