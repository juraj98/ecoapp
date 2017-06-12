package com.ecoapp;

/**
 * Created by Juraj on 18.1.16.
 */
public class LeaderboardDataProvider {

    private int number;
    private String username;
    private int points;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LeaderboardDataProvider(int number, String username, int score){

        this.setNumber(number);
        this.setUsername(username);
        this.setPoints(score);
    }
}
