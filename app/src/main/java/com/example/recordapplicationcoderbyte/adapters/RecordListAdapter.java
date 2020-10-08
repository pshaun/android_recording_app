package com.example.recordapplicationcoderbyte.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.recordapplicationcoderbyte.R;
import com.example.recordapplicationcoderbyte.room.RecordEntity;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordListViewHolder> {
    private List<RecordEntity> recordEntityList;

    public RecordListAdapter(List<RecordEntity> recordEntityList) {
        this.recordEntityList = recordEntityList;
    }

    @NonNull
    @Override
    public RecordListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_saved_list, parent, false);
        RecordListViewHolder viewHolder = new RecordListViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordListViewHolder holder, int position) {
        holder.textViewTitle.setText(recordEntityList.get(position).getTitle());
        holder.textViewDuration.setText(recordEntityList.get(position).getDuration());
        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(Uri.fromFile(new File(recordEntityList.get(position).getImgUrl())))
                .into(holder.imgThum);
    }

    @Override
    public int getItemCount() {
        return recordEntityList.size();
    }

    public class RecordListViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewDuration;
        public ImageView imgThum;
        public RecordListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewTitle = itemView.findViewById(R.id.title);
            this.textViewDuration = itemView.findViewById(R.id.duration);
            this.imgThum = itemView.findViewById(R.id.imgThum);
        }
    }
}
