package com.ecoapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraj on 24.1.16.
 */
public class EcoWallAdapter extends RecyclerView.Adapter<EcoWallAdapter.ViewHolder> {
    private List<ImageView> imageViewList = new ArrayList();
    private List<EcoWallDataProvider> list;

    public EcoWallAdapter(List<EcoWallDataProvider> givenList) {
        list = givenList;
    }

    public void setImagesHeight(){
        for (int i = 0; i < imageViewList.size(); i++){
            Log.i("EcoApp", "Height " + imageViewList.get(i).getLayoutParams().height);
            Log.i("EcoApp", "Width " + imageViewList.get(i).getLayoutParams().width);
            imageViewList.get(i).getLayoutParams().height = imageViewList.get(i).getLayoutParams().width;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecowall_item, null);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        EcoWallDataProvider ecoWallDataProvider = list.get(position);
        imageViewList.add(holder.imageView);
        holder.imageView.setImageBitmap(ecoWallDataProvider.getImage());
        holder.nameTextView.setText(ecoWallDataProvider.getName());
        holder.usernameTextView.setText(ecoWallDataProvider.getUsername());

    }

    @Override
    public int getItemCount() {
        return  null != list ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;
        public TextView usernameTextView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.ecoWallImageView);
            this.nameTextView = (TextView) view.findViewById(R.id.ecowallNameTextView);
            this.usernameTextView = (TextView) view.findViewById(R.id.ecowallUsernameTextView);
        }
    }

}
