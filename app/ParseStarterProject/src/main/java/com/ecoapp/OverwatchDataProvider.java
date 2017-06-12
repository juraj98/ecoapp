package com.ecoapp;

import android.graphics.Bitmap;

/**
 * Created by Juraj on 22.1.16.
 */
public class OverwatchDataProvider {
    private String name;
    private String username;
    private Bitmap image;
    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public OverwatchDataProvider(String name, String username, Bitmap image, String objectId) {
        this.setName(name);
        this.setUsername(username);
        this.setImage(image);
        this.setObjectId(objectId);
    }
}
