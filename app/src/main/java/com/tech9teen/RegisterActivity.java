package com.tech9teen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;


public class RegisterActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtMobile;
    private Button btnCreateAccount;
    private EditText edtPassword;
    private CheckBox chkPassword;
    private TextView txvTerms;
    private String TAG = RegisterActivity.class.getSimpleName();
    private CoordinatorLayout coordinatorLayout;
    private Context context = this;
    private SpotsDialog spotDialog;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private TextInputLayout input_edtName, input_edtEmail, input_edtMobile, input_edtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(true);


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //setTitle("Registration");

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();


        input_edtName = (TextInputLayout) findViewById(R.id.input_layout_edtName);
        input_edtMobile = (TextInputLayout) findViewById(R.id.input_layout_edtMobile);
        input_edtEmail = (TextInputLayout) findViewById(R.id.input_layout_edtEmail);
        input_edtPassword = (TextInputLayout) findViewById(R.id.input_layout_edtPassword);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        edtName = (EditText) findViewById(R.id.edtName);
        edtMobile = (EditText) findViewById(R.id.edtMobile);

        chkPassword = (CheckBox) findViewById(R.id.chkPassword);

        txvTerms = (TextView) findViewById(R.id.txtTerms);

        txvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(), "See Tems and Conditions", Toast.LENGTH_SHORT).show();





                sessionmanager.setActivityname("RegisterActivity");

                Intent intent = new Intent(context, WebviewActivity.class);
                intent.putExtra("TITLE",""+txvTerms.getText().toString());
                intent.putExtra("URL", AllKeys.URL_TermsAndConditions);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);


            }
        });

        chkPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Log.d(TAG, " Checked");
                } else {
                    Log.d(TAG, "Unchecked");

                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isErrorFlag = false;


                if (edtName.getText().toString().equals("")) {

                    isErrorFlag = true;
                    input_edtName.setErrorEnabled(true);
                    input_edtName.setError("Please enter Name");
                } else {

                    input_edtName.setErrorEnabled(false);
                }

                if (edtEmail.getText().toString().equals("")) {

                    isErrorFlag = true;
                    input_edtEmail.setErrorEnabled(true);
                    input_edtEmail.setError("Please enter Email");

                } else {

                    if (dbhandler.isValidEmail(edtEmail)) {

                        input_edtEmail.setErrorEnabled(false);

                    } else {
                        isErrorFlag = true;
                        input_edtEmail.setErrorEnabled(true);
                        input_edtEmail.setError("Invalid Email");
                    }

                }

                if (edtMobile.getText().toString().equals("")) {

                    isErrorFlag = true;
                    input_edtMobile.setErrorEnabled(true);
                    input_edtMobile.setError("Please enter Mobile No.");
                } else {

                    Log.d(TAG, " Mobile No Length : " + edtMobile.getText().toString().length());
                    if (edtMobile.getText().toString().length() != 10) {
                        isErrorFlag = true;
                        input_edtMobile.setErrorEnabled(true);
                        input_edtMobile.setError("Invalid Mobile no.");

                    } else {
                        input_edtMobile.setErrorEnabled(false);

                    }

                }

                if (edtPassword.getText().toString().equals("")) {

                    isErrorFlag = true;
                    input_edtPassword.setErrorEnabled(true);
                    input_edtPassword.setError("Please enter Password");
                } else {

                    input_edtPassword.setErrorEnabled(false);
                }


                if (isErrorFlag == false) {


                    //Toast.makeText(getApplicationContext(), " success Login ", Toast.LENGTH_SHORT).show();


                    if (NetConnectivity.isOnline(context)) {

                        SendNewRegistartionDetailsToServer();
                    } else {
                        checkInternet();
                    }


                }


            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void SendNewRegistartionDetailsToServer() {

        showDialog();

        String url_sendNewReg = AllKeys.TAG_WEBSITE_HAPPY + "/SignupCall?action=signup&email=" + dbhandler.convertEncodedString(edtEmail.getText().toString()) + "&device_type=" + AllKeys.DEVICETYPE + "&device_id=" + Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID) + "&name=" + dbhandler.convertEncodedString(edtName.getText().toString()) + "&logintype=NORMAL&mobile=" + edtMobile.getText().toString() + "&password=" + dbhandler.convertEncodedString(edtPassword.getText().toString()) + "";

        Log.d(TAG, " URL Send USer DAta : " + url_sendNewReg);

        StringRequest str_senduserdata = new StringRequest(Request.Method.GET, url_sendNewReg, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "User Response : " + response.toString());


                if (response.toString().contains("UserId")) {







                        JSONArray arr = null;

                        try {
                            response = dbhandler.convertToJsonFormat(response);


                            JSONObject obj = new JSONObject(response);
                            arr = obj.getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject c = (JSONObject) arr.get(i);


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



                                    Intent in = new Intent(context, NewVerificationActivity.class);
                                    startActivity(in);
                                    finish();
                                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);



                                //sessionmanager.setLoginType("gmail");


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Sorry, can’t login. Try again", Toast.LENGTH_SHORT).show();
                        }


                    /*
                    Intent ii = new Intent(context, DashBoardActivity.class);
                    startActivity(ii);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*/





                } else if (response.toLowerCase().contains("mobile")) {
                    input_edtMobile.setErrorEnabled(true);
                    input_edtMobile.setError("" + response.toString());


                } else if (response.toLowerCase().contains("email")) {
                    input_edtEmail.setErrorEnabled(true);
                    input_edtEmail.setError("" + response.toString());


                }

                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in  SendUSerData : " + error.getMessage());


            }
        });

        MyApplication.getInstance().addToRequestQueue(str_senduserdata);


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Register Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public void showDialog() {


        if (!spotDialog.isShowing()) {
            spotDialog.show();
        }
    }

    public void hideDialog() {
        if (spotDialog.isShowing()) {

            spotDialog.cancel();
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
                            Intent intent = new Intent(context, RegisterActivity.class);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {

            Intent ii = new Intent(context, EasyIntroActivity.class);
            startActivity(ii);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


        }
        return true;


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent ii = new Intent(context, EasyIntroActivity.class);
        startActivity(ii);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


    }


}

