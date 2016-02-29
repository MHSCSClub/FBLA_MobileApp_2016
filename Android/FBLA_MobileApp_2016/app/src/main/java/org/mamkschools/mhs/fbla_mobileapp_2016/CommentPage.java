package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.FragmentTransaction.*;
import android.view.ViewGroup;


/**
 * Created by jackphillips on 2/26/16.
 */
public class CommentPage extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Comment comment = Comment.newInstance("Jack", "Cool");

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.view_pager, comment, "Fragment").commit();

    }

    @Override
    public void onClick(View v) {

    }
}
