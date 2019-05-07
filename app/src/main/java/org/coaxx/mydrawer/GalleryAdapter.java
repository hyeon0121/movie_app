package org.coaxx.mydrawer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    public static Context context;

    ArrayList<GalleryItem> items = new ArrayList<>();

    OnItemClickListener listener;

    public static interface OnItemClickListener {
        public void OnItemClick(ViewHolder holder, View view, int position);
    }
    public GalleryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.gallery_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GalleryItem item = items.get(i);
        viewHolder.setItem(item);

        viewHolder.setOnItemClickListener(listener);
    }

    public void addItem(GalleryItem item) {
        items.add(item);
    }

    public void addItems(ArrayList<GalleryItem> items) {
        this.items = items;
    }

    public GalleryItem getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView imageView2;

        OnItemClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            imageView2 = itemView.findViewById(R.id.imageView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.OnItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(GalleryItem item) {

            if (item.isVal()) {
                Glide.with(context)
                        .load(item.getUrl())
                        .into(imageView);
                imageView2.setVisibility(View.INVISIBLE);
            } else {
                try {
                    Uri uri = Uri.parse(item.getUrl());
                    String path = uri.getPath();

                    Glide.with(context)
                            .load("https://img.youtube.com/vi"+ path +"/sddefault.jpg")
                            .into(imageView);

                    imageView2.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
}
