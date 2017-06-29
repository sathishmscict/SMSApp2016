package com.tech9teen;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.R;
import com.tech9teen.adapter.MenuAdapterRecyclerView;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.ContactsData;
import com.tech9teen.pojo.MenuItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recycler_menu;
    private Context context = this;
    private ArrayList<MenuItems> menuData = new ArrayList<MenuItems>();
    private MenuAdapterRecyclerView adapter;

    //"SendSMS", "Message Templates", "News","Visiting Card Reader", "Digital Card", "Reminders", "Schedule SMS", "Call Management","Trending Videos","Logout"};
    //, "Digital Card","Logout"
    private String[] mItemMenu = new String[]{
            "SendSMS", "Group Master", "Message Templates", "News", "Visiting Card Reader", "Reminders", "SMS History", "Call History", "Trending Videos"};

    private int[] mItemMenu_Icon = new int[]{
            R.drawable.icon_sendsms, R.drawable.icon_group_master, R.drawable.icon_message_template,
            R.drawable.icon_newspaper, R.drawable.icon_visiting_card,
            R.drawable.icon_reminders2, R.drawable.icon_schedule_alaram,
            R.drawable.icon_call_history, R.drawable.icon_videos};
    private Menu menu;
    private String TAG = MenuActivity.class.getSimpleName();
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private MenuItem menu_grid;
    private MenuItem menu_list;
    private dbhandler db;
    private SQLiteDatabase sd;

    private ArrayList<ContactsData> listContacts = new ArrayList<ContactsData>();
    private SpotsDialog spotDialog;
    private ProgressDialog pDialog;
    private TextView txtGoldRate, txtDollorRate;
    private LinearLayout lldata;


    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }





        /*pDialog = new ProgressDialog(context);
        pDialog.show();
*/

        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);


        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();


//       SendUserDetailsToServer("GMAIL");


        db = new dbhandler(context);
        sd = db.getWritableDatabase();
        sd = db.getReadableDatabase();


        /*sd.delete(dbhandler.TABLE_GROUPMASTER,null, null);
        sd.delete(dbhandler.TABLE_GROUPMASTERDET, null, null);*/

        /**
         * Check Login Credentials
         */


        /*Intent mid=new Intent(context , DashBoardActivity.class);
        startActivity(mid);*/

        int res = sessionmanager.checkLogin();

