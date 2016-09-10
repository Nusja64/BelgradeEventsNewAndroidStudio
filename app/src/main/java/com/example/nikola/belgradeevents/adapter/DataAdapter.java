package com.example.nikola.belgradeevents.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikola.belgradeevents.BelgradeMapsActivity;
import com.example.nikola.belgradeevents.DataList;
import com.example.nikola.belgradeevents.R;
import com.example.nikola.belgradeevents.model.Image;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<Image> images;
    private Context context;
    private DataList datainterface;

    public DataAdapter(BelgradeMapsActivity mapsActivity,Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
        this.datainterface = (DataList) mapsActivity;

    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {

       // viewHolder.tv_android.setText(images.get(i).getAndroid_version_name());
        Picasso.with(context).load(images.get(i).getImageUrl()).resize(120, 60).into(viewHolder.img_android);
        viewHolder.img_android.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datainterface.sendDataToActivity(images.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
      //  TextView tv_android;
        ImageView img_android;
        public ViewHolder(View view) {
            super(view);

          //  tv_android = (TextView)view.findViewById(R.id.tv_android);
            img_android = (ImageView)view.findViewById(R.id.img_android);
        }
    }
}