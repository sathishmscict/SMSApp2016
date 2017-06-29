package com.tech9teen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class ForgotPassActivity extends AppCompatActivity {

    private TextInputLayout input_edtEmail;
    private TextInputLayout input_edtPassword;
    private EditText edtConfirmPassword, edtPassword;
    private Button btnCreateAccount;
    private String Pass,ConfirmPass;
    private SpotsDialog spotDialog;
    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private String TAG = ForgotPassActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        input_edtEmail = (TextInputLayout) findViewById(R.id.input_layout_edtEmail);
        input_edtPassword = (TextInputLayout) findViewById(R.id.input_layout_edtPassword);

        edtPassword = (EditText) findViewById(R.id.edtPassword1);
        edtConfirmPassword = (EditText) findViewById(R.id.edtPassword);

        btnCreateAccount = (Button) findViewById(R.id.btnSubmit);

        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isErrorFlag = false;
                if(edtPassword.getText().toString().equals("")){
                    isErrorFlag = true;
                    input_edtEmail.setErrorEnabled(true);
                    input_edtEmail.setError("Please enter Password.");
                }else if(edtConfirmPassword.getText().toString().equals("")) {
                    isErrorFlag = true;
                    input_edtEmail.setErrorEnabled(true);
                    input_edtEmail.setError("Please enter Confirm Password.");


                }

                if (isErrorFlag == false) {

                    Pass = edtPassword.getText().toString();
                    ConfirmPass = edtConfirmPassword.getText().toString();

                        if (Pass.toString().equals(ConfirmPass.toString()))
                        {
                            /*Toast.makeText(ForgotPassActivity.this, "SuccessFully Change", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ForgotPassActivity.this, EasyIntroActivity.class);
                            // Closing all the Activities
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            // Add new Flag to start new Activity
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(i);
                            finish();*/
                            SendNewRegistartionDetailsToServer();
                        } else {
                            Toast.makeText(ForgotPassActivity.this, "Password not Match", Toast.LENGTH_SHORT).show();
                        }
                }


            }
        });
    }
    //String url_sendNewReg = AllKeys.TAG_WEBSITE_HAPPY + "/UpdatePassword?action=updatepassword&userid=" + edtPassword.getText().toString() + "&password=" + AllKeys.TAG_USERID + "";
    private void SendNewRegistartionDetailsToServer() {

        try {
            spotDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url_sendNewReg = AllKeys.TAG_WEBSITE_HAPPY + "/UpdatePassword?action=updatepassword&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&password=" + edtPassword.getText().toString() + "";

        Log.d(TAG, " URL Send USer DAta : " + url_sendNewReg);

        StringRequest str_senduserdata = new StringRequest(Request.Method.GET, url_sendNewReg, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "User Response : " + response.toString());


                if (response.toString().contains("Succesfuly")) {


                    sessionmanager.clearuserid("0");
                    Toast.makeText(ForgotPassActivity.this, "SuccessFully Change", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ForgotPassActivity.this, EasyIntroActivity.class);
                    // Closing all the Activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);
                    finish();



                    JSONArray arr = null;

                    try {
                        response = dbhandler.convertToJsonFormat(response);


                        JSONObject obj = new JSONObject(response);
                        arr = obj.getJSONArray("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                    /*
                    Intent ii = new Intent(context, DashBoardActivity.class);
                    startActivity(ii);
                    finish();
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);*/





                }

                try {
                    spotDialog.cancel();
                    spotDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in  Update Password : " + error.getMessage());


            }
        });

        MyApplication.getInstance().addToRequestQueue(str_senduserdata);


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

}