//        res=1;
        if (res == 0) {

            if (userDetails.get(SessionManager.KEY_VERSTATUS).equals("1")) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            MyFirebaseInstanceIDService mfs = new MyFirebaseInstanceIDService();
                            mfs.onTokenRefresh();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "OnTokenRefresh Error : " + e.getMessage());
                        }
                        // mfs.onTokenRefreshNew(context);


                    }
                });


                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MenuActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            AllKeys.MY_PERMISSIONS_REQUEST_WRTE_EXTERNAL_STORAGE);

                } else {


                    db.backupDB(context);


                }


            } else {

                if (userDetails.get(SessionManager.KEY_USER_MOBILE).length() != 10) {

                    Intent in = new Intent(context, MobileNumberActivity.class);
                    startActivity(in);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                } else {
                    Intent in = new Intent(context, NewVerificationActivity.class);
                    startActivity(in);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                }

            }

        }


        //dbhandler.Notify(getString(R.string.app_name), "Welcome to " + userDetails.get(SessionManager.KEY_PROFILE_NAME) , context);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String invite = "You're invited! use referal code " + userDetails.get(SessionManager.KEY_REF_CODE) + " and  Enjoy special balance of 100 SMS at StudyField SMSApp. Install now! https://play.google.com/store/apps/details?id=com.studyfield";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            invite);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        txtGoldRate = (TextView) findViewById(R.id.txtGoldRate);
        txtDollorRate = (TextView) findViewById(R.id.txtDollorRate);
        recycler_menu = (RecyclerView) findViewById(R.id.my_recycler_view_menu);
        lldata = (LinearLayout) findViewById(R.id.lldata);


        blink_Animation();


        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler_menu.setLayoutManager(mLayoutManager);
        recycler_menu.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
        recycler_menu.setItemAnimator(new DefaultItemAnimator());


        menuData.clear();
        for (int i = 0; i < mItemMenu.length; i++) {
            MenuItems mi = new MenuItems(mItemMenu[i], mItemMenu_Icon[i]);

            menuData.add(mi);

        }

        adapter = new MenuAdapterRecyclerView(context, menuData);
        recycler_menu.setAdapter(adapter);

        recycler_menu.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_menu, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                String itemname = ((TextView) view.findViewById(R.id.txtMenuName)).getText().toString();

                //    Toast.makeText(getApplicationContext(), "Menu :" + itemname, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Menu  :" + itemname);
                if (itemname.toLowerCase().equals("message templates")) {
                    Intent intent = new Intent(getApplicationContext(),
                            MessageTemplateActivity.class);

                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                } else if (itemname.toLowerCase().equals("trending videos")) {

                    Intent intent = new Intent(getApplicationContext(), TrendingVideosAcivity.class);
//                    Intent intent = new Intent(getApplicationContext(),VideoActivity.class);


                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                } else if (itemname.toLowerCase().equals("group master")) {

                    Intent intent = new Intent(getApplicationContext(),
                            GroupMasterActivity.class);

                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                } else if (itemname.toLowerCase().equals("reminders")) {

                    Intent intent = new Intent(getApplicationContext(),
                            RemindersAcivity.class);

                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                } else if (itemname.toLowerCase().equals("call history")) {


                    showDialog();
                    Intent intent = new Intent(getApplicationContext(),
                            CallHistoryActivity.class);

                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                    hideDialog();

                } else if (itemname.toLowerCase().equals("sendsms")) {
                    Intent intent = new Intent(getApplicationContext(),
                            SendSMSActivity.class);

                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                } else if (itemname.toLowerCase().toLowerCase().equals("sms history")) {
                    Intent intent = new Intent(getApplicationContext(),
                            SMSGroupActivity.class);

                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                } else {
                    /*Intent mid = new Intent(context, DashBoardActivity.class);
                    startActivity(mid);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*/
                    Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        /*getGoldRateFromServer();
        getDollorRateFromServer();
        getGroupMasterDetailsFromServer();
        getGroupMasterDataDetailsFromServer();
        getTemplateMasterDetailsFromServer();
        getReminderMasterDetailsFromServer();
*/


        if (!userDetails.get(SessionManager.KEY_USERID).equals("0") && userDetails.get(SessionManager.KEY_VERSTATUS).equals("1")) {

            getGroupMasterDetailsFromServer();
            getGroupMasterDataDetailsFromServer();
            getTemplateMasterDetailsFromServer();
            getReminderMasterDetailsFromServer();
            getSMSSettingMasterDetailsFromServer();
            GetSMSCategoryDetailsFromServer();
            GetSMSSubCategoryDetailsFromServer();


            /**
             * Get all contact derails from device
             */
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MenuActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                        AllKeys.MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBERS);

            } else {


                /**
                 * Chekc Conatct table if empty then Fetch contacts and insert into table
                 */
                Cursor managedCursor = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


                    /*String query = "select * from "+ dbhandler.TABLE_CONTACTMASTER +"";
                    Cursor cur = sd.rawQuery(query , null);*/
                Log.d(TAG, "Total " + managedCursor.getCount() + " Records Found in Device");
                if (managedCursor.getCount() != Integer.parseInt(userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_CONTACTS)) || userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_CONTACTS).equals("0")) {

                    new GetContactUsingAsyncTask().execute();

                }
                managedCursor.close();
                ;


            }


            if (!userDetails.get(SessionManager.KEY_DAY_OF_MONTH).equals(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))) {
                getGoldRateFromServer();
                getDollorRateFromServer();


            } else {

                txtDollorRate.setText(userDetails.get(SessionManager.KEY_DOLLOR_PRICE));
                txtGoldRate.setText(userDetails.get(SessionManager.KEY_GOLD_RATE));
            }


        }


    }

    private void getSMSSettingMasterDetailsFromServer() {


        showDialog();
        String url_getSMSSettings = AllKeys.TAG_WEBSITE_HAPPY + "/GetSMSSettings?action=smssetting&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetSMSSettings: " + url_getSMSSettings);


        JsonArrayRequest str_getSMSSettings = new JsonArrayRequest(url_getSMSSettings, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GetSMSSettings Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_SMSSETTINGS, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv = new ContentValues();


                            cv.put(dbhandler.SMSSETTINGS_TYPE, c.getString(AllKeys.TAG_SETTINGS_TYPE));
                            cv.put(dbhandler.SMSSETTINGS_SENDERID, c.getString(AllKeys.TAG_SETTINGS_SENDERID));
                            cv.put(dbhandler.SMSSETTINGS_USERNAME, c.getString(AllKeys.TAG_SETTINGS_USERNAME));
                            cv.put(dbhandler.SMSSETTINGS_PASSWORD, c.getString(AllKeys.TAG_SETTINGS_PASSWORD));
                            Log.d(TAG, "SMSSettings Master Data : " + cv.toString());

                            sd.insert(dbhandler.TABLE_SMSSETTINGS, null, cv);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GetSMSSettings : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getSMSSettings);


    }

    private void getReminderMasterDetailsFromServer() {


        showDialog();
        String url_getReminders = AllKeys.TAG_WEBSITE_HAPPY + "/GetAnniversaryBdayReminder?action=getreminder&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetReminders: " + url_getReminders);


        JsonArrayRequest str_getReminders = new JsonArrayRequest(url_getReminders, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GetReminders Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_REMINDER, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv = new ContentValues();

                            cv.put(dbhandler.REMINDER_Descr, c.getString(AllKeys.TAG_REMINDER_DESCR));
                            cv.put(dbhandler.REMINDER_DATE, c.getString(AllKeys.TAG_REMINDER_DATE));
                            cv.put(dbhandler.REMINDER_TIME, c.getString(AllKeys.TAG_REMINDER_TIME));
                            cv.put(dbhandler.REMINDER_ID, c.getString(AllKeys.TAG_REMINDER_ID));
                            Log.d(TAG, "Reminder Master Data : " + cv.toString());

                            sd.insert(dbhandler.TABLE_REMINDER, null, cv);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GetReminders : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getReminders);


    }

    private void getTemplateMasterDetailsFromServer() {


        showDialog();
        String url_getMsgTemplates = AllKeys.TAG_WEBSITE_HAPPY + "/GetMsgTemplate?action=getmsgtemplate&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetMsgTemplates: " + url_getMsgTemplates);


        JsonArrayRequest str_getMsgTemplates = new JsonArrayRequest(url_getMsgTemplates, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GetMsgTemplates Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_TEMPALTE_MASTER, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv = new ContentValues();


                            cv.put(dbhandler.TEMPLATE_TITLE, c.getString(AllKeys.TAG_TEMPALTE_TITLE));
                            cv.put(dbhandler.TEMPLATE_DESCR, c.getString(AllKeys.TAG_TEMPALTE_DESCR));
                            cv.put(dbhandler.TEMPLATE_ID, c.getString(AllKeys.TAG_TEMPALTEID));
                            Log.d(TAG, "Template Master Data : " + cv.toString());

                            //sd.insert(dbhandler.TABLE_TEMPALTE_MASTER, null, cv);
                            //dbhandler.InsertData(sd,dbhandler.TABLE_TEMPALTE_MASTER,cv);


                            sd.insert(dbhandler.TABLE_TEMPALTE_MASTER, null, cv);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GetMsgTemplates : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getMsgTemplates);


    }

    private void getGroupMasterDataDetailsFromServer() {


        showDialog();
        String url_getGroupMasterData = AllKeys.TAG_WEBSITE_HAPPY + "/GetGroupMasterDetail?action=getgroupdetail&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetGroupMasterDetails: " + url_getGroupMasterData);


        JsonArrayRequest str_getgroupsData = new JsonArrayRequest(url_getGroupMasterData, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GroupMasterDetails Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_GROUPMASTERDET, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv_grp = new ContentValues();


                            cv_grp.put(dbhandler.GROUPMASTER_DET_ID, c.getString(AllKeys.TAG_GROUP_DET_ID));
                            cv_grp.put(dbhandler.GROUPMASTER_DET_GROUPMASTER_ID, c.getString(AllKeys.TAG_GROUP_DET_GROUP_ID));
                            cv_grp.put(dbhandler.GROUPMASTER_DET_MOBILENUMBER, c.getString(AllKeys.TAG_GROUP_DET_MOBILENO));
                            cv_grp.put(dbhandler.GROUPMASTER_DET_CONTACTNAME, c.getString(AllKeys.TAG_GROUP_DET_CONTACT_NAME));
                            cv_grp.put(dbhandler.GROUPMASTER_DET_CONTACTID, c.getString(AllKeys.TAG_GROUP_DET_CONTACTID));

                            Log.d(TAG, "GroupMasterDet Data : " + cv_grp.toString());
                            sd.insert(dbhandler.TABLE_GROUPMASTERDET, null, cv_grp);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GroupMasterDet : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getgroupsData);


    }

    private void getGroupMasterDetailsFromServer() {

        showDialog();
        String url_getGroupMaster = AllKeys.TAG_WEBSITE_HAPPY + "/GetGroupMaster?action=getgroupmaster&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetGroupMaster : " + url_getGroupMaster);


        JsonArrayRequest str_getgroups = new JsonArrayRequest(url_getGroupMaster, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GroupMaster Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_GROUPMASTER, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv_grp = new ContentValues();

                            cv_grp.put(dbhandler.GROUPMASTER_ID, c.getString(AllKeys.TAG_GROUPID));
                            cv_grp.put(dbhandler.GROUPMASTER_NAME, c.getString(AllKeys.TAG_GROUPNAME));
                            cv_grp.put(dbhandler.GROUPMASTER_CONTACTS, c.getString(AllKeys.TAG_NO_OF_CONTACTS));
                            Log.d(TAG, "GroupMaster Data : " + cv_grp.toString());
                            sd.insert(dbhandler.TABLE_GROUPMASTER, null, cv_grp);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GroupMaster : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getgroups);


    }

    private void GetSMSSubCategoryDetailsFromServer() {


        showDialog();
        String url_smssubcategory = AllKeys.TAG_WEBSITE_HAPPY + "/GetSubCategory?action=subcategory&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL Get Subcategory : " + url_smssubcategory);
        JsonArrayRequest str_subcategory = new JsonArrayRequest(url_smssubcategory, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                Log.d(TAG, "Subcategory Response : " + response.toString());
                if (response != null) {
                    try {
                        //sd.delete(dbhandler.TABLE_SMS_SUBCATEGORY_MASTER, null, null);
                        sd.delete(dbhandler.TABLE_SENDERID_MASTER, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);

                            ContentValues cv = new ContentValues();

                            /*cv.put(dbhandler.SMS_SUBCATEGORY_MASTER_ID, c.getString(AllKeys.TAG_SMS_SUB_CATEGORY_ID));
                            cv.put(dbhandler.SMS_SUBCATEGORY_MASTER_NAME, c.getString(AllKeys.TAG_SMS_SUB_CATEGORY_NAME));
                            cv.put(dbhandler.SMS_SUBCATEGORY_MASTER_CATEGORYID, c.getString(AllKeys.TAG_SMS_CATEGORYID));
                            sd.insert(dbhandler.TABLE_SMS_SUBCATEGORY_MASTER, null, cv);*/

                            cv.put(dbhandler.SMS_SENDERID_ID, c.getString(AllKeys.TAG_SMS_SUB_CATEGORY_ID));
                            cv.put(dbhandler.SMS_SENDERID_NAME, c.getString(AllKeys.TAG_SMS_SUB_CATEGORY_NAME));
                            cv.put(dbhandler.SMS_SENDERID_CATEGORYID, c.getString(AllKeys.TAG_SMS_CATEGORYID));
                            sd.insert(dbhandler.TABLE_SENDERID_MASTER, null, cv);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error in smsSubcategory : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_subcategory);

    }

    private void GetSMSCategoryDetailsFromServer() {

        showDialog();
        String url_smscategory = AllKeys.TAG_WEBSITE_HAPPY + "/GetCategory?action=category&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GEt Category : " + url_smscategory);
        JsonArrayRequest str_getcategory = new JsonArrayRequest(url_smscategory, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response != null) {
                    Log.d(TAG, "SMS Category Response : " + response.toString());

                    sd.delete(dbhandler.TABLE_SMS_CATEGORY_MASTER, null, null);

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.getJSONObject(i);


                            ContentValues cv = new ContentValues();

                            cv.put(dbhandler.SMS_CATEGORY_MASTER_ID, c.getString(AllKeys.TAG_SMS_CATEGORYID));
                            cv.put(dbhandler.SMS_CATEGORY_MASTER_NAME, c.getString(AllKeys.TAG_SMS_CATEGORYNAME));
                            /*cv.put(dbhandler.SMS_CATEGORY_MASTER_TYPE, "W");*/


                            sd.insert(dbhandler.TABLE_SMS_CATEGORY_MASTER, null, cv);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }//Close if condition

                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "SMS Category Error : " + error.getMessage());
                hideDialog();

            }
        });

        MyApplication.getInstance().addToRequestQueue(str_getcategory);
    }

    private void getDollorRateFromServer() {

        showDialog();
        String url_dollorrate = AllKeys.TAG_DOLLOR_RATE_API;
        Log.d(TAG, "Dollor Rate API URL :" + url_dollorrate);

        StringRequest str_dollorrate = new StringRequest(Request.Method.GET, url_dollorrate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Dollor Rate API Response : " + response.toLowerCase());


                try {
                    String res = response.replace("\"dataset\":{", "\"dataset\": [{");
                    Log.d(TAG, "Before Data : " + res);
                    res = res.replace("}}", "}]}");
                    Log.d(TAG, "After Data : " + res);

                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.getJSONArray("dataset");


                    Log.d(TAG, "Total Records : " + arr.length());
                    for (int i = 0; i < arr.length(); i++) {

                        JSONObject c = arr.getJSONObject(i);
                        Log.d(TAG, "Dollor ID : " + c.getString("id"));

                        JSONArray cc = c.getJSONArray("data");
                        Log.d(TAG, "Dollor data : " + cc.toString());
                        Log.d(TAG, "Dollor data Records : " + cc.length());

                        for (int j = 0; j <= 0; j++) {

                            Log.d(TAG, "Dollor  RAte : " + cc.get(j));

                            String data = cc.get(j).toString();
                            Log.d(TAG, "Data Before :" + data);
                            data = data.substring(1, data.length() - 1);

                            Log.d(TAG, "Data After :" + data);
                            List<String> myList = new ArrayList<String>(Arrays.asList(data.split(",")));


                            txtDollorRate.setText(myList.get(1));

                            userDetails = sessionmanager.getUserDetails();

                            sessionmanager.setDollorAndGoldRateFromAPI(myList.get(1), userDetails.get(SessionManager.KEY_GOLD_RATE), "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));


                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Dollor Rate Error  : " + error.getMessage());

                hideDialog();

            }
        });

        MyApplication.getInstance().addToRequestQueue(str_dollorrate);


    }

    private void getGoldRateFromServer() {
        showDialog();

        String url_goldrate = AllKeys.TAG_GOLD_RATE_API;
        Log.d(TAG, "Gold Rate API URL: " + url_goldrate);
        //JSONObject str_goldrate = new JSONObject(Request.Method.GET ,url_goldrate,null,new JSONObject() )
        JsonObjectRequest str_goldrate = new JsonObjectRequest(Request.Method.GET, url_goldrate, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Gold Rate Response : " + response.toString());

                try {
                    //Log.d(TAG ,  "Data :"+response.getString("errors"));
                    Log.d(TAG, "ID :" + response.getInt("id"));
                    Log.d(TAG, "Source Name :" + response.getString("source_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i <= 0; i++) {

                        Log.d(TAG, "Gold RAte : " + arr.get(i));

                        String data = arr.get(i).toString();
                        Log.d(TAG, "Data Before :" + data);
                        data = data.substring(1, data.length() - 1);

                        Log.d(TAG, "Data After :" + data);
                        List<String> myList = new ArrayList<String>(Arrays.asList(data.split(",")));

                        txtGoldRate.setText(myList.get(4));

                        userDetails = sessionmanager.getUserDetails();

                        sessionmanager.setDollorAndGoldRateFromAPI(userDetails.get(SessionManager.KEY_DOLLOR_PRICE), myList.get(4), "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                    }
                    Log.d(TAG, "Array Size : " + arr.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Gold Rate Error : " + error.toString());

                hideDialog();
            }
        });

        MyApplication.getInstance().addToRequestQueue(str_goldrate);

    }
//OnCreate completed


    /**
     * Recycler View Click Handker
     */
    /**
     * Handle RecyclerView Handle
     */
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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

    /**
     * Complete Recycler Touch Handler
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        try {

            this.menu = menu;
            getMenuInflater().inflate(R.menu.menu_options, menu);
            // sync_data = (MenuItem) menu.findItem(R.id.action_sync);


            menu_grid = (MenuItem) menu.findItem(R.id.action_menugrid);
            menu_list = (MenuItem) menu.findItem(R.id.action_menulist);

            menu_grid.setVisible(false);
            menu_list.setVisible(true);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            //       Toast.makeText(getApplicationContext(), "Logout ", Toast.LENGTH_SHORT).show();


            sessionmanager.logoutUser();
            context.deleteDatabase(dbhandler.dbname);

        } else if (item.getItemId() == R.id.action_menugrid) {

            menu_grid.setVisible(false);
            menu_list.setVisible(true);

            GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            recycler_menu.setLayoutManager(mLayoutManager);
            //recycler_menu.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
            recycler_menu.setItemAnimator(new DefaultItemAnimator());


        } else if (item.getItemId() == R.id.action_menulist) {


            menu_grid.setVisible(true);
            menu_list.setVisible(false);


            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            recycler_menu.setLayoutManager(mLayoutManager);
            //recycler_menu.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
            recycler_menu.setItemAnimator(new DefaultItemAnimator());

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * get Device contact numbers
     */
    /**
     * Get Current user contacts from device
     */
    public class GetContactUsingAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                dbhandler.DeleteTableData(sd, dbhandler.TABLE_CONTACTMASTER);


                Cursor managedCursor = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                Log.d(TAG, "Total " + managedCursor.getCount() + " Contact Records  Found ");
                sessionmanager.setPreviousContactsTotalCount(String.valueOf(managedCursor.getCount()));

                if (managedCursor.getCount() > 0) {
                    int cnt = 0;
                    listContacts.clear();
                    while (managedCursor.moveToNext()) {
                        ++cnt;

                        String mobilenumber = managedCursor.getString(2);
                        mobilenumber = mobilenumber.replaceAll("\\D+", "");

                        //Log.d(TAG, cnt + " ID : " + managedCursor.getString(0) + " Number : " + mobilenumber + " Name  : " + managedCursor.getString(1));

                        ContentValues cv = new ContentValues();
                        cv.put(dbhandler.CONTACTMASTER_NAME, managedCursor.getString(1));
                        cv.put(dbhandler.CONTACTMASTER_MOBILE_NO, mobilenumber);
                        cv.put(dbhandler.CONTACTMASTER_ID, cnt);

                        dbhandler.InsertData(sd, dbhandler.TABLE_CONTACTMASTER, cv);

                        ContactsData cd = new ContactsData(mobilenumber, managedCursor.getString(1), cnt, false);
                        listContacts.add(cd);


                    }

                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideDialog();
            getContactDetailsAndSendtoServer();


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == AllKeys.MY_PERMISSIONS_REQUEST_WRTE_EXTERNAL_STORAGE) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted
                db.backupDB(context);
            } else {
                //Denied permission
                Toast.makeText(context, "Write External Storage Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }
        }

        if (requestCode == AllKeys.MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBERS) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted

                /**
                 * Chekc Conatct table if empty then Fetch contacts and insert into table
                 */


                Cursor managedCursor = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


                    /*String query = "select * from "+ dbhandler.TABLE_CONTACTMASTER +"";
                    Cursor cur = sd.rawQuery(query , null);*/
                Log.d(TAG, "Total " + managedCursor.getCount() + " Records Found in Device");
                if (managedCursor.getCount() != Integer.parseInt(userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_CONTACTS)) || userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_CONTACTS).equals("0")) {

                    new GetContactUsingAsyncTask().execute();

                }
                managedCursor.close();
                ;


                //getContactDetailsAndSendtoServer();


            } else {
                //Denied permission
                Toast.makeText(context, "Read Contacts Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void getContactDetailsAndSendtoServer() {

//        showDialog();



       /*         try {

                    dbhandler.DeleteTableData(sd, dbhandler.TABLE_CONTACTMASTER);


                    Cursor managedCursor = getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                    Log.d(TAG, "Total Records : " + managedCursor.getCount());
                    if (managedCursor.getCount() > 0) {
                        int cnt = 0;
                        listContacts.clear();
                        while (managedCursor.moveToNext()) {
                            ++cnt;

                            String mobilenumber = managedCursor.getString(2);
                            mobilenumber = mobilenumber.replaceAll("\\D+", "");

                            Log.d(TAG, cnt + " ID : " + managedCursor.getString(0) + " Number : " + mobilenumber + " Name  : " + managedCursor.getString(1));

                            ContentValues cv = new ContentValues();
                            cv.put(dbhandler.CONTACTMASTER_NAME, managedCursor.getString(1));
                            cv.put(dbhandler.CONTACTMASTER_MOBILE_NO, mobilenumber);
                            cv.put(dbhandler.CONTACTMASTER_ID, cnt);
                            ContactsData cd = new ContactsData(mobilenumber, managedCursor.getString(1), cnt, false);
                            listContacts.add(cd);

                            dbhandler.InsertData(sd, dbhandler.TABLE_CONTACTMASTER, cv);

                        }

                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
*/

        final String url_sendcontacts = AllKeys.TAG_WEBSITE_HAPPY + "/GetJSONForInsertContactDetail";
        StringRequest str_sendcontacts = new StringRequest(Request.Method.POST, url_sendcontacts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Contacts Sync Response : " + response.toString());

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Contact Sync Error :" + error.getMessage());

                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = null;
                try {
                    params = new HashMap<String, String>();


                    params.put("action", "synccontact");
                    params.put("userid", userDetails.get(SessionManager.KEY_USERID));


                    String json = "";


                    Log.d(TAG, "Total " + listContacts.size() + " Records From ContactMaster");
                    for (int i = 0; i < listContacts.size(); i++) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("ContactId", listContacts.get(i).getContacactid());
                            jsonObject.accumulate("ContactName", listContacts.get(i).getName());


                            jsonObject.accumulate("MobileNo", listContacts.get(i).getMobilenumber());

                            json = json + jsonObject.toString() + ",";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    json = json.substring(0, json.length() - 1);
                    json = "[" + json + "]";
                    Log.d("CalllogMst Data : ", json);
                    Log.d("Json Data : ", url_sendcontacts + "?action=synccontact&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&Data=" + json);
                    params.put("Data", json);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return params;


            }
        };

        MyApplication.getInstance().addToRequestQueue(str_sendcontacts);


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


    /**
     * Blink Book tickets
     */
    private void blink_Animation() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (lldata.getVisibility() == View.VISIBLE) {
                            lldata.setVisibility(View.INVISIBLE);
                        } else {
                            lldata.setVisibility(View.VISIBLE);
                        }
                        blink_Animation();
                    }
                });
            }
        }).start();
    }


    private void SendUserDetailsToServer(String logintype) {
        //Toast.makeText(getApplicationContext() , "User deails has been sended",Toast.LENGTH_SHORT).show();

        showDialog();

        userDetails = sessionmanager.getUserDetails();

        String url_senduserdata = AllKeys.TAG_WEBSITE_HAPPY + "/SignupCall?action=signup&email=jaiswalanant339@gmail.com&device_type=" + AllKeys.DEVICETYPE + "&device_id=" + Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID) + "&name=AnanatJaiswal&logintype=" + logintype + "";
        Log.d(TAG, "URL Signup User " + url_senduserdata);


        JsonArrayRequest str_request = new JsonArrayRequest(url_senduserdata, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Signup Response : " + response.toString());

                if (response != null) {

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);


                            String userid = c.getString(AllKeys.TAG_USERID);
                            String username = c.getString(AllKeys.TAG_USERNAME);
                            String userEmail = c.getString(AllKeys.TAG_USEREMAIL);
                            String mobileno = c.getString(AllKeys.TAG_MOBILENO);
                            String dob = c.getString(AllKeys.TAG_DOB);
                            String doa = c.getString(AllKeys.TAG_DOA);
                            String logintype = c.getString(AllKeys.TAG_LOGINTYPE);
                            String refpoints = c.getString(AllKeys.TAG_REFPOINTS);
                            String refcode = c.getString(AllKeys.TAG_REFERAALCODE);
                            String status = c.getString(AllKeys.TAG_STATUS);
                            String newUser = c.getString(AllKeys.TAG_NEWUSER);
                            String userType = c.getString(AllKeys.TAG_USERTYPE);


                            sessionmanager.createLoginSession(userid, username, userEmail, mobileno, dob, doa, logintype, refpoints, refcode, status, newUser, userType);


                            if (newUser.equals("1")) {

                                Intent in = new Intent(context, MobileNumberActivity.class);
                                startActivity(in);
                                finish();
                                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                            } else {

                                Intent in = new Intent(context, NewVerificationActivity.class);
                                startActivity(in);
                                finish();
                                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                            }

                            //sessionmanager.setLoginType("gmail");


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Sorry, can’t login. Try again", Toast.LENGTH_SHORT).show();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Signup Json Error : " + error.getMessage());
                hideDialog();
                Toast.makeText(context, "Sorry, can’t login. Try again", Toast.LENGTH_SHORT).show();

            }
        });


        MyApplication.getInstance().addToRequestQueue(str_request);


    }

}
