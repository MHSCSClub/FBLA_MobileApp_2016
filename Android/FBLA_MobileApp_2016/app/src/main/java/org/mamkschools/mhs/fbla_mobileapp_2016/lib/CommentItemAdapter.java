package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.mamkschools.mhs.fbla_mobileapp_2016.DetailMeActivity;
import org.mamkschools.mhs.fbla_mobileapp_2016.R;

import java.util.ArrayList;
import java.util.Arrays;

public class CommentItemAdapter extends RecyclerView.Adapter<CommentItemAdapter.PictureViewHolder> {

    ArrayList<CommentItem> commentList;

    public CommentItemAdapter(ArrayList<CommentItem> commentList, Context context) {
        this.commentList = commentList;
    }

    @Override
    public CommentItemAdapter.PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.target_single_comment
                , parent, false);
        return new PictureViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentItemAdapter.PictureViewHolder holder, int position) {
        holder.commentText.setText(commentList.get(position).getComment().trim());
        holder.userName.setText(commentList.get(position).getUser());
        int rating = Integer.parseInt(commentList.get(position).getStyleRating());
        int fill = Math.round(rating / 2f); //convert from 10 pt system to 5 pt system, take this out!! when we change rating

        char stars[] = new char[5];
        Arrays.fill(stars, '★');

        holder.style_fill.setText(stars, 0, fill);
        holder.style_unfill.setText(stars, 0, 5-fill);
    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class PictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView commentText;
        private TextView userName;
        private TextView style_fill;
        private TextView style_unfill;
        private boolean small;


        public PictureViewHolder(View itemView) {
            super(itemView);
            commentText = (TextView) itemView.findViewById(R.id.commentText);
            userName = (TextView) itemView.findViewById(R.id.username);
            style_fill = (TextView) itemView.findViewById(R.id.style_fill);
            style_unfill = (TextView) itemView.findViewById(R.id.style_unfill);
            itemView.setOnClickListener(this);
            small = true;
        }

        @Override
        public void onClick(View v) {
            small = !small;
            commentText.setMaxLines(small ? 3 : 100);
        }
    }
}