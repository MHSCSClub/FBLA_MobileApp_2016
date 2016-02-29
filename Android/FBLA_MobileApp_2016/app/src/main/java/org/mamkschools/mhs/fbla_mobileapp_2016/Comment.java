package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


public class Comment extends android.app.Fragment{

    private String user;
    private String comment;
    private String style;
    private boolean showDiv;



    public Comment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static Comment newInstance(String user, String comment, String style) {
        Comment fragment = new Comment();
        fragment.user = user;
        fragment.comment = comment;
        fragment.style = style;
        fragment.showDiv = true;
        return fragment;
    }

    public void setShowDiv(boolean showDiv){
        this.showDiv = showDiv;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView text = (TextView) view.findViewById(R.id.commentText);
        text.setText(comment);
        TextView textUser = (TextView) view.findViewById(R.id.username);
        textUser.setText(user);
        TextView count = (TextView) view.findViewById(R.id.dcount);
        count.setText(style);
        View sep = view.findViewById(R.id.sep);
        sep.setVisibility(showDiv ? View.VISIBLE : View.GONE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }
}
