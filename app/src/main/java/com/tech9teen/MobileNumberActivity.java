package com.tech9teen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.R;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

/**
 * Created by LIFE!sG on 23-08-2016.
 */

public class MobileNumberActivity extends AppCompatActivity {
    private Context context = this;
    private Button btnnext;
    private TextView txvcode;
    private Button button_no;
    private EditText txtMobile;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = MobileNumberActivity.class.getSimpleName();
    private CoordinatorLayout coordinateLayout;
    private SpotsDialog spotDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);


        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();


        txvcode = (TextView) findViewById(R.id.txvno);
        txtMobile = (EditText) findViewById(R.id.edtnumber);
        txtMobile.setText(userDetails.get(SessionManager.KEY_USER_MOBILE));
        btnnext = (Button) findViewById(R.id.btnnext);


        boolean chechnumbverpermission = Utility.checkPermission_ReadSMS(context);

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnnext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (txtMobile.getText().toString().length() == 10) {


                    showDialog();

                    //final String url_sendUpdateDetails = AllKeys.TAG_WEBSITE_HAPPY + "/UpdateUserDetails?action=updateuser&userid="+ userDetails.get(SessionManager.KEY_USERID) +"&email="+ userDetails.get(SessionManager.KEY_EMAIL) +"&mobileno="+ txtMobile.getText().toString() +"&name="+ dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_USERNAME)) +"&status="+ userDetails.get(SessionManager.KEY_STATUS) +"&gender=&password=&dob=&doa=";
                    final String url_sendUpdateDetails = AllKeys.TAG_WEBSITE_HAPPY + "/UpdateUserDetails";
                    Log.d(TAG , "URL Update User Details : "+url_sendUpdateDetails);

                    final StringRequest str_sendUpdateDetails = new StringRequest(Request.Method.POST, url_sendUpdateDetails, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            Log.d(TAG, "Update USerData Response : " + response.toString());


                            if (response.equals("1")) {

                                sessionmanager.setUserMobileNumber(txtMobile.getText().toString());
                                Intent in = new Intent(MobileNumberActivity.this, NewVerificationActivity.class);
                                startActivity(in);
                                finish();
                                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                            } else {
                                Snackbar.make(coordinateLayout, "Sorry,Try again", Snackbar.LENGTH_SHORT).show();
                            }


                            hideDialog();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d(TAG, "Error In Update Details :" + error.getMessage());

                            Snackbar.make(coordinateLayout, "Sorry,Try again", Snackbar.LENGTH_SHORT).show();

                            hideDialog();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("action", "updateuser");
                            params.put("userid", userDetails.get(SessionManager.KEY_USERID));
                            params.put("email", userDetails.get(SessionManager.KEY_EMAIL));
                            params.put("mobileno", txtMobile.getText().toString());
                            params.put("name", dbhandler.convertEncodedString(userDetails.get(SessionManager.KEY_USERNAME)));
                            params.put("status", userDetails.get(SessionManager.KEY_STATUS));
                            params.put("gender", "");
                            params.put("password", "");
                            params.put("dob", "");
                            params.put("doa", "");


                            Log.d(TAG , "Update User Data Params : "+url_sendUpdateDetails+params.toString());

                            return params;
                        }
                    };
                    MyApplication.getInstance().addToRequestQueue(str_sendUpdateDetails);


                } else {
                    Toast.makeText(MobileNumberActivity.this, "Number not valid", Toast.LENGTH_SHORT).show();

                    txtMobile.setError("Invalid Mobile Number");
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(context,
                    NewLoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            /*overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*/
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context,
                NewLoginActivity.class);
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

            // spotDialog.cancel();
            spotDialog.dismiss();

        }

    }


}
