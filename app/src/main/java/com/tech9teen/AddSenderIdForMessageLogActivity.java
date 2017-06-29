package com.tech9teen;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;


public class AddSenderIdForMessageLogActivity extends AppCompatActivity {

    private Context context=this;
    private SpotsDialog spotDialog;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout lladddata;
    private LinearLayout llshowdata;
    private TextView txt_groupname;
    private TextInputLayout input_group;
    private InputMethodManager imm;
    private Button btnSaveData;
    private FloatingActionButton fab;
    private MenuItem menu_show,menu_sync;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails=new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private RecyclerView recyclerView;
    private TextView txtnodata;
    private ArrayList<SMSGroupData> list_smsgroup= new ArrayList<SMSGroupData>();
    private SMSCategoryAdapterRecyclerView adapter;
    private String TAG = AddSenderIdForMessageLogActivity.class.getSimpleName();
    private int RECORD_POS=0;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sender_id_for_message_log);
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
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

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


        fab.setVisibility(View.VISIBLE);

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
                    input_group.setError("Please enter sender or user name");

                } else {

                    input_group.setErrorEnabled(false);

                }

                if (errorFlag == false) {


                    if (NetConnectivity.isOnline(context)) {

                        if (btnSaveData.getText().toString().toLowerCase().equals("save data"))
                        {

                            Log.d(TAG, "Insert Data called");


                            if (NetConnectivity.isOnline(context))
                            {
                                showDialog();


                                int maxid;

                                String query_maxid = "select MAX(" + dbhandler.SMS_SENDERID_ID + ") from " + dbhandler.TABLE_SENDERID_MASTER + "";
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
                                    obj.accumulate("SubCategoryId", maxid);
                                    obj.accumulate("CategoryId", userDetails.get(SessionManager.KEY_MESSAGE_GROUPID));
                                    obj.accumulate("SubCategoryName",dbhandler.convertEncodedString(txt_groupname.getText().toString()));
                                    Log.d(TAG, "SenderID Data : " + obj.toString());
                                    Data = "[" + obj.toString() + "]";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String url_addgroup = AllKeys.TAG_WEBSITE_HAPPY + "/GetJSONForInsertSubCategoryDetail?action=insertsubcategory&type=single&Data="+ Data +"&userid="+ userDetails.get(SessionManager.KEY_USERID) +"";
                                Log.d(TAG, "URL Insert SenderID :" + url_addgroup);



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
                                                cv.put(dbhandler.SMS_SENDERID_ID, finalMaxid);
                                                cv.put(dbhandler.SMS_SENDERID_NAME, txt_groupname.getText().toString());
                                                cv.put(dbhandler.SMS_SENDERID_CATEGORYID , userDetails.get(SessionManager.KEY_MESSAGE_GROUPID));

                                                    sd.insert(dbhandler.TABLE_SENDERID_MASTER, null, cv);

                                                    Snackbar.make(coordinatorLayout, "SenderId has been added", Snackbar.LENGTH_LONG);

                                                txt_groupname.setText("");

                                                FillDataOnRecyclerView();



                                            } catch (Exception e) {
                                                e.printStackTrace();


                                                    Snackbar.make(coordinatorLayout, "SenderId has not added", Snackbar.LENGTH_LONG);

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
                                    obj.accumulate("SubCategoryId", maxid);
                                    obj.accumulate("CategoryId", userDetails.get(SessionManager.KEY_MESSAGE_GROUPID));
                                    obj.accumulate("SubCategoryName",dbhandler.convertEncodedString(txt_groupname.getText().toString()));
                                    Log.d(TAG, "SenderID Data : " + obj.toString());
                                    Data = "[" + obj.toString() + "]";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String url_addgroup = AllKeys.TAG_WEBSITE_HAPPY + "/GetJSONForInsertSubCategoryDetail?action=insertsubcategory&type=single&Data="+ Data +"&userid="+ userDetails.get(SessionManager.KEY_USERID) +"";
                                Log.d(TAG, "URL Insert SenderID :" + url_addgroup);



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
                                                cv.put(dbhandler.SMS_SENDERID_ID, finalMaxid);
                                                cv.put(dbhandler.SMS_SENDERID_NAME, txt_groupname.getText().toString());
                                                cv.put(dbhandler.SMS_SENDERID_CATEGORYID , userDetails.get(SessionManager.KEY_MESSAGE_GROUPID));





                                                sd.update(dbhandler.TABLE_SENDERID_MASTER, cv, "" + dbhandler.SMS_SENDERID_ID + "=" + list_smsgroup.get(RECORD_POS).getGroup_id() + "", null);

                                                FillDataOnRecyclerView();


                                                txt_groupname.setText("");
                                                Snackbar.make(coordinatorLayout, "Senderid has been updated", Snackbar.LENGTH_LONG);


                                                btnSaveData.setText("save data");

                                                FillDataOnRecyclerView();

                                                lladddata.setVisibility(View.GONE);
                                                llshowdata.setVisibility(View.VISIBLE);
                                                fab.setVisibility(View.VISIBLE);



                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                Snackbar.make(coordinatorLayout, "Senderid has not updated", Snackbar.LENGTH_LONG);
                                            }


                                        }



                                        hideDialog();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        hideDialog();
                                        Snackbar.make(coordinatorLayout, "Senderid has not added", Snackbar.LENGTH_LONG);

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


        recyclerView.addOnItemTouchListener(new AddSenderIdForMessageLogActivity.RecyclerTouchListener(getApplicationContext(), recyclerView, new AddSenderIdForMessageLogActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                //Toast.makeText(getApplicationContext(), "clicked" + list_smsgroup.get(position).toString(), Toast.LENGTH_SHORT).show();

                Log.d(TAG, "CLicked : " + list_smsgroup.get(position).toString());


                Intent intent = new Intent(context, MessageLogDataActivity.class);
                intent.putExtra("SENDERID", "" + list_smsgroup.get(position).getGroup_id());

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


                                        String url_deleteTemplate = AllKeys.TAG_WEBSITE_HAPPY + "/DeleteSubCategoryMaster?action=deletesubcategory&userid="+ userDetails.get(SessionManager.KEY_USERID) +"&subcategoryid="+ list_smsgroup.get(position).getGroup_id() +"";
                                        Log.d(TAG, "URL Delete Group : " + url_deleteTemplate);
                                        StringRequest str_deleterequest = new StringRequest(Request.Method.GET, url_deleteTemplate, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                Log.d(TAG, "Delete Group Response : " + response.toString());

                                                if (response.equals("1")) {

                                                    sd.delete(dbhandler.TABLE_SENDERID_MASTER, dbhandler.SMS_SENDERID_ID + " = " + list_smsgroup.get(position).getGroup_id(), null);
                                                    list_smsgroup.remove(position);
                                                    adapter.notifyItemRemoved(position);

                                                } else {
                                                    Snackbar.make(coordinatorLayout, "Sorry, Group has not deleted .Try again...", Snackbar.LENGTH_SHORT).show();

                                                }


                                                hideDialog();
                                                Snackbar.make(coordinatorLayout, "Record has been deleted", Snackbar.LENGTH_SHORT).show();
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




        //   new GetAllMessageDetailsFromDevice().execute();


        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String carrierName = manager.getNetworkOperatorName();


            Log.d(TAG, "Sim Operators  :" + carrierName + manager.getSimOperator() + manager.getSimOperatorName());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    //OnCreate Completed



    private void FillDataOnRecyclerView() {

        String query = "Select * from " + dbhandler.TABLE_SENDERID_MASTER + " where "+ dbhandler.SMS_SENDERID_CATEGORYID +"="+ userDetails.get(SessionManager.KEY_MESSAGE_GROUPID) +"";
        Log.d(TAG, "Query Select all senderid's : " + query);

        Cursor cur = sd.rawQuery(query, null);
        Log.d(TAG, "Total " + cur.getCount() + " Records available");


        if (cur.getCount() > 0) {
            txtnodata.setVisibility(View.GONE);
            list_smsgroup.clear();
            while (cur.moveToNext()) {

                SMSGroupData sms = new SMSGroupData(cur.getString(cur.getColumnIndex(dbhandler.SMS_SENDERID_NAME)), cur.getInt(cur.getColumnIndex(dbhandler.SMS_SENDERID_ID)), 0);
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
                    SMSGroupActivity.class);

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
                SMSGroupActivity.class);
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
        private AddSenderIdForMessageLogActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AddSenderIdForMessageLogActivity.ClickListener clickListener) {
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
        if (requestCode == Utility.MY_PERMISSIONS_REQUEST_WRTE_EXTERNAL_STORAGE) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted
                db.backupDB(context);
            } else {
                //Denied permission
                Toast.makeText(context, "Write External Storage Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }


        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }




}


