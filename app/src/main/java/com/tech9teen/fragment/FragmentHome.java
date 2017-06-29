package com.tech9teen.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Bitmap;
import android.graphics.Paint;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.NetConnectivity;
import com.tech9teen.NewSendSMSActivity;
import com.tech9teen.R;
import com.tech9teen.SessionManager;
import com.tech9teen.Utility;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.helper.ImageUtils;
import com.tech9teen.helper.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class FragmentHome extends android.support.v4.app.Fragment {


    private View view;
    private boolean add = false;
    private Paint p = new Paint();

    protected Context context = getActivity();


    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Bitmap bitmap;


    private SessionManager sessionmanager;

    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private TextView txtthought, txtnodata;
    private dbhandler db;
    private SQLiteDatabase sd;
    private ArrayList<Integer> lstGoalID = new ArrayList<Integer>();


    private ArrayList<String> lstSubtask = new ArrayList<String>();

    private ArrayList<Integer> lstSubtaskID = new ArrayList<Integer>();

    private RecyclerView mRecyclerView_goal;
    //private RecyclerView mRecyclerView_goaltype;
    private float COMPLETED_IN_PERCETAGE = 0.0f;
    private String currentdate = "";
    private TextView txtocuupation;
    private ExpandableListView expandablelist;


    ArrayList<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();


    private List<String> itemdata = new ArrayList<>();
    private ArrayList<List<String>> lstitemdata = new ArrayList<List<String>>();

    private String TAG = FragmentHome.class.getSimpleName();
    private ImageView imgProfilePic;
    private String userChoosenTask;
    private SpotsDialog spotDialog;


    private TextView txtName,txtEmail;
    private LinearLayout llSendSMS;

    private LinearLayout llSingle;
    private Button btnSingle,btnContacts,btnGroup;
    private TextView txtBalance;


    public FragmentHome() {
        // Required empty public constructor
    }

	/*
     * @Override public void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState); setHasOptionsMenu(true);
	 * 
	 * 
	 * }
	 */

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container,
                false);

        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }



        db = new dbhandler(getActivity());
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        spotDialog = new SpotsDialog(getActivity());
        spotDialog.setCancelable(false);


        sessionmanager = new SessionManager(getActivity());
        userDetails = sessionmanager.getUserDetails();


        imgProfilePic = (ImageView) rootView.findViewById(R.id.imgProfile);
        txtName = (TextView)rootView.findViewById(R.id.txtName);
        txtEmail = (TextView)rootView.findViewById(R.id.txtEmail);


        txtBalance = (TextView)rootView.findViewById(R.id.btnBalance1);

        llSendSMS  =(LinearLayout)rootView.findViewById(R.id.llSendSMS);

       // cardSingle = (CardView)rootView.findViewById(R.id.btnSingle);
        btnContacts = (Button)rootView.findViewById(R.id.btnContacts);
        btnGroup = (Button)rootView.findViewById(R.id.btnGroup);

        llSingle = (LinearLayout)rootView.findViewById(R.id.llsingle);
        btnSingle = (Button)rootView.findViewById(R.id.btnSingle);



        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Toast.makeText(getActivity() , "Contacts  ",Toast.LENGTH_SHORT).show();
                sessionmanager.setMessageType("contacts");


                Intent intent = new Intent(getActivity(),
                        NewSendSMSActivity.class);

                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


            }
        });
        btnSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(getActivity() , "Single  ",Toast.LENGTH_SHORT).show();
                sessionmanager.setMessageType("single");


                Intent intent = new Intent(getActivity(),
                        NewSendSMSActivity.class);

                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


            }
        });
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(getActivity() , "Group ",Toast.LENGTH_SHORT).show();
                sessionmanager.setMessageType("group");

                Intent intent = new Intent(getActivity(),
                        NewSendSMSActivity.class);
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

            }
        });



        try {
           // LinearLayout llmain = (LinearLayout) dialog.findViewById(R.id.llmain);
            llSendSMS.setVisibility(LinearLayout.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_y);
            animation.setDuration(1000);
            llSendSMS.setAnimation(animation);
            llSendSMS.animate();
            animation.start();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }




        if(!userDetails.get(SessionManager.KEY_USERNAME).equals(""))
        {
            txtName.setText(userDetails.get(SessionManager.KEY_USERNAME));

        }

        if(!userDetails.get(SessionManager.KEY_EMAIL).equals(""))
        {
            txtEmail.setText(userDetails.get(SessionManager.KEY_EMAIL));

        }




        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        SetUserProfilePictireFromBase64EnodedString();




        /*if (NetConnectivity.isOnline(getActivity())) {

            if (userDetails.get(SessionManager.KEY_VERRIFIVATION_STATUS).equals("true")) {

                //  new GetThoughtOfTheDayFromServer().execute();
            }
        }*/





        if(NetConnectivity.isOnline(getActivity()))
        {

        FetchSMSBalanceFromserver();
        }




        // Inflate the layout for this fragment
        return rootView;
    }
    //Complete OnCreateView



    private void FetchSMSBalanceFromserver() {

        showDialog();
        String url_getbalance = AllKeys.TAG_WEBSITE_HAPPY + "/GetSMSBalance?action=balance&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL Fetch Balance  :" + url_getbalance);

        StringRequest str_getbalance = new StringRequest(Request.Method.GET, url_getbalance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.d(TAG, "Response Of GEt SMS Balance: " + response.toString());

                txtBalance.setText(" " + response.toString());

                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Error in Get SMS BAlaance : " + error.getMessage());

                txtBalance.setText("Error in Fetching Balance From Server");

                hideDialog();


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getbalance);


    }


    private void SetUserProfilePictireFromBase64EnodedString() {


        showDialog();
        try {
            userDetails = sessionmanager.getUserDetails();

            String myBase64Image = userDetails.get(SessionManager.KEY_ENODEDED_STRING);
            if (!myBase64Image.equals("")) {

                Bitmap myBitmapAgain = dbhandler.decodeBase64(myBase64Image);

                imgProfilePic.setImageBitmap(myBitmapAgain);


            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Decode Img Exception : ", e.getMessage());
            hideDialog();
        }
        hideDialog();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // pDialog.dismiss();
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Profile Picture!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = Utility.checkPermission_ReadExternalStorage(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    /*@Overridef
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/


    private void onCaptureImageResult(Intent data) {

        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);


        int bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
        Log.d("C:Before Bitmap Size : ", "" + bitmapByteCount);


        //String realPath=getRealPathFromURI(data.getData());

        Uri tempUri = getImageUri(bitmap);


        String realPath = null;
        try {
            Log.d("C: Realpath URI : ", "" + tempUri.toString());
            realPath = getRealPathFromURI(tempUri);
            Log.d("C: Realpath : ", realPath);
        } catch (Exception e) {
            e.printStackTrace();
        }


        bitmap = ImageUtils.getInstant().getCompressedBitmap(realPath);
        //imageView.setImageBitmap(bitmap);

        bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
        Log.d("C:After Bitmap Size : ", "" + bitmapByteCount);


        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        imgProfilePic.setImageBitmap(bitmap);


        if (NetConnectivity.isOnline(getActivity())) {

           // new SendUserProfilePictureToServer().execute();
           // SetUserProfilePictireFromBase64EnodedString();
        } else {

            //   checkInternet();
            //Toast.makeText(getActivity(), "Please enable internet", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {


                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                try {
                    imgProfilePic.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
                Log.d("Before Bitmap Size : ", "" + bitmapByteCount);


                Uri tempUri = getImageUri(bitmap);


                String realPath = null;
                try {
                    Log.d("CC: Realpath URI : ", "" + tempUri.toString());
                    realPath = getRealPathFromURI(tempUri);
                    Log.d("CC: Realpath : ", realPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                /*Uri uri = data.getData();

                realPath=""+getRealPathFromURI(data.getData());
                Log.d("RealPath : " , ""+realPath);
                realPath = uri.getEncodedPath();
                Log.d("RealPath URI : " , ""+realPath);
                realPath=""+getRealPathFromURI_NEW(data.getData());
                Log.d("RealPath New : " , ""+realPath);*/


                bitmap = ImageUtils.getInstant().getCompressedBitmap(realPath);
                //imageView.setImageBitmap(bitmap);

                bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
                Log.d("After Bitmap Size : ", "" + bitmapByteCount);



                // getStringImage(bm);


            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        try {
            if (NetConnectivity.isOnline(getActivity())) {

             //   new SendUserProfilePictureToServer().execute();
//                SetUserProfilePictireFromBase64EnodedString();
            } else {
//                checkInternet();
              //  Toast.makeText(getActivity(), "Please enable internet", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private class SendUserProfilePictureToServer extends AsyncTask<Void, Void, Void> {

/*
        private Dialog dialog;
        private ProgressBar progressBar;
*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*dialog = new Dialog(getActivity());

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_progress);


            progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar2);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int width = display.getWidth();  // deprecated
            int height = display.getHeight();
            height = height / 3;// - 100;
            width = width - 20;

            dialog.getWindow().setLayout(width, height);
            dialog.show();
            dialog.setCancelable(false);*/
            showDialog();

        }


        @Override
        protected Void doInBackground(Void... voids) {
            final Bitmap finalBm = bitmap;

            /*getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run()
                {*/

            //String enc = encodeToBase64(finalBm, Bitmap.CompressFormat.JPEG, 50);


            String enc = dbhandler.getStringImage(finalBm);
            sessionmanager.setEncodedImage(enc);

            if (NetConnectivity.isOnline(getActivity())) {
                ServiceHandler sh = new ServiceHandler();

                String URL_PROFILEPICUPDATE = AllKeys.WEBSITE + "UpdateStudentProfile";
//                        String URL_PROFILEPICUPDATE = AllKeys.TAG_WEBSITE_SERVICET + "InsertGyanCapsule";
                Log.d("URL ProfileUpdate : ", URL_PROFILEPICUPDATE);
                //String res = sh.makeServiceCall(URL_PROFILEPICUPDATE, ServiceHandler.GET);
                //Log.d("Response : ", res);


                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("type", "updateprofile"));
                params.add(new BasicNameValuePair("custid", "" + userDetails.get(sessionmanager.KEY_USERID)));

                params.add(new BasicNameValuePair("imagecode", enc));
                String response = sh.makeServiceCall(URL_PROFILEPICUPDATE, ServiceHandler.POST, params);

                Log.d("Profile Url :", URL_PROFILEPICUPDATE + params.toString());
                Log.d("Response : ", response);

            } else {
                //checkInternet();

            }


              /*  }
            });*/

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           /* if (dialog.isShowing()) {
                imgProfilePic.setImageBitmap(bitmap);
                dialog.dismiss();
                dialog.cancel();

            }*/

            hideDialog();
            imgProfilePic.setImageBitmap(bitmap);

        }


    }

    /**
     * Complete User select profile picture from device
     */


    /*public String getRealPathFromURI( Uri contentUri) {//content://media/external/images/media/4288
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/
    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String dd = cursor.getString(column_index);
            return cursor.getString(column_index);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return "sa";

    }


    private String getRealPathFromURI_NEW(Uri contentURI) {
        String result = null;

        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }


    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void showDialog() {


        if (!spotDialog.isShowing()) {
            spotDialog.show();
        }
    }

    public void hideDialog() {
        if (spotDialog.isShowing()) {

            // spotDialog.cancel();
            spotDialog.dismiss();

        }

    }


}