package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.util;

import java.io.File;

public class MeFragmentPicture extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView title;
    private TextView ucount;
    private TextView dcount;

    private ImageView image;

    private String titles;
    private int views;
    private int dislikes;
    private int likes;
    private int pid;
    private boolean showDiv;

    private File location;
    private Bitmap imageData;


    public static MeFragmentPicture newInstance(int pid, String title, int dislikes, int likes, int views, File location) {
        MeFragmentPicture frag =  new MeFragmentPicture();
        frag.titles = title;
        frag.pid = pid;
        frag.dislikes = dislikes;
        frag.likes = likes;
        frag.views = views;
        frag.location = location;
        frag.showDiv = true;
        return frag;
    }

    public void setShow(boolean showDiv){
        this.showDiv = showDiv;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = (TextView) view.findViewById(R.id.TitleText);
        ucount = (TextView) view.findViewById(R.id.ucount);
        dcount = (TextView) view.findViewById(R.id.dcount);

        title.setText(titles);
        ucount.setText(Integer.toString(likes));
        dcount.setText(Integer.toString(dislikes));

        View div = view.findViewById(R.id.div);
        div.setVisibility(showDiv ? View.VISIBLE : View.GONE);
        view.setOnClickListener(this);
    }

    public MeFragmentPicture() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me_picture, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        Intent myintent = new Intent(getContext(), CommentPage.class);
        myintent.putExtra("pid", pid);
        myintent.putExtra("title", titles);
        startActivity(myintent);
    }

}
