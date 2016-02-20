package org.mamkschools.mhs.fbla_mobileapp_2016;

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
    private TextView info;

    private ImageView image;

    private String titles;
    private int views;
    private int dislikes;
    private int likes;
    private int pid;

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
        return frag;

    }
    public Bitmap getPictureBitmap(File file){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;
        if (imageData != null) {
            options.inBitmap = imageData;
        }
        try {
            Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            util.log("" + b.getByteCount()); //do not remove line throws exception if decoding problem
            return b;
        } catch(Exception e) {
            //Problem decoding into existing bitmap, allocate new memory
            options.inBitmap = null;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        }

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = (TextView) view.findViewById(R.id.TitleText);
        info = (TextView) view.findViewById(R.id.infomation);
        image = (ImageView) view.findViewById(R.id.imageProfile);

        title.setText(titles);
        info.setText("likes:" + likes + "\n" + "Dislikes:" + dislikes);
        new GetPicture().execute(pid);

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

    }

    private class GetPicture extends AsyncTask<Integer, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(getContext());

        @Override
        protected Boolean doInBackground(Integer... params) {
            try{
                int pid = params[0];
                util.log("picture/" + pid + "?authcode=" + Constants.AUTHCODE);
                picture.HTTPSFETCHPIC("picture/" + pid + "?authcode=" + Constants.AUTHCODE, new File(location, "picture.jpg"));
                imageData = getPictureBitmap(new File(location, "picture.jpg"));
            }catch(Exception e){
                util.log(e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean v) {
            if(v){
                image.setImageBitmap(imageData);
            }else{
                util.log("Life will go on");
            }
        }
    }
}
