package com.tech9teen;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class SettingsActivity extends AppCompatActivity {

    private TextView txtSendRequest;
    private Spinner spnRoute,spnSenderid;
    private Button btnSaveSettings;
    private CoordinatorLayout coordinateLayout;

    public Context context=this;
    private String TAG = SettingsActivity.class.getSimpleName();
    private SpotsDialog spotDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails=new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;


    private ArrayList<String> list_senderid_route3 = new ArrayList<String>();

    private ArrayList<String> list_senderid_route2 = new ArrayList<String>();
    private CheckBox chkReminder;
    private TextView txtTerms,txtPrivacy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();

        db = new dbhandler(context);
        sd = db.getWritableDatabase();
        sd = db.getReadableDatabase();


        txtSendRequest = (TextView)findViewById(R.id.txtSendRequest);
        spnRoute = (Spinner)findViewById(R.id.spnRoute);
        spnSenderid= (Spinner)findViewById(R.id.spnSenderId);


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }








        txtTerms  =(TextView)findViewById(R.id.txtTerms);
        txtPrivacy = (TextView)findViewById(R.id.txtPrivacy);

        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionmanager.setActivityname("SettingsActivity");
                Intent intent = new Intent(context, WebviewActivity.class);
                intent.putExtra("TITLE","Terms & Conditions");
                intent.putExtra("URL", AllKeys.URL_TermsAndConditions);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);



                //Toast.makeText(getApplicationContext() , " Clicked on "+txtTerms.getText().toString() , Toast.LENGTH_SHORT).show();
            }
        });

        txtPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sessionmanager.setActivityname("SettingsActivity");

                Intent intent = new Intent(context, WebviewActivity.class);
                intent.putExtra("TITLE",""+txtPrivacy.getText().toString());
                intent.putExtra("URL",AllKeys.URL_TermsAndConditions);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);

             //   Toast.makeText(context, "Clicked On "+txtPrivacy.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });




        btnSaveSettings  =(Button)findViewById(R.id.btnSaveData);

        chkReminder   = (CheckBox)findViewById(R.id.chkReminder);

        chkReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                //Toast.makeText(getApplicationContext() , "Value :"+String.valueOf(isChecked) , Toast.LENGTH_SHORT).show();
                sessionmanager.setBalanceReminder(String.valueOf(isChecked));


            }
        });

        if(userDetails.get(SessionManager.KEY_BALANCE_REMINDER).equals("true"))
        {

            chkReminder.setChecked(true);
        }
        else
        {
            chkReminder.setChecked(false);
        }





        txtSendRequest.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                /*dialog.setTitle("Request SenderId");*/
                dialog.setContentView(R.layout.dialog_request_senderid);


                try {
                    LinearLayout llmain = (LinearLayout) dialog.findViewById(R.id.llmain);
                    llmain.setVisibility(LinearLayout.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim);
                    animation.setDuration(1000);
                    llmain.setAnimation(animation);
                    llmain.animate();
                    animation.start();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }


                final Spinner spnSenderidType = (Spinner) dialog.findViewById(R.id.spnSenderIdRequest);
                final TextView  txtError = (TextView)dialog.findViewById(R.id.txtError);



                ArrayList<String> list_typesOfSenderIds = new ArrayList<String>();

                list_typesOfSenderIds.add("Tap to Select Route");
                list_typesOfSenderIds.add("Transactional");
                list_typesOfSenderIds.add("Promotional");

                ArrayAdapter<String> Templateadapter = new ArrayAdapter<String>(
                        context, android.R.layout.simple_spinner_dropdown_item,
                        list_typesOfSenderIds);
                spnSenderidType.setAdapter(Templateadapter);


                final EditText edtSenderid = (EditText) dialog.findViewById(R.id.edtsenderid);
                final TextInputLayout input_edtsenderid = (TextInputLayout) dialog.findViewById(R.id.input_layout_edtsenderid);


                final Button btnCancelRequest = (Button) dialog.findViewById(R.id.btnCancelRequest);
                Button btnSendRequest = (Button) dialog.findViewById(R.id.btnSendRequest);

                btnSendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(spnSenderidType.getSelectedItemPosition() == 0)
                        {
                            txtError.setText("Pleas select Route");

                        }
                        else if (edtSenderid.getText().toString().equals("")) {

                            txtError.setText("");
                            input_edtsenderid.setErrorEnabled(true);
                            input_edtsenderid.setError("Please enter sender id");

                        }
                        else if(edtSenderid.getText().toString().length() != 6)
                        {
                            txtError.setText("");
                            input_edtsenderid.setErrorEnabled(true);
                            input_edtsenderid.setError("SenderId Must be 6 Characters");

                        }
                        else {
                            txtError.setText("");

                            input_edtsenderid.setErrorEnabled(false);

                            if (NetConnectivity.isOnline(context)) {
                                showDialog();
                                String url_requestsenderid = AllKeys.TAG_WEBSITE_HAPPY + "/SenderIDRequest?action=senderidrequest&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&senderid=" + edtSenderid.getText().toString() + "";
                                Log.d(TAG, "Url RequestSenderID : " + url_requestsenderid);

                                StringRequest str_requestSenderId = new StringRequest(Request.Method.GET, url_requestsenderid, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d(TAG, "Response Request SenderId  :" + response.toLowerCase());

                                        if (response.equals("1")) {

                                            Snackbar.make(coordinateLayout, "Request has been sended", Snackbar.LENGTH_SHORT).show();


                                            dialog.cancel();
                                            dialog.dismiss();

                                        } else {
                                            Snackbar.make(coordinateLayout, "Sorry,try again...", Snackbar.LENGTH_SHORT).show();


                                        }

                                        hideDialog();

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.d(TAG, "Error Request SendID : " + error.getMessage());

                                        hideDialog();
                                        Snackbar.make(coordinateLayout, "Sorry,try again...", Snackbar.LENGTH_SHORT).show();

                                    }
                                });
                                MyApplication.getInstance().addToRequestQueue(str_requestSenderId);

                            } else {

                                checkInternet();
                            }


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
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                //dialog.show();


                Intent intent = new Intent(context , UploadDocumentsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_left , R.anim.slide_right);



            }
        });


        /**
         * Fill Data Of spinner Route Type
         */
        ArrayList<String> list_typesOfSenderIds = new ArrayList<String>();


        list_typesOfSenderIds.add("Promotional");
        list_typesOfSenderIds.add("Transactional");

        ArrayAdapter<String> Templateadapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_dropdown_item,
                list_typesOfSenderIds);
        spnRoute.setAdapter(Templateadapter);
        spnRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    if(position == 1)
                    {

                        sessionmanager.setDefaultRouteType("route3");//Transactional




                        ArrayAdapter<String> SenderIDadapter = new ArrayAdapter<String>(
                                context, android.R.layout.simple_spinner_dropdown_item,
                                list_senderid_route3);
                        spnSenderid.setAdapter(SenderIDadapter);

                    }
                    else if(position == 0)
                    {
                        //Promotional
                        sessionmanager.setDefaultRouteType("route2");






                        ArrayAdapter<String> SenderIDadapter = new ArrayAdapter<String>(
                                context, android.R.layout.simple_spinner_dropdown_item,
                                list_senderid_route2);
                        spnSenderid.setAdapter(SenderIDadapter);
                    }




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spnSenderid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spnRoute.getSelectedItemPosition() == 1)
                {

                    //Transactional
                    //sessionmanager.setDefaultRouteType("route3");

                    sessionmanager.setDefaultSenderID(list_senderid_route3.get(position) , String.valueOf(position));

                }
                else if(position == 0)
                {
                    //Promotional
                    //sessionmanager.setDefaultRouteType("route2");

                    sessionmanager.setDefaultSenderID(list_senderid_route2.get(position),String.valueOf(position));

                }






            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       /**
         * Fill Data  Of SenderID Details From Database
         */

        /**
         * Fetch SenderID Details From Tables
         */
        String query_selectSenderIds = "select * from " + dbhandler.TABLE_SMSSETTINGS + "";
        Log.d(TAG, "Query Select All SMSSettings  : " + query_selectSenderIds);
        Cursor cur = sd.rawQuery(query_selectSenderIds, null);
        Log.d(TAG, "Total " + cur.getCount() + " Records found in " + dbhandler.TABLE_SMSSETTINGS);
        if (cur.getCount() > 0) {
            list_senderid_route2.clear();
            list_senderid_route3.clear();
            while (cur.moveToNext()) {


                if (cur.getString(cur.getColumnIndex(dbhandler.SMSSETTINGS_TYPE)).toLowerCase().contains("transactional")) {

                    list_senderid_route3.add(cur.getString(cur.getColumnIndex(dbhandler.SMSSETTINGS_SENDERID)));
                } else if (cur.getString(cur.getColumnIndex(dbhandler.SMSSETTINGS_TYPE)).toLowerCase().contains("promotional")) {
                    list_senderid_route2.add(cur.getString(cur.getColumnIndex(dbhandler.SMSSETTINGS_SENDERID)));

                }

            }

        }




        if(userDetails.get(SessionManager.KEY_DEFAULT_ROUTETYPE).toLowerCase().equals("transactional") || userDetails.get(SessionManager.KEY_DEFAULT_ROUTETYPE).toLowerCase().equals("route3"))
        {
            try {
                spnRoute.setSelection(1);
                spnSenderid.setSelection(Integer.parseInt(userDetails.get(SessionManager.KEY_DEFAULT_SENDERID_POS)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(userDetails.get(SessionManager.KEY_DEFAULT_ROUTETYPE).toLowerCase().equals("promotional") || userDetails.get(SessionManager.KEY_DEFAULT_ROUTETYPE).toLowerCase().equals("route2"))
        {

            try {
                spnRoute.setSelection(0);
                spnSenderid.setSelection(Integer.parseInt(userDetails.get(SessionManager.KEY_DEFAULT_SENDERID_POS)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }








        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sessionmanager.setMessageType("");

                Toast.makeText(getApplicationContext() , "Settings has been saved",Toast.LENGTH_SHORT).show();

            }
        });





    }
    //OnCreate Completed


    /**
     * Check Internet Connectivity
     */
    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinateLayout, "" + getString(R.string.no_network2), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, SettingsActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_right, R.anim.slide_left);

                        } else {

                            Snackbar snackbar1 = Snackbar.make(coordinateLayout, "" + getString(R.string.no_network2), Snackbar.LENGTH_SHORT);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {

            Intent intent = new Intent(getApplicationContext(),
                    DashBoardActivity.class);

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
                DashBoardActivity.class);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }




}
