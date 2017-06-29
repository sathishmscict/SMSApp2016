package com.tech9teen;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.tech9teen.R;
import com.tech9teen.adapter.SMSCategoryAdapterRecyclerView;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.SMSGroupData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


public class SMSGroupActivity extends AppCompatActivity {


    private Context context = this;
    private SpotsDialog spotDialog;
    private CoordinatorLayout coordinatorLayout;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private RecyclerView recyclerView;
    private TextView txtnodata;
    private SMSCategoryAdapterRecyclerView adapter;
    private ArrayList<SMSGroupData> list_smsgroup = new ArrayList<SMSGroupData>();
    private String TAG = SMSGroupActivity.class.getSimpleName();
    private Integer RECORD_POS = 0;
    private int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 121;

    private int MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBERS = 100;
    private int MY_PERMISSIONS_REQUEST_WRTE_EXTERNAL_STORAGE = 101;
    private Menu menu;
    private MenuItem menu_sync, menu_show;
    private LinearLayout lladddata, llshowdata;
    private InputMethodManager imm;
    private FloatingActionButton fab;
    private TextView txt_groupname;
    private Button btnSaveData;
    private TextInputLayout input_group;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsgroupactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(true);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }



        lladddata = (LinearLayout) findViewById(R.id.lladddata);

        llshowdata = (LinearLayout) findViewById(R.id.llshowdata);

        lladddata.setVisibility(View.GONE);
        llshowdata.setVisibility(View.VISIBLE);


        txt_groupname = (TextView) findViewById(R.id.edtgroup);
        input_group = (TextInputLayout) findViewById(R.id.input_layout_edtgroup);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        btnSaveData = (Button) findViewById(R.id.btnSaveData);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //GetAllContactsFromDevice();


                txt_groupname.setText("");
                btnSaveData.setText("save data");


                lladddata.setVisibility(View.VISIBLE);
                llshowdata.setVisibility(View.GONE);


                fab.setVisibility(View.GONE);

                menu_show.setVisible(true);
                menu_sync.setVisible(false);


            }
        });


        sessionmanager = new SessionManager(context);

        userDetails = sessionmanager.getUserDetails();

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_sms);
        txtnodata = (TextView) findViewById(R.id.txtnodata);





        txtnodata.setVisibility(View.GONE);
        adapter = new SMSCategoryAdapterRecyclerView(this, list_smsgroup);


        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean errorFlag = false;
                if (txt_groupname.getText().toString().equals("")) {

                    errorFlag = true;
                    input_group.setErrorEnabled(true);
                    input_group.setError("Please enter Group name");

                } else {

                    input_group.setErrorEnabled(false);

                }

                if (errorFlag == false) {


                    if (NetConnectivity.isOnline(context)) {

                        if (btnSaveData.getText().toString().toLowerCase().equals("save data")) {

                            Log.d(TAG, "Insert Data called");


                            if (NetConnectivity.isOnline(context)) {
                                showDialog();


                                int maxid;


                                String query_maxid = "select MAX(" + dbhandler.SMS_CATEGORY_MASTER_ID + ") from " + dbhandler.TABLE_SMS_CATEGORY_MASTER + "";
                                Log.d(TAG, "Query GetMaxId : " + query_maxid);
                                Cursor cur = sd.rawQuery(query_maxid, null);
                                cur.moveToNext();
                                maxid = cur.getInt(0);
                                Log.d(TAG, "Before Maxid : " + maxid);
                                ++maxid;
                                Log.d(TAG, "After Maxid : " + maxid);


                                String Data = null;
                                try {
                                    JSONObject obj = new JSONObject();
                                    obj.accumulate("CategoryId", maxid);
                                    obj.accumulate("CategoryName", dbhandler.convertEncodedString(txt_groupname.getText().toString()));
                                    Log.d(TAG, "Group Data : " + obj.toString());
                                    Data = "[" + obj.toString() + "]";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String url_addgroup = AllKeys.TAG_WEBSITE_HAPPY + "/GetJSONForInsertCategoryDetail?action=insertcategory&type=single&Data=" + Data + "&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
                                Log.d(TAG, "URL Insert Category :" + url_addgroup);

                                final int finalMaxid = maxid;
                                StringRequest str_addgroup = new StringRequest(Request.Method.GET, url_addgroup, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Log.d(TAG, "Reponse Insert Category : " + response.toLowerCase());

                                        if (response.equals("1")) {
                                            /**
                                             * Insert data into smsCategoryTable
                                             */
                                            try {

                                                ContentValues cv = new ContentValues();
                                                cv.put(dbhandler.SMS_CATEGORY_MASTER_ID, finalMaxid);
                                                cv.put(dbhandler.SMS_CATEGORY_MASTER_NAME, txt_groupname.getText().toString());

                                                sd.insert(dbhandler.TABLE_SMS_CATEGORY_MASTER, null, cv);


                                                txt_groupname.setText("");

                                                Snackbar.make(coordinatorLayout, "Group has been added", Snackbar.LENGTH_LONG);


                                            } catch (Exception e) {
                                                e.printStackTrace();


                                                Snackbar.make(coordinatorLayout, "Group has not added", Snackbar.LENGTH_LONG);

                                            }


                                        }


                                        FillDataOnRecyclerView();
                                        hideDialog();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        hideDialog();
                                        Snackbar.make(coordinatorLayout, "Group has not added", Snackbar.LENGTH_LONG);

                                    }
                                });
                                MyApplication.getInstance().addToRequestQueue(str_addgroup);

                            } else {
                                checkInternet();
                            }


                        }//close save data if condition
                        else {

                            Log.d(TAG, "Update  Data called");


                            if (NetConnectivity.isOnline(context)) {
                                showDialog();


                                int maxid;

                                maxid = list_smsgroup.get(RECORD_POS).getGroup_id();


                                String Data = null;
                                try {
                                    JSONObject obj = new JSONObject();
                                    obj.accumulate("CategoryId", maxid);
                                    obj.accumulate("CategoryName", dbhandler.convertEncodedString(txt_groupname.getText().toString()));
                                    Log.d(TAG, "Group Data : " + obj.toString());
                                    Data = "[" + obj.toString() + "]";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String url_addgroup = AllKeys.TAG_WEBSITE_HAPPY + "/GetJSONForInsertCategoryDetail?action=insertcategory&type=single&Data=" + Data + "&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
                                Log.d(TAG, "URL Insert Category :" + url_addgroup);

                                final int finalMaxid = maxid;
                                StringRequest str_addgroup = new StringRequest(Request.Method.GET, url_addgroup, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Log.d(TAG, "Reponse Insert Category : " + response.toLowerCase());

                                        if (response.equals("1")) {
                                            /**
                                             * Insert data into smsCategoryTable
                                             */
                                            try {

                                                ContentValues cv = new ContentValues();
                                                cv.put(dbhandler.SMS_CATEGORY_MASTER_ID, finalMaxid);
                                                cv.put(dbhandler.SMS_CATEGORY_MASTER_NAME, txt_groupname.getText().toString());

                                                sd.update(dbhandler.TABLE_SMS_CATEGORY_MASTER, cv, "" + dbhandler.SMS_CATEGORY_MASTER_ID + "=" + list_smsgroup.get(RECORD_POS).getGroup_id() + "", null);


                                                txt_groupname.setText("");
                                                Snackbar.make(coordinatorLayout, "Group has been updated", Snackbar.LENGTH_LONG);


                                                btnSaveData.setText("save data");

                                                FillDataOnRecyclerView();

                                                lladddata.setVisibility(View.GONE);
                                                llshowdata.setVisibility(View.VISIBLE);
                                                fab.setVisibility(View.VISIBLE);


                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                Snackbar.make(coordinatorLayout, "Group has not updated", Snackbar.LENGTH_LONG);
                                            }


                                        }


                                        hideDialog();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        hideDialog();
                                        Snackbar.make(coordinatorLayout, "Group has not added", Snackbar.LENGTH_LONG);

                                    }
                                });
                                MyApplication.getInstance().addToRequestQueue(str_addgroup);

                            } else {
                                checkInternet();
                            }


                        }//Close Update DAta


                    }//Close check net connectivity if condition
                    else {
                        checkInternet();
                    }


                }


            }
        });
        //Complet BtnSaveDataClick Listener


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);


        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new dbhandler.GridSpacingItemDecoration(2, dbhandler.dpToPx(5, context), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                //Toast.makeText(getApplicationContext(), "clicked" + list_smsgroup.get(position).toString(), Toast.LENGTH_SHORT).show();

                Log.d(TAG, "CLicked : " + list_smsgroup.get(position).toString());


                sessionmanager.setMessageGroupId(String.valueOf(list_smsgroup.get(position).getGroup_id()));

                Intent intent = new Intent(context, AddSenderIdForMessageLogActivity.class);
                intent.putExtra("CATEGORYID", "" + list_smsgroup.get(position).getGroup_id());
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        FillDataOnRecyclerView();


        /**
         * RecyclerView Swiping Gesture
         */
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {


                                    Log.d(TAG, "Left Detected");

                                    /**
                                     * Get Current Template Details edit on Controls
                                     */
                                    SMSGroupData sm = list_smsgroup.get(position);
                                    Log.d(TAG, "Edited Template Data : " + sm.toString());


                                    txt_groupname.setText(list_smsgroup.get(position).getGroupname());

                                    RECORD_POS = position;


                                    lladddata.setVisibility(View.VISIBLE);
                                    llshowdata.setVisibility(View.GONE);
                                    menu_show.setVisible(true);
                                    menu_sync.setVisible(false);
                                    btnSaveData.setText("update data");
                                    fab.setVisibility(View.GONE);


                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(final RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {

                                    //Toast.makeText(context, "Right Detected", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Right Detected");

                                    if (NetConnectivity.isOnline(context)) {
                                        showDialog();


                                        String url_deleteTemplate = AllKeys.TAG_WEBSITE_HAPPY + "/DeleteCategoryMaster?action=deletecategorymaster&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&categoryid=" + list_smsgroup.get(position).getGroup_id() + "";
                                        Log.d(TAG, "URL Delete Group : " + url_deleteTemplate);
                                        StringRequest str_deleterequest = new StringRequest(Request.Method.GET, url_deleteTemplate, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                Log.d(TAG, "Delete Group Response : " + response.toString());

                                                if (response.equals("1")) {

                                                    sd.delete(dbhandler.TABLE_SMS_CATEGORY_MASTER, dbhandler.SMS_CATEGORY_MASTER_ID + " = " + list_smsgroup.get(position).getGroup_id(), null);
                                                    list_smsgroup.remove(position);
                                                    adapter.notifyItemRemoved(position);
                                                    Snackbar.make(coordinatorLayout, "Record has been deleted", Snackbar.LENGTH_SHORT).show();

                                                } else {
                                                    Snackbar.make(coordinatorLayout, "Sorry, Group has not deleted .Try again...", Snackbar.LENGTH_SHORT).show();

                                                }


                                                hideDialog();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Log.d(TAG, "Error in deleting Group : " + error.getMessage());

                                                hideDialog();
                                                Snackbar.make(coordinatorLayout, "Sorry, Group has not deleted .Try again...", Snackbar.LENGTH_SHORT).show();

                                            }
                                        });
                                        MyApplication.getInstance().addToRequestQueue(str_deleterequest);


                                    } else {
                                        checkInternet();
                                    }


                                }
                                adapter.notifyDataSetChanged();
                            }
                        });


        recyclerView.addOnItemTouchListener(swipeTouchListener);


        /**
         * Get all contact derails from device
         */
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SMSGroupActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CALL_LOG);

        } else {

            /*new GetContactUsingAsyncTask().execute();*/


        }


        /**
         * Check New Message Arrives or not
         */

        Uri uriSMSURI = Uri.parse("content://sms");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);

        Log.d(TAG, "Total " + cur.getCount() + " Messages found");
        if (cur.getCount() != Integer.parseInt(userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_MESSAGE)) || cur.getCount() == 0) {
            //sessionmanager.setPreviousMessageTotalCount(String.valueOf(cur.getCount()));
            new GetAllMessageDetailsFromDevice().execute();

        }


        /**
         * Complet eChekcing new message received or not based on count
         */


        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String carrierName = manager.getNetworkOperatorName();


            Log.d(TAG, "Sim Operators  :" + carrierName + manager.getSimOperator() + manager.getSimOperatorName());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    //OnCreate Completed


    public class GetAllMessageDetailsFromDevice extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {


            List<String> sms = new ArrayList<String>();
            Uri uriSMSURI = Uri.parse("content://sms");
            Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
            Log.d(TAG, "Total " + cur.getCount() + " Records Found in MessageLog");
            android.util.Log.i(TAG, "COLUMNS" + Arrays.toString(cur.getColumnNames()));
            sessionmanager.setPreviousMessageTotalCount(String.valueOf(cur.getCount()));

            try {
                TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr.getLine1Number();
                Log.d(TAG, "Mobile Number : " + mPhoneNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }


            sd.delete(dbhandler.TABLE_MESSAGELOGMASTER, null, null);

            sd.execSQL("delete from sqlite_sequence where name='" + dbhandler.TABLE_MESSAGELOGMASTER + "'");
            ;

            while (cur.moveToNext()) {
                String address = cur.getString(cur.getColumnIndex("address"));

                String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                String date = cur.getString(cur.getColumnIndex("date"));
                String serviceCenter = cur.getString(cur.getColumnIndex("service_center"));
                int type = cur.getInt(cur.getColumnIndex("type"));

                String personName = "" + cur.getString(cur.getColumnIndex("person"));

                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

                String time = formatter.format(calendar.getTime());


                String smstype = "";
                if (type == 1) {

                    smstype = "INBOX";
                } else if (type == 2) {
                    smstype = "OUTBOX";

                } else if (type == 3) {

                    smstype = "DRAFT";
                }


                sms.add("Number: " + address + " .Message: " + body);


       /*         if(address.toLowerCase().contains("cftlcc"))
                {
*/

                Log.d(TAG, "Time  : " + time);


                date = time.substring(0, 10);
                time = time.substring(11, 19);


                // Get time from date


                /*}*/


                try {

                    String NewName = "" + getContactName(
                            getApplicationContext(),
                            address);


                    if (!NewName.equals("null")) {

                        Log.d(TAG, "Contact Name : " + NewName);
                        //address = NewName;
                        personName = NewName;


                    }



/*
                        Log.d(TAG, "Address : " + address);
                        Log.d(TAG, "Body  :" + body);
                        Log.d(TAG, "Date  :" + date);
                        Log.d(TAG, "Time  :" + time);
                        Log.d(TAG, "Type : " + type);
                        Log.d(TAG, "SMS TYPE : " + smstype);
                        Log.d(TAG, "Person Name : " + personName);
                        Log.d(TAG, "Service Center :" + serviceCenter);
*/


                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    ContentValues cv = new ContentValues();
                    cv.put(dbhandler.MESSAGELOG_BODY, body);

                    if (personName.equals("null")) {

                        cv.put(dbhandler.MESSAGELOG_NAME, address);
                    } else {

                        cv.put(dbhandler.MESSAGELOG_NAME, personName);
                    }

                    cv.put(dbhandler.MESSAGELOG_ADDRESS, address);
                    cv.put(dbhandler.MESSAGELOG_DATE, date);
                    cv.put(dbhandler.MESSAGELOG_TIME, time);

                    cv.put(dbhandler.MESSAGELOG_TYPE, smstype);


                    cv.put(dbhandler.MESSAGELOG_SENDERID, "1");

                    Log.d(TAG, "Insert Data : " + cv.toString());
                    sd.insert(dbhandler.TABLE_MESSAGELOGMASTER, null, cv);

                    //MessageLogData(String message, String time, String messageId, String date, String address, String name, String type) {


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error in inserting Message Log  : " + e.getMessage());
                }


            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            hideDialog();
            db.backupDB(context);
            sendAllMessageDetailsToServer();

        }


    }

    private void sendAllMessageDetailsToServer() {

        showDialog();


        final String url_sendmessagedata = AllKeys.TAG_WEBSITE_HAPPY + "/GetJSONForInsertMessageHistoryDetail";
        Log.d(TAG, "URL Send Data : " + url_sendmessagedata);

        StringRequest str_sendMessageData = new StringRequest(Request.Method.POST, url_sendmessagedata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.d(TAG, "Resposne of Send Message Data : " + response.toString());

                if (response.equals("1")) {
                    Snackbar.make(coordinatorLayout, "Data has been synced..", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(coordinatorLayout, "Data has been not synced..", Snackbar.LENGTH_SHORT).show();
                }

                hideDialog();


            }


        }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d(TAG, "Error in Seng Message Log  " + error.getMessage());

                hideDialog();

            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<String, String>();


                params.put("action", "insertmessage");
                params.put("userid", userDetails.get(SessionManager.KEY_USERID));


                String json = "";

                String que = "select * from " + dbhandler.TABLE_MESSAGELOGMASTER + "";
                Log.d(TAG, "Query  :" + que);

                Cursor cur = sd.rawQuery(que, null);
                Log.d(TAG, "Total " + cur.getCount() + " Records found in " + dbhandler.TABLE_MESSAGELOGMASTER);

                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {


//                        MessageLogData msg = new MessageLogData(cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_BODY)), cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_TIME)), cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_ID)), cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_DATE)), cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_ADDRESS)), cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_NAME)), cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_TYPE)));

                        /*for (int i = 0; i < list_messagelog.size(); i++)
                        {
*/
                        try {
                            //Log.d(TAG , "Counter  :"+i);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("MessageId", cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_ID)));
                            jsonObject.accumulate("Address", cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_ADDRESS)));
                            jsonObject.accumulate("Name", dbhandler.convertEncodedString(cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_NAME))));
                            jsonObject.accumulate("Message", dbhandler.convertEncodedString(cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_BODY))));
                            //jsonObject.accumulate("Message", cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_BODY)));
                            //jsonObject.accumulate("Time",dbhandler.convertEncodedString(cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_TIME))));
                            jsonObject.accumulate("Time", cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_TIME)));
                            jsonObject.accumulate("Date", dbhandler.convertToJsonDateFormat(cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_DATE))));
                            jsonObject.accumulate("Type", cur.getString(cur.getColumnIndex(dbhandler.MESSAGELOG_TYPE)));


                            json = json + jsonObject.toString() + ",";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


  /*                      }*/


                    }

                }


                json = json.substring(0, json.length() - 1);
                json = "[" + json + "]";
                Log.d("MessageLog Data : ", json);
                Log.d("Json Data : ", url_sendmessagedata + "?action=insertmessage&userid=" + userDetails.get(SessionManager.KEY_USERID) + "+&Data=" + json);
                params.put("Data", json);


                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(str_sendMessageData);


    }


    private void FillDataOnRecyclerView() {

        String query = "Select * from " + dbhandler.TABLE_SMS_CATEGORY_MASTER + "";
        Log.d(TAG, "Query Select all smscategory : " + query);

        Cursor cur = sd.rawQuery(query, null);
        Log.d(TAG, "Total " + cur.getCount() + " Records available");


        if (cur.getCount() > 0) {




            new MaterialShowcaseView.Builder(this)
                    .setTarget(recyclerView)
                    .setDismissText("GOT IT")
                    .setContentText("Swipe Left to Edit \n Swipe Right to Delete")
                    //.setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                    .singleUse("SHOWCASE_ID") // provide a unique ID used to ensure it is only shown once
                    .show();


            txtnodata.setVisibility(View.GONE);
            list_smsgroup.clear();
            while (cur.moveToNext()) {

                SMSGroupData sms = new SMSGroupData(cur.getString(cur.getColumnIndex(dbhandler.SMS_CATEGORY_MASTER_NAME)), cur.getInt(cur.getColumnIndex(dbhandler.SMS_CATEGORY_MASTER_ID)), 0);
                list_smsgroup.add(sms);


            }

            adapter.notifyDataSetChanged();

        } else {
            txtnodata.setVisibility(View.VISIBLE);
        }


    }

    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "" + getString(R.string.no_network2), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, SMSGroupActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_right, R.anim.slide_left);

                        } else {

                            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "" + getString(R.string.no_network2), Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.YELLOW);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {

            this.menu = menu;
            getMenuInflater().inflate(R.menu.common_sync, menu);
            // sync_data = (MenuItem) menu.findItem(R.id.action_sync);


            menu_sync = (MenuItem) menu.findItem(R.id.common_sync);
            menu_show = (MenuItem) menu.findItem(R.id.common_show);

            menu_sync.setVisible(false);
            menu_show.setVisible(false);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(getApplicationContext(),
                    DashBoardActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

        } else if (item.getItemId() == R.id.common_show) {

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            fab.setVisibility(View.VISIBLE);
            lladddata.setVisibility(View.GONE);
            llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(false);
            menu_show.setVisible(false);

        } else if (item.getItemId() == R.id.common_sync) {


            //fab.setVisibility(View.VISIBLE);
            lladddata.setVisibility(View.GONE);
            llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(false);
            menu_show.setVisible(false);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context,
                DashBoardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }


    public void showDialog() {


        if (!spotDialog.isShowing()) {
            spotDialog.show();
        }
    }

    public void hideDialog() {
        if (spotDialog.isShowing()) {


            spotDialog.dismiss();

        }

    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private SMSGroupActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SMSGroupActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRTE_EXTERNAL_STORAGE) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted
                db.backupDB(context);
            } else {
                //Denied permission
                Toast.makeText(context, "Write External Storage Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBERS) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted

                /**
                 * Chekc Conatct table if empty then Fetch contacts and insert into table
                 */
                String check_contacts = "Select * from " + dbhandler.TABLE_CONTACTMASTER + "";
                Cursor cur_check_contacts = sd.rawQuery(check_contacts, null);
                if (cur_check_contacts.getCount() == 0) {
                    //GetContactDetailsFromDevice();
                    //new GetContactUsingAsyncTask().execute();

                    //new GetAllMessageDetailsFromDevice().execute();


                }


            } else {
                //Denied permission
                Toast.makeText(context, "Read Contacts Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }


}


