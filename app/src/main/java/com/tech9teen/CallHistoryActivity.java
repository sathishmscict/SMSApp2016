package com.tech9teen;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.R;
import com.tech9teen.adapter.CallLogAdapterRecyclerView;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.CallLogData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CallHistoryActivity extends AppCompatActivity {

    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();

    private dbhandler db;
    private SQLiteDatabase sd;
    private int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 120;
    private String TAG = CallHistoryActivity.class.getSimpleName();
    private MenuItem menu_sync, menu_show;
    private Menu menu;
    /*private ArrayList<String> listContactName= new ArrayList<String>();
    private ArrayList<String> listContactNumber= new ArrayList<String>();*/
    private int MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBERS = 122;
    private TextView txtContactname, txtDate, txtIncomingCalls, txtOutgoingCalls, txtMissedCalls;
    private ArrayList<String> list_callog_number = new ArrayList<String>();
    private ArrayList<String> list_callog_name = new ArrayList<String>();
    private ArrayList<String> list_callog_missed = new ArrayList<String>();
    private ArrayList<String> list_callog_incoming = new ArrayList<String>();
    private ArrayList<String> list_callog_outgoing = new ArrayList<String>();
    private ArrayList<String> list_callog_date = new ArrayList<String>();
    private ArrayList<CallLogData> list_calllog = new ArrayList<CallLogData>();
    private RecyclerView recyclerview_callog;

    private CallLogAdapterRecyclerView adapter;


    SpotsDialog spotDialog;
    private String currentdate = "";
    private CoordinatorLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        ++m;
        currentdate = y + "-" + m + "-" + d;


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(true);
        showDialog();

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();


        recyclerview_callog = (RecyclerView) findViewById(R.id.recycler_view_callog);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerview_callog.setLayoutManager(mLayoutManager);
        recyclerview_callog.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
        recyclerview_callog.setItemAnimator(new DefaultItemAnimator());






        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        setTitle("CallLog Hisotry");


        /**
         * Get all contact derails from device
         */
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CallHistoryActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CALL_LOG);

        } else {

            getCallDetails();

        }


        //FillDataOnRecyclerView();

        if (NetConnectivity.isOnline(context)) {


            if (list_calllog.size() > 0) {

                SendCallHistoryDetailsToServer();
            }


        } else {
            checkInternet();
        }


    }
    //Oncreate completeed

    private void SendCallHistoryDetailsToServer() {
        showDialog();


        final String url_sendcallhistory = AllKeys.TAG_WEBSITE_HAPPY + "/GetJSONForInsertCallHistoryDetail";
        StringRequest str_sendcallhistory = new StringRequest(Request.Method.POST, url_sendcallhistory, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.d(TAG, "Call History Response : " + response.toString());

                hideDialog();
                if (response.toString().equals("1")) {
                    Snackbar.make(coordinatorLayout, "Callog details has been synced", Snackbar.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Call Hisotry Error : " + error.getMessage());
                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                /**
                 *  Get Unique months  data from table
                 */
                String query_months = "select distinct date from " + dbhandler.TABLE_CALLOGMASTER + "";
                Log.d(TAG, "Query : " + query_months);
                Cursor cur = sd.rawQuery(query_months, null);
                ArrayList<String> list_months = new ArrayList<String>();

                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {

                        Log.d(TAG, "Month : " + cur.getString(cur.getColumnIndex(dbhandler.CALLOG_DATE)));
                        String mon = cur.getString(cur.getColumnIndex(dbhandler.CALLOG_DATE)).substring(3, 5);
                        Log.d(TAG, "Month : " + mon);
                        if (!list_months.contains(mon)) {
                            list_months.add(mon);

                        }

                    }
                    Log.d(TAG, "list-months Data : " + list_months.toString());
                    Log.d(TAG, "lst-months Data Size : " + list_months.size());

                }
/**
 * Complete GEt Unique months details from callog
 */

                Map<String, String> params = new HashMap<String, String>();


                params.put("action", "insertcallhistory");
                params.put("userid", userDetails.get(SessionManager.KEY_USERID));


                String json = "";


                Log.d(TAG, "Size of list_calllog : " + list_calllog.size());
                for (int i = 0; i < list_calllog.size(); i++) {

                    try {
                        Log.d(TAG, "Counter  :" + i);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("ContactId", String.valueOf(i));
                        jsonObject.accumulate("IncomingDuration", list_calllog.get(i).getIncomingcalls());
                        jsonObject.accumulate("OutgoingDuration", list_calllog.get(i).getOutgoingcalls());
                        jsonObject.accumulate("ContactName", list_calllog.get(i).getContactname());
                        jsonObject.accumulate("MobileNo", list_calllog.get(i).getMobilenumber());
                        jsonObject.accumulate("Date", dbhandler.convertToJsonDateFormat(list_calllog.get(i).getCalllogdate()));

                        json = json + jsonObject.toString() + ",";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                json = json.substring(0, json.length() - 1);
                json = "[" + json + "]";
                Log.d("CalllogMst Data : ", json);
                Log.d("Json Data : ", url_sendcallhistory + "?action=insertcallhistory&userid=" + userDetails.get(SessionManager.KEY_USERID) + "+&Data=" + json);
                params.put("Data", json);

                return params;
            }


        };


        MyApplication.getInstance().addToRequestQueue(str_sendcallhistory);


    }


    /**
     * Get All calllog
     */
    private class GetAllCallLogDetailsFromDevice extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                //content://call_log/calls
                StringBuffer sb = new StringBuffer();


                Calendar calendar = Calendar.getInstance();

                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
                String fromDate = String.valueOf(calendar.getTimeInMillis());
                calendar.clear();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                String toDate = String.valueOf(System.currentTimeMillis());

                String[] whereValue = {fromDate, toDate};
                Log.d(TAG, " Data Between Dates are : " + whereValue.toString());


                Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

//        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, CallLog.Calls.DATE + " DESC");


                Log.d(TAG, "Records " + managedCursor.getCount() + " Found");

                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);


                int callerName = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                sb.append("Call Log :");
                sd.delete(dbhandler.TABLE_CALLOGMASTER, null, null);
                while (managedCursor.moveToNext()) {
                    String phNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);

                    ////DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

                    //callDate = getDate(Long.valueOf(callDate), "dd-MM-yyyy hh:mm:ss");
                    callDate = getDate(Long.valueOf(callDate), "dd-MM-yyyy hh:mm: a");
                    String callname = managedCursor.getString(callerName);


               /* Date callDayTime = null;
                try {
                //    callDayTime = new Date(Long.valueOf(callDate));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }*/
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    int dircode = Integer.parseInt(callType);

                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }
                    sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDate.toString() + " \nCall duration in sec :--- " + callDuration);
                    sb.append("\n----------------------------------");

                    ContentValues cv = new ContentValues();
                    cv.put(dbhandler.CALLOG_DURATION, callDuration);
                    cv.put(dbhandler.CALLOG_CALLTYPE, dir);
                    //cv.put(dbhandler.CALLOG_DATE , callDayTime.toString());
                    Log.d(TAG, "Call Date : " + callDate.substring(0, 10));
                    Log.d(TAG, "Call Time : " + callDate.substring(callDate.length() - 8, callDate.length()));
                    //callDate = callDate.substring(0,10);

                    cv.put(dbhandler.CALLOG_DATE, callDate.substring(0, 10));
                    cv.put(dbhandler.CALLOG_TIME, callDate.substring(callDate.length() - 8, callDate.length()));
                    cv.put(dbhandler.CALLOG_MOBILE, phNumber);

                    if (callname == null) {
                        cv.put(dbhandler.CALLOG_NAME, "");
                    } else {
                        cv.put(dbhandler.CALLOG_NAME, callname);

                    }


                    Log.d(TAG, "Call Data :" + cv.toString());
                    if (dir != null) {

                        sd.insert(dbhandler.TABLE_CALLOGMASTER, null, cv);
                    }
                    cv.clear();


                }
                Log.d(TAG, "Call Log : " + sb.toString());
                //managedCursor.close(); textView.setText(sb); }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            db.backupDB(context);


            FillDataOnRecyclerView();
            hideDialog();
        }
    }


    private void getCallDetails() {








      /*  //content://call_log/calls
        showDialog();
        StringBuffer sb = new StringBuffer();


        Calendar calendar = Calendar.getInstance();

        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        String fromDate = String.valueOf(calendar.getTimeInMillis());
        calendar.clear();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        String toDate = String.valueOf(System.currentTimeMillis());

        String[] whereValue = {fromDate, toDate};
        Log.d(TAG, " Data Between Dates are : " + whereValue.toString());


        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

//        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, CallLog.Calls.DATE + " DESC");


        Log.d(TAG, "Records " + managedCursor.getCount() + " Found");

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);


        int callerName = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        sb.append("Call Log :");
        sd.delete(dbhandler.TABLE_CALLOGMASTER, null, null);
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            callDate = getDate(Long.valueOf(callDate), "dd/MM/yyyy hh:mm:ss");
            String callname = managedCursor.getString(callerName);


           *//* Date callDayTime = null;
            try {
            //    callDayTime = new Date(Long.valueOf(callDate));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }*//*
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);

            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDate.toString() + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");

            ContentValues cv = new ContentValues();
            cv.put(dbhandler.CALLOG_DURATION, callDuration);
            cv.put(dbhandler.CALLOG_CALLTYPE, dir);
            //cv.put(dbhandler.CALLOG_DATE , callDayTime.toString());
            Log.d(TAG, "Call Date : " + callDate.substring(0, 10));
            Log.d(TAG, "Call Time : " + callDate.substring(callDate.length() - 8, callDate.length()));
            //callDate = callDate.substring(0,10);

            cv.put(dbhandler.CALLOG_DATE, callDate.substring(0, 10));
            cv.put(dbhandler.CALLOG_TIME, callDate.substring(callDate.length() - 8, callDate.length()));
            cv.put(dbhandler.CALLOG_MOBILE, phNumber);

            if (callname == null) {
                cv.put(dbhandler.CALLOG_NAME, "");
            } else {
                cv.put(dbhandler.CALLOG_NAME, callname);

            }


            Log.d(TAG, "Call Data :" + cv.toString());
            if (dir != null) {

                sd.insert(dbhandler.TABLE_CALLOGMASTER, null, cv);
            }
            cv.clear();


        }
        Log.d(TAG, "Call Log : " + sb.toString());
        //managedCursor.close(); textView.setText(sb); }
        db.backupDB(context);
        FillDataOnRecyclerView();
*/


        try {
            new GetAllCallLogDetailsFromDevice().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void FillDataOnRecyclerView() {



        /**
         *  Get Unique months  data from table
         */
        String query_months = "select distinct date from " + dbhandler.TABLE_CALLOGMASTER + "";
        Log.d(TAG, "Query : " + query_months);
        Cursor cur = sd.rawQuery(query_months, null);
        ArrayList<String> list_months = new ArrayList<String>();

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {

                Log.d(TAG, "Month : " + cur.getString(cur.getColumnIndex(dbhandler.CALLOG_DATE)));
                String mon = cur.getString(cur.getColumnIndex(dbhandler.CALLOG_DATE)).substring(3, 5);
                Log.d(TAG, "Month : " + mon);
                if (!list_months.contains(mon)) {
                    list_months.add(mon);

                }

            }
            Log.d(TAG, "list-months Data : " + list_months.toString());
            Log.d(TAG, "lst-months Data Size : " + list_months.size());

        }
/**
 * Complete GEt Unique months details from callog
 */


        /**
         *Get Cal log Details based on months
         */

        list_callog_number.clear();
        list_callog_incoming.clear();
        list_callog_missed.clear();
        list_callog_number.clear();
        list_callog_name.clear();
        list_callog_date.clear();


        list_calllog.clear();


        for(int k=0;k<list_months.size();k++)
        {

            String query_calllog = "select distinct sum(" + dbhandler.CALLOG_DURATION + ")," + dbhandler.CALLOG_CALLTYPE + "," + dbhandler.CALLOG_DATE + "," + dbhandler.CALLOG_MOBILE + "," + dbhandler.CALLOG_NAME + " from " + dbhandler.TABLE_CALLOGMASTER + " where " + dbhandler.CALLOG_DATE + " like '%___" + list_months.get(k) + "_____%' and " + dbhandler.CALLOG_CALLTYPE + "!='MISSED' group by " + dbhandler.CALLOG_CALLTYPE + "," + dbhandler.CALLOG_DATE + "," + dbhandler.CALLOG_MOBILE + "," + dbhandler.CALLOG_NAME + " order by " + dbhandler.CALLOG_ID + " asc";

            query_calllog = "select distinct sum(" + dbhandler.CALLOG_DURATION + ")," + dbhandler.CALLOG_CALLTYPE + "," + dbhandler.CALLOG_DATE + "," + dbhandler.CALLOG_MOBILE + "," + dbhandler.CALLOG_NAME + " from " + dbhandler.TABLE_CALLOGMASTER + " where " + dbhandler.CALLOG_DATE + " like '%___" + list_months.get(k) + "_____%'  group by " + dbhandler.CALLOG_CALLTYPE + "," + dbhandler.CALLOG_DATE + "," + dbhandler.CALLOG_MOBILE + "," + dbhandler.CALLOG_NAME + " order by " + dbhandler.CALLOG_ID + " asc";
            Log.d(TAG, "Query Select Callog: " + query_calllog);
            Cursor cur_callog = sd.rawQuery(query_calllog, null);
            Log.d(TAG, "Records " + cur_callog.getCount() + " in " + dbhandler.TABLE_CALLOGMASTER);
            if (cur_callog.getCount() > 0) {

                while (cur_callog.moveToNext())

                {

                    //CallLogData cld=  new CallLogData();
                    try {


                        int res = list_callog_number.indexOf(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)));
                        Log.d(TAG, "Number Response : " + res);
                        Log.d(TAG, "Call Mobile Number : " + cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)));
                        Log.d(TAG, "Call Type  :" + cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)));
                        Log.d(TAG, "Caller Name : " + cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)));
                        Log.d(TAG, "Calling Date  :" + cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));
                        Log.d(TAG, "Call Duration  " + cur_callog.getString(0));


                        String type = cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE));

