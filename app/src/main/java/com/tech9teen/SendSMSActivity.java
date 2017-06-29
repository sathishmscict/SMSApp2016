package com.tech9teen;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.gsm.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.R;
import com.tech9teen.adapter.CheckBoxGroupAdapter;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.ContactsData;
import com.tech9teen.pojo.GroupMaster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class SendSMSActivity extends AppCompatActivity {


    private ArrayList<HashMap<String, String>> all_contacts = new ArrayList<HashMap<String, String>>();
    private ArrayList<ContactsData> listContacts = new ArrayList<ContactsData>();
    private ArrayList<Integer> selected_contacts = new ArrayList<Integer>();

    private ArrayList<String> selectedContatsMobileNumbers = new ArrayList<String>();

    private ArrayList<String> selectedContatsDetails = new ArrayList<String>();


    SmsManager smsManager = SmsManager.getDefault();


    private static final int FILE_SELECT_CODE = 0;


    private InputMethodManager imm;
    private SessionManager sessionmanager;
    private Context context = this;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private CoordinatorLayout coordinateLayout;
    private dbhandler db;
    private SQLiteDatabase sd;
    private EditText txtMessage;
    private Spinner spnTemplate, spnGroup;
    private RadioGroup RdGrpSMStype, RdGrpRouteType;
    private Button btnSendSms;
    private EditText txtMobile;
    private TextInputLayout input_edtMessage, input_edtMobile;
    private String TAG = SendSMSActivity.class.getSimpleName();
    private ArrayList<Integer> list_templateId = new ArrayList<Integer>();
    private ArrayList<String> list_templatesTitle = new ArrayList<String>();
    private ArrayList<String> list_templatesDescr = new ArrayList<String>();

    private ArrayList<Integer> list_groupId = new ArrayList<Integer>();
    private ArrayList<String> list_groupName = new ArrayList<String>();

    ArrayList<Integer> selectedGroups = new ArrayList<Integer>();


    private LinearLayout llMultiple;
    private CardView llSingle;
    private TextView txtGroup;

    private ArrayList<GroupMaster> listGroups = new ArrayList<GroupMaster>();
    private CheckBoxGroupAdapter adapter_group;
    private Button btnImportCSV;
    //private  Button btnScheduleSms;
    private String MESSAGETYPE = "";
    private TextView txtError;
    private LinearLayout llSenderId;
    private Spinner spnSenderId;

    private ArrayList<String> list_senderid_route3 = new ArrayList<String>();

    private ArrayList<String> list_senderid_route2 = new ArrayList<String>();
    private SpotsDialog spotDialog;
    private TextView txtBalance;
    private CheckBox chkScheduleSMS;
    private LinearLayout llScheduleSMS;
    private TextInputLayout input_layout_edtdate, input_layout_edttime;
    private EditText txtDate, txtTime;

    private int y, m, d;
    private int spm;
    private String startDate, showsdate;
    private int hour, minute;


    Calendar calendar = Calendar.getInstance();

    private static final int DATE_PICKER_ID = 123;
    private static final int TIME_DIALOG_ID = 124;
    private String ROUTE = "route1";
    private LinearLayout llRequestSenderId;
    private TextView txtBlickRequestSenderIcon;
    private int MY_PERMISSIONS_REQUEST_SEND_MESSAGES = 121;
    private ImageView imgPicContact;
    private ContactsAdapterRecyclerView contacts_adapter;
    private RecyclerView mRecyclerViewContacts;
    private EditText txtSearch;
    private CheckBox chkSelectall;
    private TextView txtseleted;
    private CardView crdMessageTyepSingleMultiple;
    /*private LinearLayout llSelectContacts;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tech9teen.R.layout.activity_send_sms);


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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


        setTitle("Send SMS");


        txtError = (TextView) findViewById(R.id.txtError);

        txtMessage = (EditText) findViewById(R.id.edtmessage);
        txtMobile = (EditText) findViewById(R.id.edtMobile);
        // txtMobile.setText("9723613143,7802819295,8141624468");
        spnTemplate = (Spinner) findViewById(R.id.spnTemplate);
        spnGroup = (Spinner) findViewById(R.id.spnGroup);
        RdGrpSMStype = (RadioGroup) findViewById(R.id.rdGrpMessage);
        RdGrpRouteType = (RadioGroup) findViewById(R.id.rdGrpRoute);
        txtBalance = (TextView) findViewById(R.id.txtBalance);


        llSenderId = (LinearLayout) findViewById(R.id.llsenderid);
        llRequestSenderId = (LinearLayout) findViewById(R.id.llreuestsenderid);


        //CardView Declarration
        crdMessageTyepSingleMultiple = (CardView) findViewById(R.id.crdMessageTyepSingleMultiple);
        crdMessageTyepSingleMultiple.setVisibility(View.GONE);


        llRequestSenderId.setOnClickListener(new View.OnClickListener() {
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


                Spinner spnSenderidType = (Spinner) dialog.findViewById(R.id.spnSenderIdRequest);


                ArrayList<String> list_typesOfSenderIds = new ArrayList<String>();

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


                        if (edtSenderid.getText().toString().equals("")) {
                            input_edtsenderid.setErrorEnabled(true);
                            input_edtsenderid.setError("Please enter sender id");

                        } else {

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
                dialog.show();

            }
        });

        txtBlickRequestSenderIcon = (TextView) findViewById(R.id.iconSenderBlinkIcon);


        blink_Animation();


        spnSenderId = (Spinner) findViewById(R.id.spnSenderId);

        llSenderId.setVisibility(View.GONE);

        input_edtMessage = (TextInputLayout) findViewById(R.id.input_layout_edtmessage);
        input_edtMobile = (TextInputLayout) findViewById(R.id.input_layout_edtMobile);

        input_layout_edtdate = (TextInputLayout) findViewById(R.id.input_layout_edtdate);
        input_layout_edttime = (TextInputLayout) findViewById(R.id.input_layout_edttime);

        txtDate = (EditText) findViewById(R.id.edtdate);
        txtTime = (EditText) findViewById(R.id.edttime);


        btnSendSms = (Button) findViewById(R.id.btnSendSMS);
        /*btnScheduleSms  =(Button)findViewById(R.id.btnSchedule);*/
        chkScheduleSMS = (CheckBox) findViewById(R.id.chkScheduleSMS);
        btnImportCSV = (Button) findViewById(R.id.btnImport);

        llScheduleSMS = (LinearLayout) findViewById(R.id.llScheduleSMS);

        llScheduleSMS.setVisibility(View.GONE);

        chkScheduleSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {

                    llScheduleSMS.setVisibility(View.VISIBLE);

                } else {
                    llScheduleSMS.setVisibility(View.GONE);

                }
            }
        });


        final Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);


        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.DAY_OF_MONTH, d);

        String dd = null;
        if (d <= 9) {
            dd = "0" + d;
        } else {
            dd = "" + d;
        }

        spm = m + 1;
        if (spm <= 9) {
            String mm = "0" + spm;
            startDate = y + "-" + mm + "-" + d;
            showsdate = y + "-" + mm + "-" + d;

            showsdate = dd + "-" + mm + "-" + y;

            if (y == cal.get(Calendar.YEAR)) {
                //y = 1993;
            }

        } else {
            startDate = y + "-" + spm + "-" + d;
            showsdate = y + "-" + spm + "-" + d;

            showsdate = dd + "-" + spm + "-" + y;

            if (y == cal.get(Calendar.YEAR)) {
                //y = 1993;
            }
        }
        txtDate.setText(showsdate);

        txtDate.setClickable(false);
        txtDate.setClickable(false);


        /********* display current time on screen Start ********/

        final Calendar c = Calendar.getInstance();
        // Current Hour
        hour = c.get(Calendar.HOUR_OF_DAY);
        // Current Minute
        minute = c.get(Calendar.MINUTE);

        // set current time into output textview
        updateTime(hour, minute);

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialog(TIME_DIALOG_ID);


            }
        });


        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);

            }
        });


        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d(TAG, "Selected Groups :" + selectedGroups.size());


                boolean errorFlag = false;

                if (txtMessage.getText().toString().equals("")) {

                    input_edtMessage.setErrorEnabled(true);
                    input_edtMessage.setError("Please Enter Message ");

                    errorFlag = true;

                } else {
                    input_edtMessage.setErrorEnabled(false);
                }


                if (MESSAGETYPE.equals("single")) {
                    if (txtMobile.getText().toString().equals("")) {
                        errorFlag = true;

                        input_edtMobile.setErrorEnabled(true);
                        input_edtMobile.setError("Please enter mobile number");

                    } else {
                        input_edtMobile.setErrorEnabled(false);
                    }

                } else if (MESSAGETYPE.equals("multiple")) {
                    // txtMobile.setText("");

/*                    if(spnGroup.getSelectedItemPosition() == 0)
                    {
                        errorFalg =true;


                        txtError.setText("Please select group");

                        txtError.setVisibility(View.VISIBLE);


                    }
                    else
                    {
                        txtError.setVisibility(View.GONE);

                    }*/

                    List<GroupMaster> groupList = ((CheckBoxGroupAdapter) adapter_group)
                            .getGroupList();
                    ArrayList<Integer> selectedGrps = new ArrayList<Integer>();

                    /**
                     * Check Groups has been selected or not , if groups selected then procced for next action
                     */

                    for (int i = 0; i < groupList.size(); i++) {

                        if (groupList.get(i).isSeleted() == true) {


                            Log.d(TAG, "Selected : " + groupList.get(i).getGroupid() + "  " + groupList.get(i).getGroupname() + "  " + groupList.get(i).getGroup_contacts());
                            selectedGrps.add(0);

                        }
                    }

                    Log.d(TAG, "Selected  contacts : " + selectedGrps.size());

                    //Check Contacts seleted or not
                    if (selectedGrps.size() == 0) {
                        errorFlag = true;
                    }
                    selectedGrps.clear();

                }//Complete Check Message Type


                if (chkScheduleSMS.isChecked() == true) {

                    if (txtDate.getText().toString().equals("")) {

                        errorFlag = true;
                        input_layout_edtdate.setErrorEnabled(true);
                        input_layout_edtdate.setError("Please enter date");

                    } else {

                        input_layout_edtdate.setErrorEnabled(false);

                    }

                    if (txtTime.getText().toString().equals("")) {

                        errorFlag = true;
                        input_layout_edttime.setErrorEnabled(true);
                        input_layout_edttime.setError("Please enter time");

                    } else {

                        input_layout_edttime.setErrorEnabled(false);

                    }


                }


                if (errorFlag == false) {


                    if (ROUTE.toLowerCase().equals("route1")) {


                        /**
                         * Get all contact derails from device
                         */
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(SendSMSActivity.this, new String[]{android.Manifest.permission.SEND_SMS},
                                    MY_PERMISSIONS_REQUEST_SEND_MESSAGES);

                        } else {

                            SendingMessageUsingGSM();
                        }


                    }//Send sms using gsm from route1
                    else {
                        /**
                         * Send SMS Using SMS Gateway route2 or route3
                         */


                        if (NetConnectivity.isOnline(context)) {

                            showDialog();


                            final String url_sendsms = AllKeys.TAG_WEBSITE_HAPPY + "/SendSms";
                            Log.d(TAG, "URL Send SMS  " + url_sendsms);

                            StringRequest str_sendsms = new StringRequest(Request.Method.POST, url_sendsms, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.d(TAG, "Send SMS Response : " + response.toString());

                                    Snackbar.make(coordinateLayout, "" + response.toString(), Snackbar.LENGTH_LONG).show();

                                    hideDialog();


                                    FetchSMSBalanceFromserver();

                                    ClearDetails();


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {


                                    Snackbar.make(coordinateLayout, "Sorry,Try again....", Snackbar.LENGTH_LONG).show();


                                    Log.d(TAG, "Error Send SMS Response : " + error.getMessage());


                                    NetworkResponse networkResponse = error.networkResponse;
                                    if (networkResponse != null) {
                                        Log.d("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                                    }

                                    if (error instanceof TimeoutError) {
                                        Log.d("Volley", "TimeoutError");
                                    } else if (error instanceof NoConnectionError) {
                                        Log.d("Volley", "NoConnectionError");
                                    } else if (error instanceof AuthFailureError) {
                                        Log.d("Volley", "AuthFailureError");
                                    } else if (error instanceof ServerError) {
                                        Log.d("Volley", "ServerError");
                                    } else if (error instanceof NetworkError) {
                                        Log.d("Volley", "NetworkError");
                                    } else if (error instanceof ParseError) {
                                        Log.d("Volley", "ParseError");
                                    }


                                    hideDialog();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {

                                    Map<String, String> params = new HashMap<String, String>();

                                    params.put("action", "sendsms");


                                    if (chkScheduleSMS.isChecked() == true) {
                                        params.put("type", "schedule");
                                    } else {

                                        params.put("type", "regular");
                                    }


                                    params.put("userid", userDetails.get(SessionManager.KEY_USERID));


                                    if (ROUTE.toLowerCase().equals("route2")) {
                                        params.put("senderid", list_senderid_route2.get(spnSenderId.getSelectedItemPosition()));

                                    } else if (ROUTE.toLowerCase().equals("route3")) {
                                        params.put("senderid", list_senderid_route3.get(spnSenderId.getSelectedItemPosition()));
                                    }


                                    if (MESSAGETYPE.equals("single")) {
                                        params.put("groupid", "0");
                                        String mo = txtMobile.getText().toString();
                                        mo = mo.replace("\n", "");
                                        params.put("mobileno", mo);
                                    } else {
                                        params.put("mobileno", "");
                                    /*if(selectedGroups.size() == 0 )
                                    {
                                        params.put("groupid","0");
                                    }
                                    else
                                    {

                                        params.put("groupid",""+selectedGroups.toString());
                                    }*/

                                        String listString = selectedGroups.toString();
                                        listString = listString.replace("[", "");
                                        listString = listString.replace("]", "");
                                        listString = listString.replace(" ", "");

                                        Log.d(TAG, "Selcetd Groups Size: " + selectedGroups.size());
                                        Log.d(TAG, "Selcetd Groups Are : " + selectedGroups.toString());

                                        params.put("groupid", listString);


                                    }


                                    if (spnTemplate.getSelectedItemPosition() == 0) {
                                        params.put("message", dbhandler.convertEncodedString(txtMessage.getText().toString()));
                                        params.put("templateid", "0");
                                    } else {
                                        params.put("message", "");

                                        params.put("templateid", "" + list_templateId.get(spnTemplate.getSelectedItemPosition()));

                                    }

                                    params.put("messagetype", "" + MESSAGETYPE);

                                    params.put("routetype", ROUTE);

                                    params.put("date", "" + dbhandler.convertToJsonDateFormat(txtDate.getText().toString()));
                                    params.put("time", "" + txtTime.getText().toString());


                                    Log.d(TAG, "Send SMS Response ; " + url_sendsms + params.toString());

                                    return params;
                                }
                            };

                            MyApplication.getInstance().addToRequestQueue(str_sendsms);

                        } else {

                            checkInternet();

                        }

                        /**
                         * Complete Sending SMS Via route2 or route3
                         */

                    }
                    //Complete Sending sms via route2 and route3


                }


            }
        });

      /*  btnScheduleSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar.make(coordinateLayout , "Schedule SMS" , Snackbar.LENGTH_SHORT).show();
            }
        });*/
        btnImportCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar.make(coordinateLayout, "Import From CSV", Snackbar.LENGTH_SHORT).show();


                final Dialog dialog = new Dialog(context);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                /*dialog.setTitle("Request SenderId");*/
                dialog.setContentView(R.layout.dialog_import_data_from_csv);


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


                Button btnImport = (Button) dialog.findViewById(R.id.btnImport);
                Button btnCancell = (Button) dialog.findViewById(R.id.btnCancel);

                btnImport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                        dialog.dismiss();

                        try {

                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);

                            try {
                                startActivityForResult(
                                        Intent.createChooser(intent, "Select a File to Upload"),
                                        FILE_SELECT_CODE);
                            } catch (android.content.ActivityNotFoundException ex) {
                                // Potentially direct the user to the Market with a Dialog
                                Toast.makeText(SendSMSActivity.this, "Please install a File Manager.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });


                btnCancell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                        dialog.dismiss();

                        selectedContatsDetails.clear();
                        selectedContatsMobileNumbers.clear();
                        selected_contacts.clear();
                        txtMobile.setText("");

                    }
                });


                dialog.getWindow().setLayout(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);

                //AlertDialog dialog2 = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;


                dialog.show();


            }
        });

        llSingle = (CardView) findViewById(R.id.llsingle);
        llMultiple = (LinearLayout) findViewById(R.id.llmultiple);

        txtGroup = (TextView) findViewById(R.id.txtGroup);

        imgPicContact = (ImageView) findViewById(R.id.imgPicContact);

        /*llSelectContacts  =(LinearLayout)findViewById(R.id.llSelectContacts);

        llSelectContacts.setVisibility(View.GONE);*/

        imgPicContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//Layout Set From Content Layout of current Activity
               /* llSelectContacts.setVisibility(View.VISIBLE);

                chkSelectall = (CheckBox)findViewById(R.id.chkSelectall);
                txtseleted = (TextView)findViewById(R.id.txtseleted);
                txtSearch  =(EditText)findViewById(R.id.txtSearch);
                mRecyclerViewContacts = (RecyclerView)findViewById(R.id.recycler_view_contacts);

                LinearLayoutManager mlayoutManager = new LinearLayoutManager(context);
                mRecyclerViewContacts.setLayoutManager(mlayoutManager);
                mRecyclerViewContacts.setHasFixedSize(true);
                mRecyclerViewContacts.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
                mRecyclerViewContacts.setItemAnimator(new DefaultItemAnimator());


                //Fetch contacts from device

                GetAllContactsFromDevice();




                txtSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                        Log.d(TAG ,  " Search String  : "+s.toString());
                        if(!s.toString().equals("") && !s.equals(null))
                        {

                            try {
                                contacts_adapter.filter(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }






                    }
                });
                */
