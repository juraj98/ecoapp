package com.ecoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraj on 18.1.16.
 */
public class LeaderboardAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public LeaderboardAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    public void removeAllObjectInList(){
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
        DataHandler dataHandler;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.leaderboard_item, parent, false);
            dataHandler = new DataHandler();
            dataHandler.number = (TextView) row.findViewById(R.id.number);
            dataHandler.username = (TextView) row.findViewById(R.id.name);
            dataHandler.points = (TextView) row.findViewById(R.id.score);
            row.setTag(dataHandler);
        }
        else {
            dataHandler = (DataHandler) row.getTag();
        }

        LeaderboardDataProvider leaderboardDataProvider;
        leaderboardDataProvider= (LeaderboardDataProvider)this.getItem(position);

        if(leaderboardDataProvider.getNumber() == -1){
            dataHandler.number.setText("");
        }else {
            dataHandler.number.setText(leaderboardDataProvider.getNumber() + ".");
        }
        dataHandler.username.setText(leaderboardDataProvider.getUsername());
        dataHandler.points.setText(leaderboardDataProvider.getPoints() + "");

        if(leaderboardDataProvider.getUsername() == ParseUser.getCurrentUser().getUsername()){
            dataHandler.username.setTextColor(0xFFFFC107);
            dataHandler.points.setTextColor(0xFFFFC107);
            dataHandler.number.setTextColor(0xFFFFC107);
        }
        else {
            dataHandler.username.setTextColor(0xFF727272);
            dataHandler.points.setTextColor(0xFF727272);
            dataHandler.number.setTextColor(0xFF727272);
        }

        return row;
    }

    static class DataHandler {
        TextView number;
        TextView username;
        TextView points;
    }
}
