package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.util;

import java.io.File;
import java.util.ArrayList;

public class UserPictures extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int PIC_UPLOAD_REQUEST = 20;
    public FloatingActionButton fab;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout commentLayout;
    private File picLoc;

    public static UserPictures newInstance(File picLoc) {
        UserPictures userPictures =  new UserPictures();
        userPictures.picLoc = picLoc;
        return userPictures;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commentLayout = (LinearLayout) view.findViewById(R.id.commentLayout);
        NestedScrollView commentScroll = (NestedScrollView) view.findViewById(R.id.commentScroll);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.cameraButton);
        fab.setOnClickListener(this);


        new GetMyPictureInfo().execute((Void) null);

    }


    public UserPictures() {
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

        return inflater.inflate(R.layout.fragment_me_better, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cameraButton:
                default:
                startActivityForResult(new Intent(getContext(), UPLOAD_TST.class), PIC_UPLOAD_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PIC_UPLOAD_REQUEST){
            new GetMyPictureInfo().execute((Void) null);
        }
    }
    private class GetMyPictureInfo extends AsyncTask<Void, Boolean, Boolean> {

        private ArrayList<MeFragmentPicture> ret = new ArrayList<>();
        SecureAPI picture = SecureAPI.getInstance(getContext());


        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                JSONObject response = picture.HTTPSGET("picture/fetch/me?authcode=" + Constants.AUTHCODE);
                util.log("picture/fetch/me?authcode=" + Constants.AUTHCODE);

                JSONArray array = response.getJSONArray("data");

                for(int i = 0; i < array.length(); i++ ){
                    int pid = array.getJSONObject(i).getInt("pid");
                    String title = array.getJSONObject(i).getString("title");
                    int views = array.getJSONObject(i).getInt("views");
                    int dislikes = array.getJSONObject(i).getInt("dislikes");
                    int likes = array.getJSONObject(i).getInt("likes");
                    ret.add(MeFragmentPicture.newInstance(pid, title, dislikes, likes, views, picLoc));

                }
            }catch (Exception e){
                if(Constants.DEBUG_MODE){
                    util.log("mypics error " + e.getMessage());
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                util.log("Finished getting my pics");
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                for(int i = 0; i < ret.size(); i++){
                    transaction.add(commentLayout.getId(), ret.get(i), "Fragment_" + i);
                }
                transaction.commit();
            }else{
                util.log("Did not work_111");
            }
        }
    }
}