/**
 *
 * Complet setting layout from current Activity
 */


                /**
                 * Set Layout From Dialog
                 */


                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_select_contacts);


                dialog.setCancelable(false);


                chkSelectall = (CheckBox) dialog.findViewById(R.id.chkSelectall);
                chkSelectall.setVisibility(View.INVISIBLE);
                txtseleted = (TextView) dialog.findViewById(R.id.txtseleted);

                txtSearch = (EditText) dialog.findViewById(R.id.txtSearch);

                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                Button btnOk = (Button) dialog.findViewById(R.id.btnOk);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                        dialog.dismiss();
                    }
                });

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                        dialog.dismiss();

                    }
                });

                txtSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                        Log.d(TAG, " Search String  : " + s.toString());
                        if (!s.toString().equals("") && !s.equals(null)) {

                            try {
                                contacts_adapter.filter(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }


                    }
                });

                mRecyclerViewContacts = (RecyclerView) dialog.findViewById(R.id.recycler_view_contacts);

                LinearLayoutManager mlayoutManager = new LinearLayoutManager(context);
                mRecyclerViewContacts.setLayoutManager(mlayoutManager);
                mRecyclerViewContacts.setHasFixedSize(true);
                mRecyclerViewContacts.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
                mRecyclerViewContacts.setItemAnimator(new DefaultItemAnimator());


                //Fetch contacts from device

                GetAllContactsFromDevice();


                dialog.getWindow().setLayout(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;


                dialog.show();


                /**
                 * Complete sEt layout from dialog
                 */


            }
        });


        txtGroup.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                try {
                    txtError.setVisibility(View.GONE);
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_groups);
                    dialog.setTitle("Select Group");


                    RecyclerView recyclerview_group = (RecyclerView) dialog.findViewById(R.id.recycler_view_group);
                    Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                    Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
                    final TextView txtError = (TextView) dialog.findViewById(R.id.txtError);


                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            List<GroupMaster> conactsList = ((CheckBoxGroupAdapter) adapter_group)
                                    .getGroupList();


                            /**
                             * Check groups has been selected or not , if groups selected then procced for next action
                             */

                            selectedGroups.clear();

                            String groupnames = "";
                            for (int i = 0; i < conactsList.size(); i++) {

                                if (conactsList.get(i).isSeleted() == true) {


                                    Log.d(TAG, "Selected : " + conactsList.get(i).getGroupname() + "  " + conactsList.get(i).getGroup_contacts());
                                    groupnames = groupnames + conactsList.get(i).getGroupname() + " ( " + conactsList.get(i).getGroup_contacts() + " ) " + "\n";
                                    selectedGroups.add(conactsList.get(i).getGroupid());

                                }
                            }

                            Log.d(TAG, "Selected  contacts : " + selectedGroups.size());

                            if (selectedGroups.size() > 0) {

                                dialog.dismiss();
                                dialog.cancel();
                                txtError.setVisibility(View.GONE);
                                txtGroup.setText(groupnames);

                            } else {
                                txtError.setVisibility(View.VISIBLE);
                                txtError.setText("Please select group");
                            }


                        }
                    });


                /*mRecyclerView_goaltype.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView_goaltype.setLayoutManager(mLayoutManager);*/

                    final LinearLayoutManager mlayoutManager = new LinearLayoutManager(context);
                    recyclerview_group.setLayoutManager(mlayoutManager);
                    recyclerview_group.setHasFixedSize(true);
                    recyclerview_group.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
                    recyclerview_group.setItemAnimator(new DefaultItemAnimator());

                    adapter_group = new CheckBoxGroupAdapter(context, listGroups);
                    recyclerview_group.setAdapter(adapter_group);
                    //FillDataOnRecyclerView();


                    Display display = getWindowManager().getDefaultDisplay();
                    int width = display.getWidth();  // deprecated
                    int height = display.getHeight();
                    height = height / 2;
                    width = width - 50;

                    dialog.getWindow().setLayout(width, height);


                    /*dialog.getWindow().setLayout(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);*/
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        //Fill Templete Data to Sinner
        String select_templates = "select * from " + dbhandler.TABLE_TEMPALTE_MASTER + "";
        Cursor cur_templates = sd.rawQuery(select_templates, null);
        Log.d(TAG, "Total " + cur_templates.getCount() + " Records Found");

        list_templatesDescr.clear();
        list_templateId.clear();
        list_templatesTitle.clear();

        list_templatesTitle.add("Select Template");
        list_templatesDescr.add("");
        list_templateId.add(0);
        if (cur_templates.getCount() > 0) {


            while (cur_templates.moveToNext()) {

                list_templateId.add(cur_templates.getInt(cur_templates.getColumnIndex(dbhandler.TEMPLATE_ID)));
                list_templatesTitle.add(cur_templates.getString(cur_templates.getColumnIndex(dbhandler.TEMPLATE_TITLE)));
                list_templatesDescr.add(cur_templates.getString(cur_templates.getColumnIndex(dbhandler.TEMPLATE_DESCR)));

            }

        }

        ArrayAdapter<String> Templateadapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_dropdown_item,
                list_templatesTitle);
        spnTemplate.setAdapter(Templateadapter);


        spnTemplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {

                    txtMessage.setText(list_templatesDescr.get(position));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Fill Group Data to spinner
        FillGroupDetailsInCustomArrayList();

        RdGrpSMStype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdMultiple) {


                    MESSAGETYPE = "multiple";

                    llMultiple.setVisibility(View.VISIBLE);
                    llSingle.setVisibility(View.GONE);


                } else if (checkedId == R.id.rdSingle) {

                    MESSAGETYPE = "single";
                    llMultiple.setVisibility(View.GONE);
                    llSingle.setVisibility(View.VISIBLE);

                }

            }
        });


        list_senderid_route2.clear();
        list_senderid_route3.clear();
        list_senderid_route2.add("STUDYF");
        list_senderid_route3.add("STUDYF");

        RdGrpRouteType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rdRoute1) {
                    ROUTE = "route1";
                    llSenderId.setVisibility(View.GONE);


                } else if (checkedId == R.id.rdRoute2) {
                    ROUTE = "route2";
                    llSenderId.setVisibility(View.VISIBLE);


                    ArrayAdapter<String> SenderIDadapter = new ArrayAdapter<String>(
                            context, android.R.layout.simple_spinner_dropdown_item,
                            list_senderid_route2);
                    spnSenderId.setAdapter(SenderIDadapter);


                } else if (checkedId == R.id.rdRoute3) {
                    ROUTE = "route3";
                    llSenderId.setVisibility(View.VISIBLE);

                    ArrayAdapter<String> SenderIDadapter = new ArrayAdapter<String>(
                            context, android.R.layout.simple_spinner_dropdown_item,
                            list_senderid_route3);
                    spnSenderId.setAdapter(SenderIDadapter);

                }

            }
        });

        RadioButton rdGSM = (RadioButton) findViewById(R.id.rdRoute2);
        rdGSM.setChecked(true);


        RadioButton rb = (RadioButton) findViewById(R.id.rdSingle);
        rb.setChecked(true);


        if (userDetails.get(SessionManager.KEY_USERTYPE).equals("demo")) {

            RadioButton rd1 = (RadioButton) findViewById(R.id.rdRoute1);
            rd1.setChecked(true);

            RadioButton rd2 = (RadioButton) findViewById(R.id.rdRoute2);
            rd2.setVisibility(View.GONE);

            RadioButton rd3 = (RadioButton) findViewById(R.id.rdRoute3);
            rd3.setText("Branded");


        } else if (userDetails.get(SessionManager.KEY_USERTYPE).equals("general")) {

            RadioButton rd1 = (RadioButton) findViewById(R.id.rdRoute1);
            rd1.setChecked(true);

            RadioButton rd2 = (RadioButton) findViewById(R.id.rdRoute2);
            rd2.setText("Promotional");

            RadioButton rd3 = (RadioButton) findViewById(R.id.rdRoute3);
            rd3.setText("Transactional");


        }


        if (NetConnectivity.isOnline(context)) {
            FetchSMSBalanceFromserver();

        } else {
            checkInternet();
        }


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


    }//onCreate Complted

    private void SendingMessageUsingGSM() {


        showDialog();

        if (MESSAGETYPE.equals("single")) {

            String mo = txtMobile.getText().toString();
            mo = mo.replace("\n", "");

            List<String> myContactaList = new ArrayList<String>(Arrays.asList(mo.split(",")));

            int counter = 0;
            for (int i = 0; i < myContactaList.size(); i++) {

                try {
                    smsManager.sendTextMessage("" + myContactaList.get(i), null, String.valueOf(txtMessage.getText().toString())
                            , null, null);
                    ++counter;
                } catch (Exception e) {
                    --counter;
                    e.printStackTrace();
                    Log.d(TAG, "Error in GSM Sending Message : " + e.getMessage());
                }


            }
            Snackbar.make(coordinateLayout, counter + " messages has been sending successfully.", Snackbar.LENGTH_SHORT).show();


        } else {


            String listString = selectedGroups.toString();
            listString = listString.replace("[", "");
            listString = listString.replace("]", "");
            listString = listString.replace(" ", "");

            Log.d(TAG, "Selcetd Groups Size: " + selectedGroups.size());
            Log.d(TAG, "Selcetd Groups Are : " + selectedGroups.toString());

            /**
             * Fetch all contact details fby groupid
             */
            String query_selectall = "Select * from " + dbhandler.TABLE_GROUPMASTERDET + " where " + dbhandler.GROUPMASTER_DET_GROUPMASTER_ID + " in " + selectedGroups.toString() + "";
            Log.d(TAG, "Query Select Contacts : " + query_selectall);
            Cursor cursor_contacts = sd.rawQuery(query_selectall, null);
            Log.d(TAG, "Total " + cursor_contacts.getCount() + " Records Found  in groupid " + selectedGroups.toString());
            if (cursor_contacts.getCount() > 0) {
                int counter = 0;

                while (cursor_contacts.moveToNext()) {

                    try {

                        smsManager.sendTextMessage("" + cursor_contacts.getString(cursor_contacts.getColumnIndex(dbhandler.GROUPMASTER_DET_MOBILENUMBER)), null, String.valueOf(txtMessage.getText().toString())
                                , null, null);
                        ++counter;
                    } catch (Exception e) {
                        --counter;
                        e.printStackTrace();
                        Log.d(TAG, "Error in GSM Sending Message by GroupId : " + e.getMessage());
                    }


                }
                Snackbar.make(coordinateLayout, counter + " messages has been sending successfully.", Snackbar.LENGTH_SHORT).show();


            }


        }

        hideDialog();
    }

    private void FillGroupDetailsInCustomArrayList() {

        String select_group = "select * from " + dbhandler.TABLE_GROUPMASTER + "";
        Cursor cur_group = sd.rawQuery(select_group, null);
        Log.d(TAG, "Total " + cur_group.getCount() + " Records Found");
        list_groupId.clear();
        list_groupName.clear();

       /* list_groupName.add("Select Group");

        list_groupId.add(0);*/


        if (cur_group.getCount() > 0) {

            list_groupId.clear();
            list_groupName.clear();

            list_groupName.add("Select Group");

            list_groupId.add(0);


            listGroups.clear();
            while (cur_group.moveToNext()) {

                list_groupId.add(cur_group.getInt(cur_group.getColumnIndex(dbhandler.GROUPMASTER_ID)));
                list_groupName.add(cur_group.getString(cur_group.getColumnIndex(dbhandler.GROUPMASTER_NAME)));

                GroupMaster GM = new GroupMaster(cur_group.getString(cur_group.getColumnIndex(dbhandler.GROUPMASTER_NAME)), cur_group.getInt(cur_group.getColumnIndex(dbhandler.GROUPMASTER_ID)), cur_group.getInt(cur_group.getColumnIndex(dbhandler.GROUPMASTER_CONTACTS)), false);

                listGroups.add(GM);


            }

        }

        ArrayAdapter<String> Groupadapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_dropdown_item,
                list_groupName);
        spnGroup.setAdapter(Groupadapter);


    }

    private void ClearDetails() {

        txtMessage.setText("");
        spnTemplate.setSelection(0);
        txtMobile.setText("");
        chkScheduleSMS.setChecked(false);

        RadioButton rb = (RadioButton) findViewById(R.id.rdSingle);
        rb.setChecked(true);

        RadioButton r1 = (RadioButton) findViewById(R.id.rdRoute1);
        r1.setChecked(true);

        FillGroupDetailsInCustomArrayList();

        txtGroup.setText(getString(R.string.tap_to_select));

    }


    private void FetchSMSBalanceFromserver() {

        showDialog();
        String url_getbalance = AllKeys.TAG_WEBSITE_HAPPY + "/GetSMSBalance?action=balance&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL Fetch Balance  :" + url_getbalance);

        StringRequest str_getbalance = new StringRequest(Request.Method.GET, url_getbalance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.d(TAG, "Response Of GEt SMS Balance: " + response.toString());
                txtBalance.setText("" + response.toString());
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
    //OnCtreate Completed


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(getApplicationContext(),
                    MenuActivity.class);

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
                MenuActivity.class);

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
                            Intent intent = new Intent(context, SendSMSActivity.class);
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


    /**
     * Setup Data and Time Pickers Handlers
     */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, y, m, d);
            case TIME_DIALOG_ID:

                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);


        }


        return null;
    }


    /**
     * \
     * Date Dialog Picker
     */
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            y = selectedYear;
            m = selectedMonth;

            d = selectedDay;


            calendar.set(Calendar.MONTH, m);
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.DAY_OF_MONTH, d);


            String dd = null;

            if (d <= 9) {
                dd = "0" + d;
            } else {
                dd = String.valueOf(d);
            }
            // Show selected date

            // String startDate;
            int spm = m + 1;
            if (spm <= 9) {
                String mm = "0" + spm;
                startDate = y + "-" + mm + "-" + d;
                showsdate = y + "-" + mm + "-" + d;

                showsdate = dd + "-" + mm + "-" + y;
            } else {
                startDate = y + "-" + spm + "-" + d;
                showsdate = y + "-" + spm + "-" + d;// d+"-"+spm+"-"+y;

                showsdate = dd + "-" + spm + "-" + y;
            }
            txtDate.setText(showsdate);

        }
    };

