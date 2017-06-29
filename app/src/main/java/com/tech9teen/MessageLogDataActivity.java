package com.tech9teen;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.R;
import com.tech9teen.adapter.MessageLogAdapterRecyclerView;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.MessageLogData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class MessageLogDataActivity extends AppCompatActivity {


/*
    ArrayList<String> list_mobilenumbers = new ArrayList<String>();
    ArrayList<String> list_contactname = new ArrayList<String>();
*/

    private Context context = this;
    private CoordinatorLayout coordinatorLayout;
    private SpotsDialog spotDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private RecyclerView recyclerview_sms;
    private dbhandler db;
    private SQLiteDatabase sd;
    private int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 121;
    private String TAG = MessageLogDataActivity.class.getSimpleName();

    private ArrayList<MessageLogData> list_MessageLog = new ArrayList<MessageLogData>();
    private MessageLogAdapterRecyclerView adapter;
    private int RECORD_POS = 0;
    private ArrayList<String> list_senderid = new ArrayList<String>();
    private TextView txtNoData;
    private int senderidID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_log_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        try {
            Intent ii = getIntent();
            senderidID = Integer.parseInt(ii.getStringExtra("SENDERID"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(true);


        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();


        txtNoData = (TextView)findViewById(R.id.txtnodata);
        recyclerview_sms = (RecyclerView) findViewById(R.id.recycler_view_sms);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerview_sms.setLayoutManager(mLayoutManager);
        recyclerview_sms.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
        recyclerview_sms.setItemAnimator(new DefaultItemAnimator());

        adapter = new MessageLogAdapterRecyclerView(context, list_MessageLog);


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


        setTitle("Message History");


        /**
         * Get all contact derails from device
         */
        /*if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MessageLogDataActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CALL_LOG);

        } else {

            new GetMessageLogUsingAsyncTask().execute();





        }*/

        recyclerview_sms.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview_sms, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

              //  Toast.makeText(getApplicationContext(), "clicked" + list_MessageLog.get(position).toString(), Toast.LENGTH_SHORT).show();


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageLogDataActivity.this);
                alertDialogBuilder.setTitle(list_MessageLog.get(position).getTitle());
                alertDialogBuilder.setMessage(list_MessageLog.get(position).getMessage());


                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        arg0.dismiss();
                        arg0.cancel();
                    }
                });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                alertDialog.show();


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                //RECORD_POS =0;
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                /*dialog.setTitle("Request SenderId");*/
                dialog.setContentView(R.layout.dialog_add_senderid);


                try {
                    LinearLayout llmain = (LinearLayout) dialog.findViewById(R.id.llmain);
                    llmain.setVisibility(LinearLayout.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim);
                    animation.setDuration(500);
                    llmain.setAnimation(animation);
                    llmain.animate();
                    animation.start();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }


                TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);


                final EditText edtGroup = (EditText) dialog.findViewById(R.id.edtgroup);

                final TextInputLayout input_edtgroup = (TextInputLayout) dialog.findViewById(R.id.input_layout_edtgroup);
                Button btnSendRequest = (Button) dialog.findViewById(R.id.btnSendRequest);
                final Button btnCancelRequest = (Button) dialog.findViewById(R.id.btnCancelRequest);

                if (RECORD_POS != 0) {

                    edtGroup.setText(list_MessageLog.get(RECORD_POS).getTitle());
                    btnSendRequest.setText("Update Data");


                }


                btnSendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (edtGroup.getText().toString().equals("")) {
                            input_edtgroup.setErrorEnabled(true);
                            input_edtgroup.setError("Please enter senderid");

                        } else {
                            input_edtgroup.setErrorEnabled(false);


                            if (NetConnectivity.isOnline(context)) {
                                showDialog();


                                int maxid;
                                if (RECORD_POS == 0) {

                                    String query_maxid = "select MAX(" + dbhandler.SMS_SENDERID_ID + ") from " + dbhandler.TABLE_SENDERID_MASTER + "";
                                    Log.d(TAG, "Query GetMaxId : " + query_maxid);
                                    Cursor cur = sd.rawQuery(query_maxid, null);
                                    cur.moveToNext();
                                    maxid = cur.getInt(0);
                                    Log.d(TAG, "Before Maxid : " + maxid);
                                    ++maxid;
                                    Log.d(TAG, "After Maxid : " + maxid);


                                } else {
                                    maxid =Integer.parseInt(list_MessageLog.get(RECORD_POS).getId());
                                }


                                String Data = null;
                                try {
                                    JSONObject obj = new JSONObject();
                                    obj.accumulate("SubCategoryId", maxid);
                                    obj.accumulate("CategoryId", userDetails.get(SessionManager.KEY_MESSAGE_GROUPID));
                                    obj.accumulate("SubCategoryName",dbhandler.convertEncodedString(edtGroup.getText().toString()));
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

                                        Log.d(TAG, "Reponse Insert SenderID : " + response.toLowerCase());

                                        if (response.equals("1")) {
                                            /**
                                             * Insert data into smsCategoryTable
                                             */
                                            try {

                                                ContentValues cv = new ContentValues();
                                                cv.put(dbhandler.SMS_SENDERID_ID, finalMaxid);
                                                cv.put(dbhandler.SMS_SENDERID_NAME, edtGroup.getText().toString());
                                                cv.put(dbhandler.SMS_SENDERID_CATEGORYID , userDetails.get(SessionManager.KEY_MESSAGE_GROUPID));
                                                if (RECORD_POS == 0) {
                                                    sd.insert(dbhandler.TABLE_SENDERID_MASTER, null, cv);

                                                    dialog.cancel();
                                                    dialog.dismiss();
                                                    Snackbar.make(coordinatorLayout, "SenderId has been added", Snackbar.LENGTH_LONG);

                                                } else {
                                                    sd.update(dbhandler.TABLE_SENDERID_MASTER, cv, "" + dbhandler.SMS_SENDERID_ID + "=" + list_MessageLog.get(RECORD_POS).getId() + "", null);


                                                    dialog.cancel();
                                                    dialog.dismiss();
                                                    Snackbar.make(coordinatorLayout, "SenderId has been updated", Snackbar.LENGTH_LONG);
                                                    RECORD_POS = 0;

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                if (RECORD_POS == 0) {

                                                    Snackbar.make(coordinatorLayout, "SenderId has not added", Snackbar.LENGTH_LONG);

                                                } else {
                                                    Snackbar.make(coordinatorLayout, "SenderId has not updated", Snackbar.LENGTH_LONG);
                                                }
                                            }


                                        }


                                        hideDialog();

                                        new GetMessageLogUsingAsyncTask().execute();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        hideDialog();
                                        Snackbar.make(coordinatorLayout, "SenderID has not added", Snackbar.LENGTH_LONG);

                                    }
                                });
                                MyApplication.getInstance().addToRequestQueue(str_addgroup);

                            } else {
                                checkInternet();
                            }

                            //Snackbar.make(coordinatorLayout, "Group has been sended", Snackbar.LENGTH_SHORT).show();


                        }

                    }
                });


                btnCancelRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                        dialog.cancel();

                    }

                });


                dialog.getWindow().setLayout(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
//                dialog.show();


                Intent intent = new Intent(getApplicationContext(),
                        AddSenderIdForMessageLogActivity.class);

                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);



            }
        });

        fab.setVisibility(View.GONE);


        /**
         * get Message  Log From MessageLog Teble
         */
        new GetMessageLogUsingAsyncTask().execute();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(getApplicationContext(),
                    AddSenderIdForMessageLogActivity.class);

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
                AddSenderIdForMessageLogActivity.class);

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
                            Intent intent = new Intent(context, MessageLogDataActivity.class);
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


    /**
     * Get Current user contacts from device
     */
    public class GetMessageLogUsingAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