/*                    list_callog_number.add(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)));*/
                        if (res == -1) {


                            list_callog_number.add(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)));
                            list_callog_name.add(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)));
                            list_callog_date.add(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));


                            if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("INCOMING")) {


                                String dur = cur_callog.getString(0);


                                list_callog_incoming.add(cur_callog.getString(0));
                                //CallLogData(String contactname, String mobilenumber, String incomingcalls, String outgoingcalls, String missedcalls, String calllogdate) {
                                CallLogData CLD = new CallLogData(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)), cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)), cur_callog.getString(0), "0", "0", cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));
                                list_calllog.add(CLD);


                            } else if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("OUTGOING")) {

                                String dur = cur_callog.getString(0);

                                //CallLogData(String contactname, String mobilenumber, String incomingcalls, String outgoingcalls, String missedcalls, String calllogdate) {
                                CallLogData CLD = new CallLogData(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)), cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)), "0", cur_callog.getString(0), "0", cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));
                                list_calllog.add(CLD);
                                list_callog_outgoing.add(cur_callog.getString(0));


                            /*for (int ij = 0; ij < list_calllog.size(); ij++) {
                                if (list_calllog.get(ij).getMobilenumber().equals(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE))) && list_calllog.get(ij).getCalllogdate().equals(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)))) {
                                    list_calllog.get(ij).setOutgoingcalls(cur_callog.getString(0));
                                    Log.d(TAG, "Outging Durtion Set");
                                }

                            }*/


                            } else if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("MISSED")) {

                                list_callog_missed.add(cur_callog.getString(0));
                            }


                            //list_callog_number.add(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)));


                        } else {


                            // list_callog_number.remove(res);
                            String typee = cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE));

                            int resDatePos = list_callog_date.indexOf(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));
                            if (resDatePos == -1) {

                                list_callog_number.add(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)));
                                list_callog_name.add(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)));
                                list_callog_date.add(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));


                                if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("INCOMING")) {
                                    String dur = cur_callog.getString(0);

                                    list_callog_incoming.add(cur_callog.getString(0));
                                    //CallLogData(String contactname, String mobilenumber, String incomingcalls, String outgoingcalls, String missedcalls, String calllogdate) {
                                    CallLogData CLD = new CallLogData(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)), cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)), cur_callog.getString(0), "0", "0", cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));
                                    list_calllog.add(CLD);


                                } else if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("OUTGOING")) {

                                    String dur = cur_callog.getString(0);

                                    //CallLogData(String contactname, String mobilenumber, String incomingcalls, String outgoingcalls, String missedcalls, String calllogdate) {
                                    CallLogData CLD = new CallLogData(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)), cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)), "0", cur_callog.getString(0), "0", cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));
                                    list_calllog.add(CLD);
                                    list_callog_outgoing.add(cur_callog.getString(0));


                            /*for (int ij = 0; ij < list_calllog.size(); ij++) {
                                if (list_calllog.get(ij).getMobilenumber().equals(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE))) && list_calllog.get(ij).getCalllogdate().equals(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)))) {
                                    list_calllog.get(ij).setOutgoingcalls(cur_callog.getString(0));
                                    Log.d(TAG, "Outging Durtion Set");
                                }

                            }*/


                                } else if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("MISSED")) {

                                    list_callog_missed.add(cur_callog.getString(0));
                                }


                            } else {


                                if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("INCOMING")) {

                                    String dur = cur_callog.getString(0);
                                    list_callog_incoming.add(cur_callog.getString(0));
                                    //CallLogData(String contactname, String mobilenumber, String incomingcalls, String outgoingcalls, String missedcalls, String calllogdate) {
                                /*CallLogData CLD = new CallLogData(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)), cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)), cur_callog.getString(0), "0", "0", cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));
                                list_calllog.add(CLD);*/

                                    list_calllog.get(res).setIncomingcalls(cur_callog.getString(0));


                                } else if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("OUTGOING")) {
                                    String dur = cur_callog.getString(0);

                                    //CallLogData(String contactname, String mobilenumber, String incomingcalls, String outgoingcalls, String missedcalls, String calllogdate) {
                                    //CallLogData CLD = new CallLogData(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_NAME)), cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE)), "0", cur_callog.getString(0), "0", cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)));
                                /*list_calllog.add(CLD);
                                list_callog_outgoing.add(cur_callog.getString(0));*/

                                    list_calllog.get(res).setOutgoingcalls(cur_callog.getString(0));



                            /*for (int ij = 0; ij < list_calllog.size(); ij++) {
                                if (list_calllog.get(ij).getMobilenumber().equals(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_MOBILE))) && list_calllog.get(ij).getCalllogdate().equals(cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_DATE)))) {
                                    list_calllog.get(ij).setOutgoingcalls(cur_callog.getString(0));
                                    Log.d(TAG, "Outging Durtion Set");
                                }

                            }*/


                                } else if (cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)).equals("MISSED")) {

                                    list_callog_missed.add(cur_callog.getString(0));
                                }


                            }
                        }

                        // list_callog_number.add(cur_callog.getString(3));
                        Log.d(TAG, "Call Type : " + cur_callog.getString(cur_callog.getColumnIndex(dbhandler.CALLOG_CALLTYPE)));
                        //Log.d(TAG , "Call Type : "+cur_callog.getString(1));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                Log.d(TAG, "Incoming Call " + list_callog_incoming.size() + " Records Found");
                Log.d(TAG, "Outgoing Call " + list_callog_outgoing.size() + " Records Found");
                Log.d(TAG, "Missed Call " + list_callog_missed.size() + " Records Found");
                Log.d(TAG, "Contacts " + list_callog_number.size() + " Records Found");

                Log.d(TAG, "Date " + list_callog_date.size() + " Records Found");
                Log.d(TAG, "Contacts : " + list_callog_number.toString());
            }




        }
        /**
         * Get all DEtails of calllog based on months
         */







        Calendar cal = Calendar.getInstance();
        cal.get(Calendar.MONTH);
        int month = cal.get(Calendar.MONTH);
        ;
        ++month;




        adapter = new CallLogAdapterRecyclerView(context, list_calllog);
        recyclerview_callog.setAdapter(adapter);
        Log.d(TAG, "Total Records : " + list_calllog.size());

        hideDialog();


        if (list_calllog.size() > 0) {

            if (NetConnectivity.isOnline(context)) {

                SendCallHistoryDetailsToServer();
            } else {

                checkInternet();
            }
        }


    }


    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat   Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CALL_LOG) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted
                getCallDetails();

            } else {
                //Denied permission
                Toast.makeText(context, "Read Call Log Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBERS) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted
                // getContactDetails();

            } else {
                //Denied permission
                Toast.makeText(context, "Read Contact Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
                    MenuActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

        } else if (item.getItemId() == R.id.common_show) {

            //fab.setVisibility(View.VISIBLE);
            //lladddata.setVisibility(View.GONE);
            //llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(true);
            menu_show.setVisible(false);

        } else if (item.getItemId() == R.id.common_sync) {


            //fab.setVisibility(View.VISIBLE);
            //lladddata.setVisibility(View.GONE);
            //llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(true);
            menu_show.setVisible(false);

            if (list_calllog.size() > 0) {

                SendCallHistoryDetailsToServer();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent intent = new Intent(getApplicationContext(),
                MenuActivity.class);

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


    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, CallHistoryActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                        } else {

                            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_SHORT);
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


}