//Complete Date Dialog Picer

    /**
     * timer dialog picker
     */

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour = hourOfDay;
            minute = minutes;

            updateTime(hour, minute);

        }

    };

    private static String utilTime(int value) {

        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.SECOND, 0);

        txtTime.setText(hours + ":" + mins);


        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
            calendar.set(Calendar.AM_PM, Calendar.PM);
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
            calendar.set(Calendar.AM_PM, Calendar.AM);
        } else if (hours == 12) {

            timeSet = "PM";
            calendar.set(Calendar.AM_PM, Calendar.PM);
        } else {

            timeSet = "AM";
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        //txtTime.setText(aTime);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    try {
                        // Get the Uri of the selected file
                        Uri uri = data.getData();
                        Log.d(TAG, "File Uri: " + uri.toString());
                        // Get the path
                        String path = dbhandler.getPath(this, uri);
                        Log.d(TAG, "File Path: " + path);
                        String ex = path.substring(path.lastIndexOf("."), path.length());

                        Log.d(TAG, "File Extention : " + ex);


                        if (ex.toLowerCase().equals(".csv")) {

                            GetMobileNumberDetailsFromCsv(path);
                        } else {


                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            alertDialogBuilder.setTitle("Invalid File");
                            alertDialogBuilder.setMessage("Please select csv file");

                            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //Toast.makeText(SendSMSActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                    arg0.cancel();
                                    arg0.dismiss();
                                }
                            });


                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();


                        }

                        // Get the file instance
                        // File file = new File(path);
                        // Initiate the upload
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void GetMobileNumberDetailsFromCsv(String fileName) {

        showDialog();

        try {
            FileReader file = new FileReader(fileName);
            BufferedReader buffer = new BufferedReader(file);
            String line = "";
         /*   String tableName ="TABLE_NAME";
            String columns = "_id, name, dt1, dt2, dt3";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
            String str2 = ");";
*/
            //   db.beginTransaction();
            StringBuilder sb = new StringBuilder();
            while ((line = buffer.readLine()) != null) {

                String[] str = line.split(",");
                if (!str[0].toLowerCase().contains("number") || !str[0].toLowerCase().contains("mobile")) {
                    sb.append("" + str[0] + ",\n");

                }
                /*sb.append(str[1] + "',");
                sb.append(str[2] + "',");
                sb.append(str[3] + "'");
                sb.append(str[4] + "'");*/
                /*sb.append(str2);*/
                //  db.execSQL(sb.toString());
                Log.d(TAG, "CSV Data : " + sb.toString());

            }

            try {

                String data = sb.toString();
                data = sb.substring(0, sb.length() - 1);
                txtMobile.setText(sb.substring(0, sb.lastIndexOf(",")));
            } catch (Exception e) {
                e.printStackTrace();
                hideDialog();
            }
            // db.setTransactionSuccessful();
            //db.endTransaction();
        } catch (IOException e) {
            e.printStackTrace();
            hideDialog();
        }

        hideDialog();

    }


    private void blink_Animation() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 250;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (txtBlickRequestSenderIcon.getVisibility() == View.VISIBLE) {
                            txtBlickRequestSenderIcon.setVisibility(View.INVISIBLE);
                        } else {
                            txtBlickRequestSenderIcon.setVisibility(View.VISIBLE);
                        }
                        blink_Animation();
                    }
                });
            }
        }).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_MESSAGES) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted
                SendingMessageUsingGSM();
            } else {
                //Denied permission
                //Toast.makeText(context, "Sending SMS Permission not granted", Toast.LENGTH_SHORT).show();

                Snackbar.make(coordinateLayout, "Sending SMS Permission not granted", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * Created by Satish Gadde on 14-09-2016.
     */
    public class ContactsAdapterRecyclerView extends RecyclerView.Adapter<SendSMSActivity.ContactsAdapterRecyclerView.MyViewHolder> {


        private final Context context;
        private final ArrayList<ContactsData> listContacts;
        private final LayoutInflater inflater;
        private ArrayList<ContactsData> arraylist = new ArrayList<ContactsData>();

        public ContactsAdapterRecyclerView(Context context, ArrayList<ContactsData> contacts) {

            inflater = LayoutInflater.from(context);
            this.context = context;
            this.listContacts = contacts;
            this.arraylist = new ArrayList<ContactsData>();
            this.arraylist.addAll(contacts);


        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private final CheckBox chkContactName;
            private final TextView txtMobile;


            public MyViewHolder(View itemView) {
                super(itemView);


                chkContactName = (CheckBox) itemView.findViewById(R.id.chkContactName);
                txtMobile = (TextView) itemView.findViewById(R.id.txtMobile);

            }
        }


        @Override
        public SendSMSActivity.ContactsAdapterRecyclerView.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.row_single_contact_cardview, parent, false);
            SendSMSActivity.ContactsAdapterRecyclerView.MyViewHolder viewHolder = new SendSMSActivity.ContactsAdapterRecyclerView.MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SendSMSActivity.ContactsAdapterRecyclerView.MyViewHolder holder, int position) {

            final int pos = position;


            final ContactsData CD = listContacts.get(position);

            holder.chkContactName.setText(CD.getName());
            holder.txtMobile.setText(CD.getMobilenumber());


            holder.chkContactName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    try {
                        CheckBox cb = (CheckBox) buttonView;


                        HashMap<String, String> con_data = new HashMap<String, String>();
                        con_data.put("MOBILENUMBER", CD.getMobilenumber());
                        con_data.put("NAME", CD.getName());
                        con_data.put("CONTACTID", String.valueOf(CD.getContacactid()));
                        con_data.put("SELECTED", String.valueOf(CD.isSeleted()));


                        int d = all_contacts.indexOf(con_data);

                        Log.d(TAG, "Index From listContacts  :" + all_contacts.indexOf(con_data));


                        try {
                            listContacts.get(pos).setSeleted(isChecked);
                            arraylist.get(all_contacts.indexOf(con_data)).setSeleted(isChecked);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //contact.setSeleted(isChecked);


                        //updatePostion();

                        if (isChecked) {
                            Log.d(TAG, "Selected ");

                            if (!selected_contacts.contains(CD.getContacactid())) {
                                selected_contacts.add(CD.getContacactid());

                                Log.d(TAG, "Added in selected_contacts");
                                Log.d(TAG, "Selected Contacts Data: " + selected_contacts.toString());
                                Log.d(TAG, "Contact ID For Added : " + CD.getContacactid());

                                selectedContatsMobileNumbers.add(CD.getMobilenumber() + "\n");
                                selectedContatsDetails.add(CD.getName() + "\n" + CD.getMobilenumber() + ",\n");

                                Log.d(TAG, "Added in selected_contacts");


                                String cData = selectedContatsMobileNumbers.toString();
                                cData = cData.replace("[", "");
                                cData = cData.replace("]", "");
                                txtMobile.setText(cData);


                            } else {
                                Log.d(TAG, "Already Contains selected_contacts");
                            }

                            txtseleted.setText(selected_contacts.size() + " seleted");

                        } else {

                            if (selected_contacts.contains(CD.getContacactid())) {

                                Log.d(TAG, "Selected Contacts Data: " + selected_contacts.toString());
                                Log.d(TAG, "Contact ID For Remove : " + CD.getContacactid());

                                int index = selected_contacts.indexOf(CD.getContacactid());

                                selected_contacts.remove(index);
                                selectedContatsDetails.remove(index);
                                selectedContatsMobileNumbers.remove(index);

                                String cData = selectedContatsMobileNumbers.toString();
                                cData = cData.replace("[", "");
                                cData = cData.replace("]", "");
                                txtMobile.setText(cData);

                                txtseleted.setText(selected_contacts.size() + " seleted");
                                Log.d(TAG, "Remove From Selected Contacts");


                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Checkbox Errorr : " + e.getMessage());
                    }


                }
            });

            holder.chkContactName.setChecked(CD.isSeleted());


        }

        @Override
        public int getItemCount() {
            return listContacts.size();
        }

        // method to access in activity after updating selection
        public List<ContactsData> getContactsList() {

            Log.d(TAG, " ListContacts Size Before: " + listContacts.size());
            Log.d(TAG, "ArrayList Size : " + arraylist.size());

            //this.listContacts = new ArrayList<ContactsData>();
            listContacts.clear();
            this.listContacts.addAll(arraylist);

            Log.d(TAG, " ListContacts Size After: " + listContacts.size());


            return listContacts;
        }


        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            listContacts.clear();
            if (charText.length() == 0) {
                listContacts.addAll(arraylist);
            } else {
                for (ContactsData pr : arraylist) {
                    if (pr.getName().toLowerCase(Locale.getDefault()).contains(charText)) {

                        listContacts.add(pr);
                    }
                }
            }
            notifyDataSetChanged();
        }


    }

    /**
     * Complete ContactsAdapterRecyclerView
     */

    /**
     * Get all contacts from device
     */
    private void GetAllContactsFromDevice() {
        String query_selectall = "Select * from " + dbhandler.TABLE_CONTACTMASTER + "";
        Cursor cur_allcontacts = sd.rawQuery(query_selectall, null);
        if (cur_allcontacts.getCount() > 0) {
            listContacts.clear();


            all_contacts.clear();
            while (cur_allcontacts.moveToNext()) {


                ContactsData CD = new ContactsData(cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_MOBILE_NO)), cur_allcontacts.getString(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_NAME)), cur_allcontacts.getInt(cur_allcontacts.getColumnIndex(dbhandler.CONTACTMASTER_ID)), false);

                listContacts.add(CD);

                HashMap<String, String> con_data = new HashMap<String, String>();
                con_data.put("MOBILENUMBER", CD.getMobilenumber());
                con_data.put("NAME", CD.getName());
                con_data.put("CONTACTID", String.valueOf(CD.getContacactid()));
                con_data.put("SELECTED", "false");

                all_contacts.add(con_data);
                // con_data.clear();


            }

        }

        Log.d(TAG, "Total Contacts M : " + listContacts.size());
        contacts_adapter = new SendSMSActivity.ContactsAdapterRecyclerView(context, listContacts);
        mRecyclerViewContacts.setAdapter(contacts_adapter);
    }
    //Oncreate completed


}
