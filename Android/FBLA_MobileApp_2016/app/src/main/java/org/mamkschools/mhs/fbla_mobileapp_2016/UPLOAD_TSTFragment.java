package org.mamkschools.mhs.fbla_mobileapp_2016;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Commands;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.Constants;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.SecureAPI;
import org.mamkschools.mhs.fbla_mobileapp_2016.lib.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

public class UPLOAD_TSTFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context parentContext;
    private static SimpleLocation simpleLocation;
    private EditText editTitleText;
    private ImageView picPrev;
    private Uri picUri;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static UPLOAD_TSTFragment newInstance() {
        return new UPLOAD_TSTFragment();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        simpleLocation = new SimpleLocation(getActivity().getApplicationContext());
        view.findViewById(R.id.getPic).setOnClickListener(this);
        parentContext = getActivity().getApplicationContext();
        editTitleText = (EditText)view.findViewById(R.id.editTitleText);
        picPrev = (ImageView)view.findViewById(R.id.uploadImage);
        view.findViewById(R.id.uploadNow).setOnClickListener(this);
    }

    public UPLOAD_TSTFragment() {
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
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private Uri outputFileUri;
    private static final int PICTURE_REQUEST_CODE = 1;

    private void openImageIntent() {
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = parentContext.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICTURE_REQUEST_CODE) {
            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }


            if (isCamera) {
                picUri = outputFileUri;
            } else {
                picUri = data.getData();
            }
            //picPrev.setImageURI(picUri);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.getPic:
                openImageIntent();
                break;
            case R.id.uploadNow:
                PicUploadParams uploadPic = new PicUploadParams();
                uploadPic.pics.put("picture",picUri);
                uploadPic.paramMap.put("title", editTitleText == null ? picUri.toString()
                        : editTitleText.getText().toString());

                Constants.LATITUDE = simpleLocation.getLatitude();
                Constants.LONGITUDE = simpleLocation.getLongitude();

                util.log("lat" + Constants.LATITUDE + "lon" + Constants.LONGITUDE);

                uploadPic.paramMap.put("geolong", ""+Constants.LONGITUDE);
                uploadPic.paramMap.put("geolat", "" + Constants.LATITUDE);

                new PicUpload().execute(uploadPic);
                break;
            default:
                //Do nothing
        }

    }

    private static class PicUploadParams{
        public Map<String, String> paramMap = new HashMap<String, String>();
        public Map<String, Uri> pics = new HashMap<String, Uri>();
    }

    private class PicUpload extends AsyncTask<PicUploadParams, Void, Boolean> {
        SecureAPI picture = SecureAPI.getInstance(parentContext);

        @Override
        protected Boolean doInBackground(PicUploadParams... params) {

            try {
                JSONObject response = picture.HTTPSPOSTMULTI(Commands.Post.POSTPIC +
                                Commands.AUTHCODE_BASE + Constants.AUTHCODE,
                        params[0].paramMap, params[0].pics, parentContext);
                if(response.getString("status").equals("success")){
                    return true;
                } else if(response.getString("status").equals("error")){
                    return false;
                } else {
                    throw new Exception("Impossible status");
                }
            }catch (Exception e){
                //e.printStackTrace();
                if(Constants.DEBUG_MODE){
                    util.log(e.getMessage());
                }
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                util.log("Upload worked");
                getActivity().finish();
            } else {
                util.log("Upload failed");
            }
        }
    }
}
