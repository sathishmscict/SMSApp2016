package com.tech9teen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.CursorLoader;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.R;
import com.tech9teen.adapter.UploadDocumentRecyclerViewAdapter;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.helper.ImageUtils;
import com.tech9teen.helper.ServiceHandler;
import com.tech9teen.pojo.UploadDocuments;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class UploadDocumentsActivity extends AppCompatActivity {


    boolean IsUploadDone = true;
    private static String TAG2 = UploadDocumentsActivity.class.getSimpleName();
    private CoordinatorLayout coordinateLayout;
    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private Spinner spnRoute;
    private EditText edtSenderid;
    private TextInputLayout input_edtsenderid;
    private TextView txtPhoto, txtDocument;
    private TextView txtIdProof, txtIdProof2;
    private CardView crdPhoto, crdDocuemnt, crdIdProof, crdIdProof2;
    private ImageView imgPhoto, imgDocument, imgIdProof, imgIdProof2;
    private Button btnUpload;
    private SpotsDialog spotDialog;
    private String userChoosenTask = "";


    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Bitmap bitmap;

    String base64_Photo = "", base64_document = "", base64_idproof1 = "", base64_idproof2 = "";
    private String DOCUMENTTYPE;
    private String TAG = UploadDocumentsActivity.class.getSimpleName();
    private TextView txtError;


    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private ImageView imgPhotoStatus, imgDocumentStatus, imgIdProofStaus, imgIdProof2Status;
    private LinearLayout llAddData;
    private LinearLayout llShowData;
    private Menu menu;
    private MenuItem menu_sync,menu_show;
    private FloatingActionButton fab;
    private RecyclerView recyclerView_documents;
    private UploadDocumentRecyclerViewAdapter adapter;
    private ArrayList<UploadDocuments> list_document= new ArrayList<UploadDocuments>();
    private TextView txtnodata;
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_documents);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                fab.setVisibility(View.GONE);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                llAddData.setVisibility(View.VISIBLE);
                llShowData.setVisibility(View.GONE);
            }
        });

        fab.setVisibility(View.GONE);




        spotDialog = new SpotsDialog(context);
        // spotDialog = new SpotsDialog(context,R.style.Custom);
        spotDialog.setCancelable(false);


        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();

        db = new dbhandler(context);
        sd = db.getWritableDatabase();
        sd = db.getReadableDatabase();


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        llAddData = (LinearLayout)findViewById(R.id.lladddata);
        llShowData = (LinearLayout)findViewById(R.id.llshowdata);

        txtnodata  =(TextView)findViewById(R.id.txtnodata);
        txtnodata.setVisibility(View.GONE);



        edtSenderid = (EditText) findViewById(R.id.edtsenderid);
        input_edtsenderid = (TextInputLayout) findViewById(R.id.input_layout_edtsenderid);

        llShowData.setVisibility(View.GONE);
        llAddData.setVisibility(View.VISIBLE);



        recyclerView_documents = (RecyclerView)findViewById(R.id.recycler_view_documents);



        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView_documents.setLayoutManager(mLayoutManager);
        recyclerView_documents.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
        recyclerView_documents.setItemAnimator(new DefaultItemAnimator());

        adapter = new UploadDocumentRecyclerViewAdapter(context, list_document);
        recyclerView_documents.setAdapter(adapter);


        /**
         * Fill Data Of spinner Route Type
         */

        spnRoute = (Spinner) findViewById(R.id.spnGateway);
        ArrayList<String> list_typesOfSenderIds = new ArrayList<String>();


        list_typesOfSenderIds.add("Tap to Select Route");
        list_typesOfSenderIds.add("Promotional");
        list_typesOfSenderIds.add("Transactional");

        ArrayAdapter<String> Templateadapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_dropdown_item,
                list_typesOfSenderIds);
        spnRoute.setAdapter(Templateadapter);


        txtError = (TextView) findViewById(R.id.txtError);
        txtError.setVisibility(View.GONE);

        txtPhoto = (TextView) findViewById(R.id.txtUploadPhoto);
        txtDocument = (TextView) findViewById(R.id.txtFilledDocument);
        txtIdProof = (TextView) findViewById(R.id.txtIdProof);
        txtIdProof2 = (TextView) findViewById(R.id.txtIdProof2);


        crdPhoto = (CardView) findViewById(R.id.crdPhoto);
        crdDocuemnt = (CardView) findViewById(R.id.crdDocument);
        crdIdProof = (CardView) findViewById(R.id.crdIdProof);
        crdIdProof2 = (CardView) findViewById(R.id.crdIdProof2);

        crdIdProof2.setVisibility(View.GONE);


        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        imgDocument = (ImageView) findViewById(R.id.imgDocument);
        imgIdProof = (ImageView) findViewById(R.id.imgIdProof);
        imgIdProof2 = (ImageView) findViewById(R.id.imgIdProof2);

        imgPhotoStatus = (ImageView) findViewById(R.id.imgPhotoCheck);
        imgDocumentStatus = (ImageView) findViewById(R.id.imgDocumentCheck);
        imgIdProofStaus = (ImageView) findViewById(R.id.imgIdProofCheck);
        imgIdProof2Status = (ImageView) findViewById(R.id.imgIdProof2Check);

        imgPhotoStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (NetConnectivity.isOnline(context)) {


                    if(imgPhotoStatus.getTag().equals("0"))
                    {

                    SendBase64PhotoToServer();
                    }


                } else {
                    checkInternet();
                }
            }
        });



        imgDocumentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (NetConnectivity.isOnline(context)) {


                    if(imgDocumentStatus.getTag().equals("0"))
                    {

                        SendBase64DocumentToServer();
                    }


                } else {
                    checkInternet();
                }
            }
        });


        imgIdProofStaus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (NetConnectivity.isOnline(context)) {


                    if(imgIdProofStaus.getTag().equals("0"))
                    {

                        SendBase64IDProofToServer();
                    }


                } else {
                    checkInternet();
                }
            }
        });


        imgIdProof2Status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (NetConnectivity.isOnline(context)) {


                    if(imgIdProof2Status.getTag().equals("0"))
                    {

                        SendBase64IDProof2ToServer();
                    }


                } else {
                    checkInternet();
                }
            }
        });



        btnUpload = (Button) findViewById(R.id.btnUpload);


        txtPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DOCUMENTTYPE = "photo";
                selectImage();

            }
        });

        txtDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DOCUMENTTYPE = "document";

                selectImage();
            }
        });

        txtIdProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DOCUMENTTYPE = "proof1";
                selectImage();
            }
        });

        txtIdProof2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DOCUMENTTYPE = "proof2";
                selectImage();
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean IsError = false;
                if (spnRoute.getSelectedItemPosition() == 0) {

                    IsError = true;
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText("Please Select Route");
                } else {
                    txtError.setVisibility(View.GONE);
                }

                if (edtSenderid.getText().toString().equals("")) {

                    IsError = true;
                    input_edtsenderid.setError("Please enter senderid");
                    input_edtsenderid.setErrorEnabled(true);
                } else {
                    if (edtSenderid.getText().toString().length() != 6) {

                        IsError = true;
                        input_edtsenderid.setError("Must Be 6 characters");
                        input_edtsenderid.setErrorEnabled(true);


                    } else {
                        input_edtsenderid.setErrorEnabled(false);
                    }

                }

                if (!base64_Photo.equals("") && !base64_document.equals("") && !base64_idproof1.equals("")) {


                    if (NetConnectivity.isOnline(context)) {

                        /*SendBase64PhotoToServer();
                        Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
                        SendBase64DocumentToServer();
                        SendBase64IDProofToServer();
                        Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
                        if (!base64_idproof2.equals("")) {
                            SendBase64IDProof2ToServer();

                        }


                        if (IsUploadDone == true) {
                            Toast.makeText(getApplicationContext(), "Uploading Done", Toast.LENGTH_SHORT).show();

                        }*/



                        /*final String url_uploaddocs = AllKeys.TAG_WEBSITE_HAPPY+"/UpdateStudentDocument";
                        Log.d(TAG , " URL Upload images : "+url_uploaddocs);


                        StringRequest str_upload = new StringRequest(Request.Method.POST, url_uploaddocs, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d(TAG , " Response  : "+response);


                                if(!response.equals("0"))
                                {
                                    Toast.makeText(getApplicationContext() , "Documents has been submitted",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                    Toast.makeText(getApplicationContext() , "Sorry,Try again...",Toast.LENGTH_SHORT).show();
                                }
                                hideDialog();





                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG  , "Error in Uploading Images : "+error.getMessage());

                                hideDialog();

                            }
                        }){

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String,String> params= null;


                                try {
                                    *//**
                         * Get Maxid of document
                         *//*
                                    String qry_maxid = "select MAX("+ dbhandler.UPLOADDOC_ID +") from "+ dbhandler.TABLE_UPLOADDOC +"";
                                    Cursor cur = sd.rawQuery(qry_maxid , null);
                                    Log.d(TAG , " Total  "+cur.getCount()+" Records Found");
                                    cur.moveToNext();
                                    int maxid = cur.getInt(0);
                                    Log.d(TAG , "MAXID BEFORE : "+maxid);
                                    ++maxid;
                                    Log.d(TAG , "MAXID AFTER : "+maxid);


                                    params = new HashMap<String, String>();
                                    params.put("type","updatedocument");
                                    params.put("documentid",String.valueOf(maxid));
                                    params.put("routetype",""+spnRoute.getSelectedItem());
                                    params.put("userid",""+userDetails.get(SessionManager.KEY_USERID));
                                    params.put("senderid",""+edtSenderid.getText().toString());
                                    params.put("photocode",""+base64_Photo);
                                    params.put("documentcode",""+base64_document);
                                    params.put("idproofcode",""+base64_idproof1);
                                    params.put("extradocument",""+base64_idproof2);


                                    Log.d(TAG  , "Documents Data : "+url_uploaddocs+params.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                return params;
                            }
                        };

                        MyApplication.getInstance().addToRequestQueue(str_upload);

*/


                        if(imgPhotoStatus.getTag().equals("1") && imgDocumentStatus.getTag().equals("1")&&imgIdProofStaus.getTag().equals("1"))
                        {
                            if(!base64_idproof2.equals(""))
                            {
                                if(imgIdProof2Status.getTag().equals("1"))
                                {

                                    bitmap=null;
                                    imgPhoto.setImageBitmap(bitmap);
                                    imgPhotoStatus.setImageBitmap(bitmap);


                                    imgDocument.setImageBitmap(bitmap);
                                    imgDocumentStatus.setImageBitmap(bitmap);


                                    imgIdProof.setImageBitmap(bitmap);
                                    imgIdProofStaus.setImageBitmap(bitmap);

                                    imgIdProof2.setImageBitmap(bitmap);
                                    imgIdProof2Status.setImageBitmap(bitmap);




                                    AlertMessage();

                                }
                                else
                                {
                                    Snackbar.make(coordinateLayout,"Please Resend IdProof",Snackbar.LENGTH_SHORT).show();
                                }

                            }
                            else
                            {



                                bitmap=null;
                                imgPhoto.setImageBitmap(bitmap);
                                imgPhotoStatus.setImageBitmap(bitmap);


                                imgDocument.setImageBitmap(bitmap);
                                imgDocumentStatus.setImageBitmap(bitmap);


                                imgIdProof.setImageBitmap(bitmap);
                                imgIdProofStaus.setImageBitmap(bitmap);

                                /*imgIdProof2.setImageBitmap(bitmap);
                                imgIdProof2Status.setImageBitmap(bitmap);*/


                                AlertMessage();

                            }

                        }
                        else
                        {
                            Snackbar.make(coordinateLayout,"Please resend documents",Snackbar.LENGTH_SHORT).show();
                        }


                    } else {

                        checkInternet();
                    }
                    //showDialog();

                } else {
                    Snackbar.make(coordinateLayout, "Please upload all documents", Snackbar.LENGTH_SHORT).show();
                }


            }
        });


        if (NetConnectivity.isOnline(context)) {

            GetAllSenderIdRequestDetails();
        } else {
            checkInternet();
            //FillDataOnRecyclerView();
        }


    }

    private void AlertMessage() {


        AlertDialog.Builder builder= new AlertDialog.Builder(context);

        builder.setTitle("Upload Info");
        builder.setMessage("Files has been successfully uploaded.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                dialog.dismiss();

            }
        });

       // builder.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        builder.show();



    }

    private void SendBase64IDProof2ToServer() {


        showDialog();

        final Request.Priority priority = Request.Priority.LOW;

        final String url_uploaddocs = AllKeys.TAG_WEBSITE_HAPPY + "/UpdateExtraId";
        Log.d(TAG, " URL Upload Proof2 : " + url_uploaddocs);


        imgIdProof2.setImageBitmap(bitmap);


        StringRequest str_upload = new StringRequest(Request.Method.POST, url_uploaddocs, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Proof2 Upload Response  : " + response);


                if (!response.equals("0")) {

                    //Toast.makeText(getApplicationContext(), "Proof2 has been submitted", Toast.LENGTH_SHORT).show();

                    //Snackbar.make(coordinateLayout, "Proof2 has been submitted", Snackbar.LENGTH_SHORT).show();

                    imgIdProof2Status.setBackgroundResource(R.drawable.icon_success);

                    imgIdProof2Status.setTag("1");



                } else {

                    Snackbar.make(coordinateLayout, "Sorry, Try again...", Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "Sorry,Try again...", Toast.LENGTH_SHORT).show();
                    imgIdProof2Status.setBackgroundResource(R.drawable.icon_refresh);
                    imgIdProof2Status.setTag("0");
                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error in Uploading Proof2 : " + error.getMessage());

                hideDialog();
                IsUploadDone = false;
                imgIdProof2Status.setBackgroundResource(R.drawable.icon_refresh);
                imgIdProof2Status.setTag("0");

            }
        }) {

           /* @Override
            public Priority getPriority() {
                return priority;
            }
*/

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = null;


                try {
                    /**
                     * Get Maxid of document
                     */
                    String qry_maxid = "select MAX(" + dbhandler.UPLOADDOC_ID + ") from " + dbhandler.TABLE_UPLOADDOC + "";
                    Cursor cur = sd.rawQuery(qry_maxid, null);
                    Log.d(TAG, " Total  " + cur.getCount() + " Records Found");
                    cur.moveToNext();
                    int maxid = cur.getInt(0);
                    Log.d(TAG, "MAXID BEFORE : " + maxid);
                    ++maxid;
                    Log.d(TAG, "MAXID AFTER : " + maxid);


                    params = new HashMap<String, String>();
                    params.put("type", "updatedocument");
                    params.put("documentid", String.valueOf(maxid));
                    params.put("routetype", "" + spnRoute.getSelectedItem());
                    params.put("userid", "" + userDetails.get(SessionManager.KEY_USERID));
                    params.put("senderid", "" + edtSenderid.getText().toString());
                    // params.put("photocode",""+base64_Photo);
                    //params.put("documentcode",""+base64_document);
                    // params.put("idproofcode",""+base64_idproof1);
                    params.put("extradocument", "" + base64_idproof2);


                    Log.d(TAG, "Documents Prof2 Data : " + url_uploaddocs + params.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(str_upload);

    }

    private void SendBase64IDProofToServer() {



        showDialog();
        final Request.Priority priority = Request.Priority.NORMAL;

        final String url_uploaddocs = AllKeys.TAG_WEBSITE_HAPPY + "/UpdateIDProof";
        Log.d(TAG, " URL Upload Proof1 : " + url_uploaddocs);

        imgIdProof.setImageBitmap(bitmap);

        StringRequest str_upload = new StringRequest(Request.Method.POST, url_uploaddocs, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Proof1 Upload Response  : " + response);


                if (!response.equals("0")) {

//                    Toast.makeText(getApplicationContext(), "Proof1 has been submitted", Toast.LENGTH_SHORT).show();

  //                  Snackbar.make(coordinateLayout, "Proof1 has been submitted", Snackbar.LENGTH_SHORT).show();
                    imgIdProofStaus.setBackgroundResource(R.drawable.icon_success);
                    imgIdProofStaus.setTag("1");

                } else {

                    Snackbar.make(coordinateLayout, "Sorry, Try again...", Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "Sorry,Try again...", Toast.LENGTH_SHORT).show();
                    imgIdProofStaus.setBackgroundResource(R.drawable.icon_refresh);
                    imgIdProofStaus.setTag("0");
                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error in Uploading Proof1 : " + error.getMessage());
                IsUploadDone = false;
                hideDialog();
                imgIdProofStaus.setBackgroundResource(R.drawable.icon_refresh);

                imgIdProofStaus.setTag("0");
            }
        }) {

            /*@Override
            public Request.Priority getPriority() {
                return priority;
            }*/


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = null;


                try {
                    /**
                     * Get Maxid of document
                     */
                    String qry_maxid = "select MAX(" + dbhandler.UPLOADDOC_ID + ") from " + dbhandler.TABLE_UPLOADDOC + "";
                    Cursor cur = sd.rawQuery(qry_maxid, null);
                    Log.d(TAG, " Total  " + cur.getCount() + " Records Found");
                    cur.moveToNext();
                    int maxid = cur.getInt(0);
                    Log.d(TAG, "MAXID BEFORE : " + maxid);
                    ++maxid;
                    Log.d(TAG, "MAXID AFTER : " + maxid);


                    params = new HashMap<String, String>();
                    params.put("type", "updatedocument");
                    params.put("documentid", String.valueOf(maxid));
                    params.put("routetype", "" + spnRoute.getSelectedItem());
                    params.put("userid", "" + userDetails.get(SessionManager.KEY_USERID));
                    params.put("senderid", "" + edtSenderid.getText().toString());
                    //  params.put("photocode",""+base64_Photo);
                    //params.put("documentcode",""+base64_document);
                    params.put("idproofcode", "" + base64_idproof1);
                    //params.put("extradocument",""+base64_idproof2);


                    Log.d(TAG, "Documents Proof1 Data : " + url_uploaddocs + params.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(str_upload);

    }

    private void SendBase64DocumentToServer() {

        showDialog();
        final Request.Priority priority = Request.Priority.IMMEDIATE;


        final String url_uploaddocs = AllKeys.TAG_WEBSITE_HAPPY + "/UpdateDocument";
        Log.d(TAG, " URL Upload Document : " + url_uploaddocs);


        imgDocument.setImageBitmap(bitmap);
        StringRequest str_upload = new StringRequest(Request.Method.POST, url_uploaddocs, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, " Document Upload Response  : " + response);


                if (!response.equals("0")) {

                    //Toast.makeText(getApplicationContext(), "Document has been submitted", Toast.LENGTH_SHORT).show();

                    //Snackbar.make(coordinateLayout, "Document has been submitted", Snackbar.LENGTH_SHORT).show();

                    imgDocumentStatus.setBackgroundResource(R.drawable.icon_success);
                    imgDocumentStatus.setTag("1");


                } else {

                    Snackbar.make(coordinateLayout, "Sorry, Try again...", Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "Sorry,Try again...", Toast.LENGTH_SHORT).show();
                    imgDocumentStatus.setBackgroundResource(R.drawable.icon_refresh);
                    imgDocumentStatus.setTag("0");

                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error in Uploading Documents : " + error.getMessage());

                imgDocumentStatus.setBackgroundResource(R.drawable.icon_refresh);
                IsUploadDone = false;
                hideDialog();

                imgDocumentStatus.setTag("0");
            }
        }) {

            /*@Override
            public Priority getPriority() {
                return priority;
            }*/


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = null;


                try {
                    /**
                     * Get Maxid of document
                     */
                    String qry_maxid = "select MAX(" + dbhandler.UPLOADDOC_ID + ") from " + dbhandler.TABLE_UPLOADDOC + "";
                    Cursor cur = sd.rawQuery(qry_maxid, null);
                    Log.d(TAG, " Total  " + cur.getCount() + " Records Found");
                    cur.moveToNext();
                    int maxid = cur.getInt(0);
                    Log.d(TAG, "MAXID BEFORE : " + maxid);
                    ++maxid;
                    Log.d(TAG, "MAXID AFTER : " + maxid);


                    params = new HashMap<String, String>();
                    params.put("type", "updatedocument");
                    params.put("documentid", String.valueOf(maxid));
                    params.put("routetype", "" + spnRoute.getSelectedItem());
                    params.put("userid", "" + userDetails.get(SessionManager.KEY_USERID));
                    params.put("senderid", "" + edtSenderid.getText().toString());
                    //params.put("photocode",""+base64_Photo);
                    params.put("documentcode", "" + base64_document);
                    // params.put("idproofcode",""+base64_idproof1);
                    //params.put("extradocument",""+base64_idproof2);


                    Log.d(TAG, "Documents Document Data : " + url_uploaddocs + params.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(str_upload);
    }

    private void SendBase64PhotoToServer() {


        showDialog();


        final Request.Priority priority = Request.Priority.HIGH;

        final String url_uploaddocs = AllKeys.TAG_WEBSITE_HAPPY + "/UpdatePhoto";
        Log.d(TAG, " URL Upload PHOTO : " + url_uploaddocs);


        imgPhoto.setImageBitmap(bitmap);

        final StringRequest str_upload = new StringRequest(Request.Method.POST, url_uploaddocs, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                Log.d(TAG, " Photo Upload Response  : " + response);


                if (!response.equals("0")) {

                    //Toast.makeText(getApplicationContext(), "Photo has been submitted", Toast.LENGTH_SHORT).show();


                    //Snackbar.make(coordinateLayout, "Photo has been submitted", Snackbar.LENGTH_SHORT).show();
                    imgPhotoStatus.setBackgroundResource(R.drawable.icon_success);
                    imgPhotoStatus.setTag("1");


                } else {

                    Snackbar.make(coordinateLayout, "Sorry, Try again...", Snackbar.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), "Sorry,Try again...", Toast.LENGTH_SHORT).show();

                    imgPhotoStatus.setBackgroundResource(R.drawable.icon_refresh);
                    imgPhotoStatus.setTag("0");
                }
                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IsUploadDone = false;
                Log.d(TAG, "Error in Uploading Photos : " + error.getMessage());

                hideDialog();
                imgPhotoStatus.setBackgroundResource(R.drawable.icon_refresh);
                imgPhotoStatus.setTag("0");

            }
        }) {
            /*@Override
            public Priority getPriority() {
                return priority;
            }*/


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = null;


                try {
                    /**
                     * Get Maxid of document
                     */
                    String qry_maxid = "select MAX(" + dbhandler.UPLOADDOC_ID + ") from " + dbhandler.TABLE_UPLOADDOC + "";
                    Cursor cur = sd.rawQuery(qry_maxid, null);
                    Log.d(TAG, " Total  " + cur.getCount() + " Records Found");
                    cur.moveToNext();
                    int maxid = cur.getInt(0);
                    Log.d(TAG, "MAXID BEFORE : " + maxid);
                    ++maxid;
                    Log.d(TAG, "MAXID AFTER : " + maxid);


                    params = new HashMap<String, String>();
                    params.put("type", "updatedocument");
                    params.put("documentid", String.valueOf(maxid));
                    params.put("routetype", "" + spnRoute.getSelectedItem());
                    params.put("userid", "" + userDetails.get(SessionManager.KEY_USERID));
                    params.put("senderid", "" + edtSenderid.getText().toString());
                    params.put("photocode", "" + base64_Photo);
                    //params.put("documentcode",""+base64_document);
                    // params.put("idproofcode",""+base64_idproof1);
                    //params.put("extradocument",""+base64_idproof2);


                    Log.d(TAG, "Documents Photo Data : " + url_uploaddocs + params.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return params;
            }

        };


        MyApplication.getInstance().addToRequestQueue(str_upload);


    }

    private void GetAllSenderIdRequestDetails() {


        showDialog();

        String url_getallData = AllKeys.TAG_WEBSITE_HAPPY + "/GetDocumentDetail?action=documentdetail&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, " URL GetAllSenderIDData : " + url_getallData);
        StringRequest str_getalldata = new StringRequest(Request.Method.GET, url_getallData, new Response.Listener<String>() {
            @Override

            public void onResponse(String response) {
                Log.d(TAG, "GetDocuemntDetails Response : " + response.toString());
                if (!response.equals("0")) {

                    response = dbhandler.convertToJsonFormat(response);

                    try {

                        sd.delete(dbhandler.TABLE_UPLOADDOC, null, null);


                        list_document.clear();

                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {


                            JSONObject c = arr.getJSONObject(i);

                            ContentValues cv = new ContentValues();


                            cv.put(dbhandler.UPLOADDOC_ID, c.getString(AllKeys.TAG_DOCUMENTID));
                            cv.put(dbhandler.UPLOADDOC_SENDERID, c.getString(AllKeys.TAG_SENDERID));
                            cv.put(dbhandler.UPLOADDOC_TYPE, c.getString(AllKeys.TAG_TYPE));
                            cv.put(dbhandler.UPLOADDOC_PHOTO, c.getString(AllKeys.TAG_PHOTO));
                            cv.put(dbhandler.UPLOADDOC_DOCUMENT, c.getString(AllKeys.TAG_DOCEMNT));
                            cv.put(dbhandler.UPLOADDOC_IDPROOF, c.getString(AllKeys.TAG_PROOF1));
                            cv.put(dbhandler.UPLOADDOC_IDPROOF2, c.getString(AllKeys.TAG_PROOF2));
                            cv.put(dbhandler.UPLOADDOC_STATUS, c.getString(AllKeys.TAG_STATUS));
                            cv.put(dbhandler.UPLOADDOC_APPROVAL_DATE, c.getString(AllKeys.TAG_APPROVALDATE));
                            cv.put(dbhandler.UPLOADDOC_REQUEST_DATE, c.getString(AllKeys.TAG_REQUESTEDDATE));

                            Log.d(TAG, "Data : " + cv.toString());

                            sd.insert(dbhandler.TABLE_UPLOADDOC, null, cv);

                            String status = c.getString(AllKeys.TAG_STATUS);
                            if(status.equals("1"))
                            {

                                status = "Approved";
                            }
                            else if(status.equals("0"))
                            {

                                status = "Not Approved";
                            }





                            UploadDocuments ud= new UploadDocuments(status,c.getString(AllKeys.TAG_SENDERID) ,c.getString(AllKeys.TAG_TYPE),c.getString(AllKeys.TAG_DOCUMENTID),c.getString(AllKeys.TAG_APPROVALDATE),c.getString(AllKeys.TAG_REQUESTEDDATE));
                            list_document.add(ud);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                if(list_document.size() == 0)
                {

                    txtnodata.setVisibility(View.VISIBLE);
                }
                else
                {

                    txtnodata.setVisibility(View.GONE);

                }
                adapter.notifyDataSetChanged();
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error in GetAllSenderIdData : " + error.getMessage());

                hideDialog();
            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getalldata);

    }
    //OnCreate Completed




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {


            this.menu = menu;
            getMenuInflater().inflate(R.menu.common_sync, menu);
            // sync_data = (MenuItem) menu.findItem(R.id.action_sync);


            menu_sync = (MenuItem) menu.findItem(R.id.common_sync);
            menu_show = (MenuItem) menu.findItem(R.id.common_show);

            menu_sync.setVisible(false);
            menu_show.setVisible(true);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.common_show)
        {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            fab.setVisibility(View.VISIBLE);
            llAddData.setVisibility(View.GONE);
            llShowData.setVisibility(View.VISIBLE);


        }
        else if (item.getItemId() == android.R.id.home) {


            Intent intent = new Intent(getApplicationContext(),
                    SettingsActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent intent = new Intent(getApplicationContext(),
                SettingsActivity.class);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }


    /**
     * Image Selection and Update Related Code
     */
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Profile Picture!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = Utility.checkPermission_ReadExternalStorage(context);

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
/*
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_CAMERA);
*/


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
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

    /**
     * Create a file Uri for saving an image or video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }


    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                AllKeys.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG2, "Oops! Failed create "
                        + AllKeys.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } /*else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        }*/ else {
            return null;
        }

        return mediaFile;
    }

    private void onCaptureImageResult(Intent data) {


        //get file
        File photo = new File(fileUri.getPath());///storage/emulated/0/Pictures/IMS APP/IMG_20160213_135407.jpg

        //file name
        String fileName = photo.getName();
        try {


            Integer.parseInt(fileName);

            //Uri selectedImageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};


            CursorLoader cursorLoader = new CursorLoader(this, fileUri, projection, null, null,
                    null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);///storage/emulated/0/Download/12042772_765400193585968_4006769710146743259_n.jpg

            fileUri = Uri.parse(selectedImagePath);

            photo = new File(fileUri.getPath());
            fileName = photo.getName();


        } catch (Exception e) {
            Log.d("Convertion Error : ", e.getMessage());
        }


        Calendar calendar = Calendar.getInstance();

        long startTime = calendar.getTimeInMillis();


        //resave file with new name
        File newFile = new File(userDetails.get(SessionManager.KEY_USERID) + startTime);
        photo.renameTo(newFile);


        //Display Selected Image To Bitmap


        //

        //Complete fill Selected Image  to Bitmap


        if (fileUri.getPath() != null) {
            // Displaying the image or video on the screen
            try {
                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();

                // down sizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;


                bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            //Toast.makeText(getApplicationContext(), "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
            Snackbar.make(coordinateLayout, "Sorry, file path is missing!", Snackbar.LENGTH_LONG).show();


        }


//        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);


        try {


            /*Imageview my_img_view = (Imageview ) findViewById (R.id.my_img_view);
            my_img_view.setImageBitmap(bitmap);
            */

            showDialog();

            if (DOCUMENTTYPE.toLowerCase().equals("photo")) {


                new SetDocumentRelaredDrawablesUsingAsyncTask().execute();
                /*imgPhoto.setImageBitmap(bitmap);

                base64_Photo = dbhandler.getStringImage(bitmap);

                SendBase64PhotoToServer();*/


            } else if (DOCUMENTTYPE.toLowerCase().equals("document")) {

                new SetDocumentRelaredDrawablesUsingAsyncTask().execute();


                /*imgDocument.setImageBitmap(bitmap);
                base64_document = dbhandler.getStringImage(bitmap);*/

            } else if (DOCUMENTTYPE.toLowerCase().equals("proof1")) {

                new SetDocumentRelaredDrawablesUsingAsyncTask().execute();


                /*imgIdProof.setImageBitmap(bitmap);
                crdIdProof2.setVisibility(View.VISIBLE);
                base64_idproof1 = dbhandler.getStringImage(bitmap);*/
            } else if (DOCUMENTTYPE.toLowerCase().equals("proof2")) {

                new SetDocumentRelaredDrawablesUsingAsyncTask().execute();


                /*imgIdProof2.setImageBitmap(bitmap);
                base64_idproof2 = dbhandler.getStringImage(bitmap);*/

            }
            hideDialog();

        } catch (Exception e) {
            hideDialog();
            e.printStackTrace();
        }


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


        //       imgProfilePic.setImageBitmap(bitmap);


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {

                showDialog();


                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

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

                try {

                    if (DOCUMENTTYPE.toLowerCase().equals("photo")) {

                        new SetDocumentRelaredDrawablesUsingAsyncTask().execute();

                        /*imgPhoto.setImageBitmap(bitmap);

                        base64_Photo = dbhandler.getStringImage(bitmap);
                        SendBase64PhotoToServer();*/

                    } else if (DOCUMENTTYPE.toLowerCase().equals("document")) {

                        new SetDocumentRelaredDrawablesUsingAsyncTask().execute();


/*
                        imgDocument.setImageBitmap(bitmap);
                        base64_document = dbhandler.getStringImage(bitmap);
*/

                    } else if (DOCUMENTTYPE.toLowerCase().equals("proof1")) {
                        new SetDocumentRelaredDrawablesUsingAsyncTask().execute();


                        /*imgIdProof.setImageBitmap(bitmap);
                        crdIdProof2.setVisibility(View.VISIBLE);
                        base64_idproof1 = dbhandler.getStringImage(bitmap);*/
                    } else if (DOCUMENTTYPE.toLowerCase().equals("proof2")) {

                        new SetDocumentRelaredDrawablesUsingAsyncTask().execute();

                        /*imgIdProof2.setImageBitmap(bitmap);
                        base64_idproof2 = dbhandler.getStringImage(bitmap);*/
                    }
                    hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error in SetImage");
                    hideDialog();
                }



                // getStringImage(bm);


            } catch (IOException e) {

                e.printStackTrace();
            }
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

            if (NetConnectivity.isOnline(context)) {
                ServiceHandler sh = new ServiceHandler();

                String URL_PROFILEPICUPDATE = AllKeys.TAG_WEBSITE_HAPPY + "/UpdateStudentProfile";
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

            imgDocument.setImageBitmap(bitmap);
            imgPhoto.setImageBitmap(bitmap);
            imgIdProof.setImageBitmap(bitmap);


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
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
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

        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

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
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
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


    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinateLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, UploadDocumentsActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                        } else {

                            Snackbar snackbar1 = Snackbar.make(coordinateLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_SHORT);
                            snackbar1.show();

                        }


                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

        textView.setTextColor(Color.YELLOW);

        snackbar.show();


    }


    public class SetDocumentRelaredDrawablesUsingAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute Execute Called");
            showDialog();
            //spotDialog.show();

            /*pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);

            pDialog.show();*/

            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "doInBackground Execute Called");



            if (DOCUMENTTYPE.toLowerCase().equals("photo")) {


                base64_Photo = dbhandler.getStringImage(bitmap);


            } else if (DOCUMENTTYPE.toLowerCase().equals("document")) {

                base64_document = dbhandler.getStringImage(bitmap);

            } else if (DOCUMENTTYPE.toLowerCase().equals("proof1")) {

                //crdIdProof2.setVisibility(View.VISIBLE);
                base64_idproof1 = dbhandler.getStringImage(bitmap);
            } else if (DOCUMENTTYPE.toLowerCase().equals("proof2")) {

                base64_idproof2 = dbhandler.getStringImage(bitmap);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            Log.d(TAG, "onPostExecute Execute Called");

            /*hideDialog();
            showDialog();*/

            if (DOCUMENTTYPE.toLowerCase().equals("photo")) {


//                imgPhoto.setImageBitmap(bitmap);


                SendBase64PhotoToServer();


            } else if (DOCUMENTTYPE.toLowerCase().equals("document")) {
                //imgDocument.setImageBitmap(bitmap);
                SendBase64DocumentToServer();


            } else if (DOCUMENTTYPE.toLowerCase().equals("proof1")) {

//                imgIdProof.setImageBitmap(bitmap);


                SendBase64IDProofToServer();

                crdIdProof2.setVisibility(View.VISIBLE);


            } else if (DOCUMENTTYPE.toLowerCase().equals("proof2")) {




                //imgIdProof2.setImageBitmap(bitmap);

                SendBase64IDProof2ToServer();

            }
/*            hideDialog();*/


        }
    }


}
