package com.tech9teen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.tech9teen.R;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextInputLayout input_edtEmail;
    private TextInputLayout input_edtPassword;
    private EditText edtEmail, edtPassword;
    private Button btnCreateAccount;
    private Context context = this;
    private CoordinatorLayout coordinatorLayout;
    private TextView txtForgot;
    private String TAG = LoginActivity.class.getSimpleName();
    private SpotsDialog spotDialog;

    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private ImageView imgShowPassword;
    private Button btn_facebbok;
    private Button btn_gmail;
    private LoginButton login;

    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;


    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    private CallbackManager callbackManager;
    private String CALLTYPE = "";



    private int request_code;
    private int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(com.tech9teen.R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sessionmanager = new SessionManager(context);


        userDetails = sessionmanager.getUserDetails();


        spotDialog = new SpotsDialog(context);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }

        input_edtEmail = (TextInputLayout) findViewById(R.id.input_layout_edtEmail);
        input_edtPassword = (TextInputLayout) findViewById(R.id.input_layout_edtPassword);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);


        imgShowPassword = (ImageView) findViewById(R.id.imgShowPassword);


        imgShowPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;


            }
        });


        txtForgot = (TextView) findViewById(R.id.txvforgotpass);

        btnCreateAccount = (Button) findViewById(R.id.btnSubmit);

        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isErrorFlag = false;
                if(edtEmail.getText().toString().equals("")) {

                    isErrorFlag = true;
                    input_edtEmail.setErrorEnabled(true);
                    input_edtEmail.setError("Please enter Mobile No. for Further Instuction");
                }else{
                    input_edtEmail.setErrorEnabled(false);
                }
                if (isErrorFlag == false)
                {


                    //Toast.makeText(getApplicationContext(), " success Login ", Toast.LENGTH_SHORT).show();


                    if (NetConnectivity.isOnline(context)) {
                        //SendLoginDetailsToServer();
                       // sessionmanager.forgotpass("48",edtEmail.getText().toString(),"0");
                        /*Intent in = new Intent(context, VerityForgotPassAcitvity.class);
                        startActivity(in);
                        finish();
                        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*/
                        SendmobileDetailsToServer();
                    } else {
                        checkInternet();
                    }


                }
            }
        });


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isErrorFlag = false;


                if (edtEmail.getText().toString().equals("")) {

                    isErrorFlag = true;
                    input_edtEmail.setErrorEnabled(true);
                    input_edtEmail.setError("Please enter Mobile No.");

                } else {



                        input_edtEmail.setErrorEnabled(false);


                        /*isErrorFlag = true;
                        input_edtEmail.setErrorEnabled(true);
                        input_edtEmail.setError("Invalid Email");*/


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
                        SendLoginDetailsToServer();
                    } else {
                        checkInternet();
                    }


                }


            }
        });



        buidNewGoogleApiClient();
        btn_facebbok = (Button) findViewById(R.id.fb);
        btn_gmail = (Button) findViewById(R.id.gmail);
        login = (LoginButton) findViewById(R.id.login_button);





        callbackManager = CallbackManager.Factory.create();

        login.setReadPermissions("public_profile email");


        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();




                   /* Intent in= new Intent(context,MobileNumberActivity.class);
                    sessionmanager.setLoginType("facebook");

                    startActivity(in);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*/

                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });


        btn_facebbok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                spotDialog.setCancelable(false);
                CALLTYPE = "fb";
                if (NetConnectivity.isOnline(context)) {
                    showDialog();
                    login.performClick();

                } else {

                    Toast.makeText(context, getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
                }


            }
        });

        btn_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.GET_ACCOUNTS},
                            MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);

                } else {


                    PerformGmailLogin();


                }


            }
        });



    }
    //OnCreate Compelted

    private void PerformGmailLogin() {


        spotDialog.setCancelable(true);

        CALLTYPE = "gmail";
        if (NetConnectivity.isOnline(context)) {
            gPlusSignIn();
        } else {
            Toast.makeText(context, getString(R.string.no_network2), Toast.LENGTH_SHORT).show();
        }

    }


    private void SendLoginDetailsToServer() {


        showDialog();
        String url_logincheck = AllKeys.TAG_WEBSITE_HAPPY + "/GetUserLogin?action=userlogin&mobile=" + dbhandler.convertEncodedString(edtEmail.getText().toString()) + "&password=" + dbhandler.convertEncodedString(edtPassword.getText().toString()) + "";
        Log.d(TAG, "URL Check Login : " + url_logincheck);


        StringRequest str_userlogin = new StringRequest(Request.Method.GET, url_logincheck, new Response.Listener<String>() {
            public JSONObject obj;
            public JSONArray arr;

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "URL Rsponse : " + response);

                if (response.contains("UserId")) {


                    try {
                        response = dbhandler.convertToJsonFormat(response);


                        obj = new JSONObject(response);
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


                            if (newUser.equals("1")) {

                                Intent in = new Intent(context, MobileNumberActivity.class);
                                startActivity(in);
                                finish();
                                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                            } else {

                                Log.d(TAG , "Mobile Number Length  : "+mobileno.length());
                                if (mobileno.length() == 10) {
                                    Intent in = new Intent(context, NewVerificationActivity.class);
                                    startActivity(in);
                                    finish();
                                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                                } else {

                                    Intent in = new Intent(context, MobileNumberActivity.class);
                                    startActivity(in);
                                    finish();
                                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


                                }


                            }

                            //sessionmanager.setLoginType("gmail");


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Sorry, can’t login. Try again", Toast.LENGTH_SHORT).show();
                    }


                } else {


                    hideDialog();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setTitle("Login Info");
                    alertDialogBuilder.setMessage("" + response.toString());

                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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


                hideDialog();


             }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Error in Login Check  : " + error.getMessage());


                hideDialog();


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_userlogin);


    }

    private void SendmobileDetailsToServer() {


        showDialog();
        String url_logincheck = AllKeys.TAG_WEBSITE_HAPPY + "/GetForgotPassword?action=userlogin&mobile=" + dbhandler.convertEncodedString(edtEmail.getText().toString()) + "";
        Log.d(TAG, "URL Check Login : " + url_logincheck);


        StringRequest str_userlogin = new StringRequest(Request.Method.GET, url_logincheck, new Response.Listener<String>() {
            public JSONObject obj;
            public JSONArray arr;

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "URL Rsponse : " + response);

                if (response.contains("UserId")) {


                    try {
                        response = dbhandler.convertToJsonFormat(response);


                        obj = new JSONObject(response);
                        arr = obj.getJSONArray("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = (JSONObject) arr.get(i);


                            String userid = c.getString(AllKeys.TAG_USERID);

                            String mobileno = c.getString(AllKeys.TAG_MOBILENO);

                            String newUser = c.getString(AllKeys.TAG_NEWUSER);


                            sessionmanager.forgotpass(userid,mobileno,newUser);




                                    Intent in = new Intent(context, VerityForgotPassAcitvity.class);
                                    startActivity(in);
                                    finish();
                                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                            //sessionmanager.setLoginType("gmail");


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Sorry, can’t login. Try again", Toast.LENGTH_SHORT).show();
                    }


                } else {


                    hideDialog();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setTitle("Login Info");
                    alertDialogBuilder.setMessage("" + response.toString());

                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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


                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Error in Login Check  : " + error.getMessage());


                hideDialog();


            }
        });
        MyApplication.getInstance().addToRequestQueue(str_userlogin);


    }

    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, LoginActivity.class);
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


    private void buidNewGoogleApiClient() {

        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();*/

    }


    protected void onStart() {
        super.onStart();
        google_api_client.connect();
    }

    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    protected void onResume() {
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(this, result.getErrorCode(), request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            connection_result = result;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }

    }


    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        getProfileInfo();
        hideDialog();

        SendUserDetailsToServer("GMAIL");


    }

    private void SendUserDetailsToServer(String logintype) {
        //Toast.makeText(getApplicationContext() , "User deails has been sended",Toast.LENGTH_SHORT).show();

        showDialog();

        userDetails = sessionmanager.getUserDetails();

        String url_senduserdata = AllKeys.TAG_WEBSITE_HAPPY + "/SignupCall?action=signup&email=" + userDetails.get(SessionManager.KEY_EMAIL) + "&device_type=" + AllKeys.DEVICETYPE + "&device_id=" + Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID) + "&name=" + dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_USERNAME)) + "&logintype=" + logintype + "&mobile=&password=";
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



        /*StringRequest str_senduserdata = new StringRequest(Request.Method.GET, url_senduserdata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG , "Signp Response : "+response.toString());

                if(response.contains("UserId"))
                {


                    Intent in= new Intent(context,MobileNumberActivity.class);
                    sessionmanager.setLoginType("gmail");
                    startActivity(in);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


                }
                hideDialog();





            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG , "Error in signup : "+error.getMessage());


            }
        });

        MyApplication.getInstance().addToRequestQueue(str_senduserdata);
*/

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        google_api_client.connect();
    }

    private void gPlusSignIn() {


        if (!google_api_client.isConnecting()) {
            showDialog();
            Log.d("user connected", "connected");
            is_signInBtn_clicked = true;
            /*progress_dialog.show();*/
            resolveSignInError();

        }
    }

    private void getProfileInfo() {

        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                setPersonalInfo(currentPerson);
                /*currentPerson.getDisplayName();
                String email=currentPerson.getId();
                Log.d("Emailid",email);*/

            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPersonalInfo(Person currentPerson) {

        try {
            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson2 = Plus.PeopleApi
                        .getCurrentPerson(google_api_client);
                String personName = currentPerson2.getDisplayName();
                String personPhotoUrl = currentPerson2.getImage().getUrl();
                String personGooglePlusProfile = currentPerson2.getUrl();
                String personemail = "" + Plus.AccountApi.getAccountName(google_api_client);
                int genderid = currentPerson.getGender();


                Log.d("Person Name ", personName);
                Log.d("Person Email ", personemail);

                //String userprofile = personPhotoUrl;
                if (personPhotoUrl.contains("")) {
                    personPhotoUrl = personPhotoUrl.replace(" ", "%20");

                }


                try {
                    if (!personPhotoUrl.equals("")) {
                        URL url = new URL(personPhotoUrl);
                        Log.d("Image URL : ", "" + personPhotoUrl);
                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        String enc = dbhandler.getStringImage(image);
                        sessionmanager.setEncodedImage(enc);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                sessionmanager.createsavegmaildetails(personName, personPhotoUrl, personemail);


                // LOGINTYPE = "gmail";

//                new LoginFromSocialWebsite().execute();


                //  session.createRegSession("" + personemail ,"",""+personName ,"gmail");


                //              new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
/*

            String personName = currentPerson.getDisplayName();
            String personPhotoUrl = currentPerson.getImage().getUrl();
            String personEmail = currentPerson.getId();
*/





        /*String email12=currentPerson.getId();*/


            gPlusSignOut();
            gPlusRevokeAccess();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    private void gPlusSignOut() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            google_api_client.disconnect();
            google_api_client.connect();
        }
    }

    private void gPlusRevokeAccess() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            Plus.AccountApi.revokeAccessAndDisconnect(google_api_client)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.d("MainActivity", "User access revoked!");
                            buidNewGoogleApiClient();
                            google_api_client.connect();
                        }

                    });
        }
    }

    /**
     * Facebook Login Reated Code
     */
    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        String name = Html.fromHtml(json.getString("name")).toString();
                        String email = Html.fromHtml(json.getString("email")).toString();

                        String personPhotoUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                        Log.d("profilefb", personPhotoUrl);


                        Log.d("Person Name ", name);
                        Log.d("Person Email ", email);

                        //String userprofile = personPhotoUrl;
                        if (personPhotoUrl.contains("")) {
                            personPhotoUrl = personPhotoUrl.replace(" ", "%20");

                        }


                        try {
                            if (!personPhotoUrl.equals("")) {
                                URL url = new URL(personPhotoUrl);
                                Log.d("Image URL : ", "" + personPhotoUrl);
                                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                                String enc = dbhandler.getStringImage(image);
                                sessionmanager.setEncodedImage(enc);

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        sessionmanager.createsavegmaildetails(name, personPhotoUrl, email);


                        //sessionmanager.setLoginType("facebook");


                       /* details_txt.setText(text);
                        emailname.setText(email);
                        profilelink.setText(prifle);*/
                        //profile.setProfileId(json.getString("id"));
                        //sessionmanager.createsaveFBdetails(name,profile,email);


                        disconnectFromFacebook();
                        deleteFacebookApplication();
                        SendUserDetailsToServer("FB");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture.type(large)");
        //parameters.putString("fields", "id,email,gender,cover,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void disconnectFromFacebook() {

        /*if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }*/

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    void deleteFacebookApplication() {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                boolean isSuccess = false;
                try {
                    isSuccess = response.getJSONObject().getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSuccess && response.getError() == null) {
                    // Application deleted from Facebook account
                }

            }
        }).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (CALLTYPE.equals("fb")) {
            callbackManager.onActivityResult(requestCode, responseCode, data);
        }
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                /*progress_dialog.dismiss();*/

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }

    }

    /**
     * Run Permission handler
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_GET_ACCOUNTS) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted
                PerformGmailLogin();

            } else {
                //Denied permission
                Toast.makeText(context, "Get Access Accounts Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }






}
