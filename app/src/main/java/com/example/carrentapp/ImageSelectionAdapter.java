package com.example.carrentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageSelectionAdapter extends RecyclerView.Adapter<ImageSelectionAdapter.ViewHolder> {
    ArrayList<String> bitmapArrayList;
    Context context;

    public ImageSelectionAdapter(Context context, ArrayList<String> bitmapArrayList) {
        this.bitmapArrayList = bitmapArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_simple_itemview;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSelectionAdapter.ViewHolder holder, int position) {
        holder.bindData(context, bitmapArrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return bitmapArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView1);
        }

        public void bindData(Context context, String uri) {

            Glide.with(context)
                    .load(uri)
                    .into(imageView);


        }
    }
}
