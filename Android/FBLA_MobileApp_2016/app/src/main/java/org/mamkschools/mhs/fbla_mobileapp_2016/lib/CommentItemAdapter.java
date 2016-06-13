package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        Util.log("" + rating);
        int fill = Math.max(0, Math.min(rating, 5));


        char stars[] = new char[5];
        Arrays.fill(stars, '\u2605');

        holder.style_fill.setText(stars, 0, fill);
        holder.style_unfill.setText(stars, 0, 5 - fill);
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
            commentText.setMaxLines(small ? 100 : 2);
        }
    }
}