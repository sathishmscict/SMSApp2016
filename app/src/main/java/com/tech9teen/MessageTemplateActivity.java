package com.tech9teen;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.tech9teen.R;
import com.tech9teen.adapter.MessageTemplateAdapterRecyclerView;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class MessageTemplateActivity extends AppCompatActivity {

    private Button btnSaveData;
    private TextInputLayout input_template_title;
    private TextInputLayout input_template_descr;
    private TextView txt_templatetitle;
    private TextView txt_templatedescr;
    private SessionManager sessionmanager;
    private Context context = this;
    private HashMap<String, String> userDetails = new HashMap<String, String>();

    private String TAG = MessageTemplateActivity.class.getSimpleName();
    private RecyclerView recyclerview_message_templates;
    private dbhandler db;
    private SQLiteDatabase sd;
    private ArrayList<MessageTemplate> listMessageTemplate = new ArrayList<MessageTemplate>();
    private MessageTemplateAdapterRecyclerView adapter;
    private LinearLayout lladddata, llshowdata;
    private Menu menu;
    private MenuItem menu_sync, menu_show;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinateLayout;
    private int RECORD_ID=0;
    private InputMethodManager imm;
    private SpotsDialog spotDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_template);


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);


        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();

        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }



        setTitle("Message Templates");


        btnSaveData = (Button) findViewById(R.id.btnSaveData);
        input_template_title = (TextInputLayout) findViewById(R.id.input_layout_edttemplate);
        input_template_descr = (TextInputLayout) findViewById(R.id.input_layout_templatedescr);
        txt_templatetitle = (TextView) findViewById(R.id.edttemplate);
        txt_templatedescr = (TextView) findViewById(R.id.edttemplatedescr);


        lladddata = (LinearLayout) findViewById(R.id.lladddata);
        llshowdata = (LinearLayout) findViewById(R.id.llshowdata);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                txt_templatedescr.setText("");
                txt_templatetitle.setText("");
                btnSaveData.setText("save data");
                lladddata.setVisibility(View.VISIBLE);
                llshowdata.setVisibility(View.GONE);


                fab.setVisibility(View.GONE);

                menu_show.setVisible(true);
                menu_sync.setVisible(false);

            }
        });


        lladddata.setVisibility(View.GONE);
        llshowdata.setVisibility(View.VISIBLE);


        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean errorFlag = false;
                if (txt_templatetitle.getText().toString().equals("")) {

                    errorFlag = true;
                    input_template_title.setErrorEnabled(true);
                    input_template_title.setError("Please enter template title");

                } else {

                    input_template_title.setErrorEnabled(false);

                }

                if (txt_templatedescr.getText().toString().equals("")) {
                    errorFlag = true;
                    input_template_descr.setErrorEnabled(true);
                    input_template_descr.setError("Please enter template description");

                } else {
                    input_template_descr.setErrorEnabled(false);
                }

                if (errorFlag == false) {


                    if(NetConnectivity.isOnline(context))
                    {

                        showDialog();
                        if (btnSaveData.getText().toString().toLowerCase().equals("save data"))
                        {

                            Log.d(TAG, "Insert Data Called");

                            /**
                             * Get Max Id Of Template Master Table
                             */
                            String query_maxid = "select MAX("+ dbhandler.TEMPLATE_ID +") from " + dbhandler.TABLE_TEMPALTE_MASTER + "";
                            Cursor cur_maxid = sd.rawQuery(query_maxid, null);
                            cur_maxid.moveToFirst();
                            int maxid = cur_maxid.getInt(0);
                            Log.d(TAG, "Before  MAxid : " + maxid);
                            ++maxid;
                            Log.d(TAG, "After  MAxid : " + maxid);

                            cur_maxid.close();


                            String url_sendTempleteDate= AllKeys.TAG_WEBSITE_HAPPY+"/InsertMsgTemplate?action=insertmsgtemplate&userid="+ userDetails.get(SessionManager.KEY_USERID) +"&templateid="+ maxid +"&templatename="+ dbhandler.convertEncodedString(txt_templatetitle.getText().toString()) +"&message="+ dbhandler.convertEncodedString(txt_templatedescr.getText().toString()) +"";
                            Log.d(TAG , "URL Insert Template Data : "+url_sendTempleteDate);

                            final int finalMaxid = maxid;
                            StringRequest str_sendTemplateData= new StringRequest(Request.Method.GET, url_sendTempleteDate, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {


                                    if(response.equals("1"))
                                    {

                                        ContentValues cv = new ContentValues();


                                        cv.put(dbhandler.TEMPLATE_TITLE, txt_templatetitle.getText().toString());
                                        cv.put(dbhandler.TEMPLATE_DESCR, txt_templatedescr.getText().toString());
                                        cv.put(dbhandler.TEMPLATE_ID, finalMaxid);
                                        Log.d(TAG, "Templet Master Data : " + cv.toString());

                                        //sd.insert(dbhandler.TABLE_TEMPALTE_MASTER, null, cv);
                                        dbhandler.InsertData(sd,dbhandler.TABLE_TEMPALTE_MASTER,cv);


                                        Snackbar.make(coordinateLayout, "Template has been added", Snackbar.LENGTH_SHORT).show();

                                        txt_templatedescr.setText("");
                                        txt_templatetitle.setText("");
                                        FillDataOnRecyclerView();




                                    }
                                    else
                                    {
                                        Snackbar.make(coordinateLayout, "Sorry, Template not create.Try Again",Snackbar.LENGTH_SHORT).show();
                                    }


                                    hideDialog();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.d(TAG , "Error in Inserting Template Data : "+error.getMessage());
                                    hideDialog();

                                }
                            });
                            MyApplication.getInstance().addToRequestQueue(str_sendTemplateData);







                        }//close if condition save data
                        else
                        {
                            Log.d(TAG, "Update Data Called");



                            String url_sendTempleteDate= AllKeys.TAG_WEBSITE_HAPPY+"/InsertMsgTemplate?action=insertmsgtemplate&userid="+ userDetails.get(SessionManager.KEY_USERID) +"&templateid="+ RECORD_ID +"&templatename="+ dbhandler.convertEncodedString(txt_templatetitle.getText().toString()) +"&message="+ dbhandler.convertEncodedString(txt_templatedescr.getText().toString()) +"";
                            Log.d(TAG , "URL Update Template Data : "+url_sendTempleteDate);


                            StringRequest str_sendTemplateData= new StringRequest(Request.Method.GET, url_sendTempleteDate, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {


                                    if(response.equals("1"))
                                    {


                                        ContentValues cv = new ContentValues();


                                        cv.put(dbhandler.TEMPLATE_TITLE, txt_templatetitle.getText().toString());
                                        cv.put(dbhandler.TEMPLATE_DESCR, txt_templatedescr.getText().toString());
                                        //cv.put(dbhandler.TEMPLATE_ID, RECORD_ID);
                                        Log.d(TAG, "Templet Master Update Data : " + cv.toString());

                                        //sd.insert(dbhandler.TABLE_TEMPALTE_MASTER, null, cv);
                                        sd.update(dbhandler.TABLE_TEMPALTE_MASTER,cv,dbhandler.TEMPLATE_ID+" =" +RECORD_ID,null);


                                        Snackbar.make(coordinateLayout, "Template has been updated", Snackbar.LENGTH_SHORT).show();



                                        FillDataOnRecyclerView();
                                        txt_templatedescr.setText("");
                                        txt_templatetitle.setText("");


                                        lladddata.setVisibility(View.GONE);
                                        llshowdata.setVisibility(View.VISIBLE);
                                        menu_show.setVisible(false);
                                        menu_sync.setVisible(true);
                                        btnSaveData.setText("save data");
                                        fab.setVisibility(View.VISIBLE);

                                    }
                                    else
                                    {
                                        Snackbar.make(coordinateLayout, "Sorry, Template not updated.Try Again",Snackbar.LENGTH_SHORT).show();
                                    }


                                    hideDialog();

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.d(TAG , "Error in Inserting Template Data : "+error.getMessage());
                                    Snackbar.make(coordinateLayout, "Sorry, Template not updated.Try Again",Snackbar.LENGTH_SHORT).show();

                                    hideDialog();

                                }
                            });
                            MyApplication.getInstance().addToRequestQueue(str_sendTemplateData);










                        }//Close update else condition


                    }//close if condition of chekc net conncetivity
                    else
                    {
                        checkInternet();
                    }



                }

            }
        });


        recyclerview_message_templates = (RecyclerView) findViewById(R.id.recycler_view_message_templates);
        //LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerview_message_templates.setLayoutManager(mLayoutManager);
        recyclerview_message_templates.addItemDecoration(new dbhandler.GridSpacingItemDecoration(2, dbhandler.dpToPx(10, context), true));
        recyclerview_message_templates.setItemAnimator(new DefaultItemAnimator());



        /*recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new AllKeys.GridSpacingItemDecoration(2, AllKeys.dpToPx(10, context), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        */


        adapter = new MessageTemplateAdapterRecyclerView(context, listMessageTemplate);
        recyclerview_message_templates.setAdapter(adapter);


        FillDataOnRecyclerView();


        /**
         * RecyclerView Swiping Gesture
         */
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerview_message_templates,
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

                                    /*listMessageTemplate.remove(position);
                                    adapter.notifyItemRemoved(position);*/

       //                             Toast.makeText(context, "Left Detected", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG , "Left Detected");

                                    /**
                                     * Get Current Template Details edit on Controls
                                     */
                                MessageTemplate MT = listMessageTemplate.get(position);
                                    Log.d(TAG , "Edited Template Data : "+MT.toString());

                                    RECORD_ID = MT.getTemplateId();

                                    txt_templatetitle.setText(MT.getTemplateTitle());
                                    txt_templatedescr.setText(MT.getTemplateDescr());


                                    lladddata.setVisibility(View.VISIBLE);
                                    llshowdata.setVisibility(View.GONE);
                                    menu_show.setVisible(true);
                                    menu_sync.setVisible(false);
                                    btnSaveData.setText("update data");
                                    fab.setVisibility(View.GONE);



                                    /*String check_record = "Select * from "+ dbhandler.TABLE_TEMPALTE_MASTER +" where "+ dbhandler.TEMPLATE_ID +"="+ MT.getTemplateId() +"";
                                    Cursor cur_check_record = sd.rawQuery(check_record , null);
                                    if(cur_check_record.getCount() > 0 )
                                    {
                                        while (cur_check_record.moveToNext())
                                        {
                                            txt_templatetitle.setText(MT.getTemplateTitle());
                                            txt_templatedescr.setText(MT.getTemplateDescr());

                                        }

                                    }*/


                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(final RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {

                                    //Toast.makeText(context, "Right Detected", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG , "Right Detected");

                                    if(NetConnectivity.isOnline(context))
                                    {
                                        showDialog();



                                        String url_deleteTemplate = AllKeys.TAG_WEBSITE_HAPPY+"/DeleteMsgTemplate?action=deletemsgtemplate&userid="+ userDetails.get(SessionManager.KEY_USERID)  +"&templateid="+ listMessageTemplate.get(position).getTemplateId() +"";
                                        Log.d(TAG , "URL Delete Template : "+url_deleteTemplate);
                                        StringRequest str_deleterequest =new StringRequest(Request.Method.GET, url_deleteTemplate, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                Log.d(TAG , "Delete Template Response : "+response.toString());

                                                if(response.equals("1"))
                                                {

                                                    sd.delete(dbhandler.TABLE_TEMPALTE_MASTER,dbhandler.TEMPLATE_ID+" = "+listMessageTemplate.get(position).getTemplateId(),null);
                                                    listMessageTemplate.remove(position);
                                                    adapter.notifyItemRemoved(position);

                                                }
                                                else
                                                {
                                                    Snackbar.make(coordinateLayout,"Sorry, Template has not deleted .Try again...",Snackbar.LENGTH_SHORT).show();

                                                }


                                                hideDialog();
                                                Snackbar.make(coordinateLayout,"Record has been deleted",Snackbar.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Log.d(TAG , "Error in deleting Template : "+error.getMessage());

                                                hideDialog();
                                                Snackbar.make(coordinateLayout,"Sorry, Template has not deleted .Try again...",Snackbar.LENGTH_SHORT).show();

                                            }
                                        });
                                        MyApplication.getInstance().addToRequestQueue(str_deleterequest);



                                    }
                                    else
                                    {
                                        checkInternet();
                                    }










                                    //lladddata.setVisibility(View.GONE);
                                    //llshowdata.setVisibility(View.VISIBLE);
                                    //menu_show.setVisible(true);
                                    //menu_sync.setVisible(false);
                                    //btnSaveData.setText("update data");
                                    //fab.setVisibility(View.GONE);




                                }
                                adapter.notifyDataSetChanged();
                            }
                        });


        recyclerview_message_templates.addOnItemTouchListener(swipeTouchListener);


    }//Oncreate completed

    /**
     * Fill data on recycler view
     */
    private void FillDataOnRecyclerView() {

        String query_selectall_templates = "Select * from " + dbhandler.TABLE_TEMPALTE_MASTER + "";
        Log.d(TAG, "Query Select All Templates : " + query_selectall_templates);
        Cursor cur_selectalldata = sd.rawQuery(query_selectall_templates, null);
        Log.d(TAG, "Total Records :" + cur_selectalldata.getCount() + " " + dbhandler.TABLE_TEMPALTE_MASTER);
        if (cur_selectalldata.getCount() > 0) {
            listMessageTemplate.clear();

            while (cur_selectalldata.moveToNext()) {
                MessageTemplate MT = new MessageTemplate(cur_selectalldata.getInt(cur_selectalldata.getColumnIndex(dbhandler.TEMPLATE_ID)), cur_selectalldata.getString(cur_selectalldata.getColumnIndex(dbhandler.TEMPLATE_TITLE)), cur_selectalldata.getString(cur_selectalldata.getColumnIndex(dbhandler.TEMPLATE_DESCR)));


                listMessageTemplate.add(MT);


            }


            adapter.notifyDataSetChanged();


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

            menu_sync.setVisible(true);
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
            menu_sync.setVisible(true);
            menu_show.setVisible(false);

        } else if (item.getItemId() == R.id.common_sync) {


            //fab.setVisibility(View.VISIBLE);
            lladddata.setVisibility(View.GONE);
            llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(true);
            menu_show.setVisible(false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent intent = new Intent(getApplicationContext(),
                DashBoardActivity.class);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }

    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinateLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, MessageTemplateActivity.class);
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