/*
                dbhandler.DeleteTableData(sd, dbhandler.TABLE_CONTACTMASTER);


                Cursor managedCursor = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                Log.d(TAG, "Total " + managedCursor.getCount() + " Contact Records  Found ");
                if (managedCursor.getCount() > 0) {




                    list_mobilenumbers.clear();
                    list_contactname.clear();


                    while (managedCursor.moveToNext()) {

                        list_mobilenumbers.add(String.valueOf(managedCursor.getString(2)));
                        list_contactname.add(String.valueOf(managedCursor.getString(1)));

                    }

                }*/

                String query_getsenderid = "select * from " + dbhandler.TABLE_SENDERID_MASTER + " where " + dbhandler.SMS_SENDERID_CATEGORYID + "=" + userDetails.get(SessionManager.KEY_MESSAGE_GROUPID) + "";
                query_getsenderid = "select * from " + dbhandler.TABLE_SENDERID_MASTER + " where " + dbhandler.SMS_SENDERID_ID + "=" + senderidID + "";
                Log.d(TAG, "Query Select all SenderID's : " + query_getsenderid);
                Cursor cur = sd.rawQuery(query_getsenderid, null);
                Log.d(TAG , "Total "+cur.getCount() +" SenderId's Founs in categoryid = "+userDetails.get(SessionManager.KEY_MESSAGE_GROUPID));
                if (cur.getCount() > 0) {

                    list_senderid.clear();
                    while (cur.moveToNext())
                    {

                        if (cur.getString(cur.getColumnIndex(dbhandler.SMS_SENDERID_NAME)).contains("-")) {
                            String senderid = cur.getString(cur.getColumnIndex(dbhandler.SMS_SENDERID_NAME));
                            Log.d(TAG, "SenderID Before : " + senderid);
                            senderid = senderid.substring(senderid.indexOf("-"), senderid.length());
                            Log.d(TAG, "SenderID After : " + senderid);


                            list_senderid.add(senderid.toLowerCase());

                        } else {

                            list_senderid.add(cur.getString(cur.getColumnIndex(dbhandler.SMS_SENDERID_NAME)).toLowerCase());
                        }



                    }


                }


                String query = "Select * from " + dbhandler.TABLE_MESSAGELOGMASTER + " where " + dbhandler.MESSAGELOG_SENDERID + "=1";

                Cursor c = sd.rawQuery(query, null);
                if (c.getCount() > 0) {
                    list_MessageLog.clear();
                    while (c.moveToNext()) {


                        String dtStart = c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_DATE));//+"2010-10-15T09:27:37Z";
                        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyyy");
                        try {
                            Date date = format.parse(dtStart);

                            DateFormat outputFormat = new SimpleDateFormat("MMM dd");
                            dtStart = outputFormat.format(date);


                            System.out.println(date);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dtStart = dtStart + " " + c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_TIME));

                        String senderid  = c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_NAME));
                        senderid   = senderid.substring(senderid.indexOf("-")+1,senderid.length()).toLowerCase();


                        String allSenderIds= list_senderid.toString();

                        allSenderIds = allSenderIds.replace("[","");
                        allSenderIds = allSenderIds.replace("]","");

                        boolean res  = allSenderIds.toLowerCase().contains(senderid);

                        if(allSenderIds.toLowerCase().contains(senderid))
                        {

                            Log.d(TAG , "list SenderID :"+allSenderIds);
                            Log.d(TAG,  "SenderId : "+senderid.toLowerCase());
                            boolean ress = allSenderIds.contains(senderid.toLowerCase());
                            Log.d(TAG , "Contains : "+ress);
                            MessageLogData msg = new MessageLogData(c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_BODY)), c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_NAME)), dtStart, c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_ID)));
                            list_MessageLog.add(msg);


                        }





                        /*if(allSenderIds.contains(senderid.toLowerCase()))
                        {

                            MessageLogData msg = new MessageLogData(c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_BODY)), c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_NAME)), dtStart, c.getString(c.getColumnIndex(dbhandler.MESSAGELOG_ID)));
                            list_MessageLog.add(msg);

                        }*/






                    }


                }

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideDialog();


            if(list_MessageLog.size() == 0 )
            {
                txtNoData.setVisibility(View.VISIBLE);
            }
            else
            {
                txtNoData.setVisibility(View.GONE);
            }
            recyclerview_sms.setAdapter(adapter);


//            getContactDetailsAndSendtoServer();


        }
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MessageLogDataActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MessageLogDataActivity.ClickListener clickListener) {
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


}
