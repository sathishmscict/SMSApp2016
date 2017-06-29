package com.tech9teen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tech9teen.helper.AllKeys;
import com.tech9teen.helper.ServiceHandler;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class VerityForgotPassAcitvity extends AppCompatActivity {

    private TextView txtcode;
    private Context context = this;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private Timer timer;
    private Button btnsubmit, btnresend;
    private TextView txterror;
    private int counter = 0;
    private CountDownTimer countdowntimer;
    private int TIMER_SECONDS = 60000;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_verification);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().hide();


        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();


        Utility.checkPermission_ReadSMS(context);


        txtcode = (TextView) findViewById(R.id.txtcode);
        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        btnresend = (Button) findViewById(R.id.btnresend);
        txterror = (TextView) findViewById(R.id.txterror);


        countdowntimer = new VerityForgotPassAcitvity.MyCountDownTimer(TIMER_SECONDS, 1000);


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentcode = txtcode.getText().toString();
                // CheckVerification(currentcode);

                if (userDetails.get(SessionManager.KEY_CODE)

                        .equals(currentcode)) {


                    String url_statuupdate = AllKeys.TAG_WEBSITE_HAPPY + "/UpdateUserStatus?action=updatestatus&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&status=1";
                    Log.d(TAG, "Status Update : " + url_statuupdate);
                    String res =  new ServiceHandler().makeServiceCall(url_statuupdate ,ServiceHandler.GET);
                    Log.d(TAG , "Response Status Update : "+res);
                    if (res.equals("1")) {

                        sessionmanager.CheckSMSVerificationNoActivity("",

                                "1");
                        /*if (userDetails.get(SessionManager.KEY_NEWUSER).equals("1")) {

                            Intent in = new Intent(context, InviteCodeActivity.class);
                            startActivity(in);
                            finish();
                            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


                        } else {
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            //Intent in = new Intent(context, MenuActivity.class);

                            Intent in = new Intent(context, FirstFragment.class);
                            startActivity(in);
                            finish();
                            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                        }*/
                        //sessionmanager.clearuserid("0");
                        Intent in = new Intent(context, ForgotPassActivity.class);
                        startActivity(in);
                        finish();
                        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                    }


                    // serviceP.asmx/SetStudentVerificationStatusUpdate?type=varemp&empid=string&mobileno=string&status=string&clientid=string&branch=strin
                    //Intent in = new Intent(context, Gmail_detail_activity.class);


                } else {
                    Toast.makeText(context, "Invalid code", Toast.LENGTH_SHORT)
                            .show();

                }


            }
        });

        btnresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                txterror.setVisibility(View.GONE);

                if (counter >= 3) {
                    String dd = ", please mail us on info@" + getString(R.string.app_name) + " from your registered mobile no";
                    /*AlertDialogManager2.showAlertDialog(context,
                            getString(R.string.app_name), "Maximum Request Exceeded....",
							false);*/
                    Toast.makeText(context, dd, Toast.LENGTH_SHORT).show();
                } else {

                    if (btnresend.getText().toString().toLowerCase().equals("resend")) {
                        countdowntimer.start();
                        new VerityForgotPassAcitvity.sendSmsToUser().execute();
                    } else {
                        Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });

        if (NetConnectivity.isOnline(context)) {
            timer = new Timer();
            TimerTask hourlyTask = new TimerTask() {
                @Override
                public void run() {
                    // your code here...

                    userDetails = sessionmanager.getUserDetails();

					/*try {
						userDetails.get(SessionManager.KEY_CODE);

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
                    if (userDetails.get(SessionManager.KEY_RECEIVECODE)
                            .length() == 4) {
                        if (userDetails.get(SessionManager.KEY_RECEIVECODE)
                                .equals(userDetails
                                        .get(SessionManager.KEY_CODE))) {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        txtcode.setText("");
                                        txtcode.setText(""
                                                + userDetails
                                                .get(SessionManager.KEY_RECEIVECODE));


                                        String url_statuupdate = AllKeys.TAG_WEBSITE_HAPPY + "/UpdateUserStatus?action=updatestatus&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&status=1";
                                        Log.d(TAG, "Status Update : " + url_statuupdate);
                                        String res =  new ServiceHandler().makeServiceCall(url_statuupdate ,ServiceHandler.GET);
                                        Log.d(TAG , "Response Status Update : "+res);
                                        if (res.equals("1")) {

                                            sessionmanager.CheckSMSVerificationNoActivity("", "1");

                                            try {
                                                countdowntimer.cancel();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                            /*if (userDetails.get(SessionManager.KEY_NEWUSER).equals("1")) {

                                                Intent in = new Intent(context, InviteCodeActivity.class);
                                                startActivity(in);
                                                finish();
                                                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


                                            } else {

//                                                Intent in = new Intent(context, MenuActivity.class);
                                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                //Intent in = new Intent(context, MenuActivity.class);

                                                Intent in = new Intent(context, FirstFragment.class);
                                                startActivity(in);
                                                finish();
                                                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

                                            }*/
                                            //sessionmanager.clearuserid("0");
                                            Intent in = new Intent(context, ForgotPassActivity.class);
                                            startActivity(in);
                                            finish();
                                            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


                                        }


                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            });




							/*
							 * if(timer != null) { timer.cancel(); timer = null;
							 * }
							 */

                        }
                    }

                }
            };

            // schedule the task to run starting now and then every hour...
            timer.schedule(hourlyTask, 0l, 1000 * 5); // 1000*10*60 every 10 minutes

        }//Complete thread


        if (NetConnectivity.isOnline(context)) {
            new sendSmsToUser().execute();
            txterror.setVisibility(View.GONE);
        } else {
            txterror.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Please enable internet",
                    Toast.LENGTH_SHORT).show();
        }


    }//Oncreate completed


    private class sendSmsToUser extends AsyncTask<Void, Void, Void> {

        String Error = "";
        private String jsonStr;

        String ans = "";
        String url;
        ServiceHandler sh;
        String sendsms;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(Void... arg0) {
            // /Write here statements

            sh = new ServiceHandler();

            if (userDetails.get(SessionManager.KEY_CODE).equals("0")) {
                Random r = new Random();
                int code = r.nextInt(9999 - 1000) + 1000;
                Log.d("Verification Code : ", "" + code);

                //sendsms = AllKeys.WEBSITE + "GetMobileVerification?action=action&mobileno=" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "&code=" + code + "";

                sendsms = AllKeys.TAG_WEBSITE_HAPPY + "/GetVerificationCode?action=verification&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&mobileno=" + userDetails.get(SessionManager.KEY_USER_MOBILE) + "&code=" + code + "";

                Log.d("URL GetVerificationCode : ", "" + sendsms);

                sessionmanager.createUserSendSmsUrl("" + code, sendsms);

            } else {
                userDetails = sessionmanager.getUserDetails();
                sendsms = userDetails.get(SessionManager.KEY_SMSURL);
            }

            Log.d("sendsms res : ", "" + sendsms);
            String resp = sh.makeServiceCall(sendsms, ServiceHandler.GET);
            Log.d("sendsms res : ", "" + resp);

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.cancel();

            countdowntimer.start();

            // Write statement after background process execution
        }
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Toast.makeText(getApplicationContext(), "Please Complete Verification",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),
                NewVerificationActivity.class);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

    }


    public class MyCountDownTimer extends CountDownTimer {


        public MyCountDownTimer(long startTime, long interval) {

            super(startTime, interval);

        }

        public void onTick(long millisUntilFinished) {

            Log.d("millisunitFinished : ", "" + millisUntilFinished);

            //txtcounter.setText("seconds remaining: " + millisUntilFinished / 1000);
            int cc = (int) (long) (millisUntilFinished / 1000);
            if (cc > 9) {
                btnresend.setText("00 : " + (millisUntilFinished / 1000));
            } else {
                btnresend.setText("00 : 0" + (millisUntilFinished / 1000));
            }


            //gss = millisUntilFinished / 1000;

            //i++;


            //mProgressBar.setProgress((int) Math.floor(dd));

            //here you can have your logic to set text to edittext
        }

        public void onFinish() {

            countdowntimer.cancel();

            btnresend.setText("Resend");


        }


    }


}