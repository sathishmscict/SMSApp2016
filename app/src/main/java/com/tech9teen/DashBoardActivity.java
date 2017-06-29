package com.tech9teen;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.fragment.FragmentHome;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.ContactsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class DashBoardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = this;
    private SpotsDialog spotDialog;
    boolean doubleBackToExitPressedOnce = false;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private String TAG = DashBoardActivity.class.getSimpleName();
    private ActionBarDrawerToggle toggle;
    private TextView txtname, txtemail;
    private ImageView imgProfilePic;
    private Menu menu;

    private ArrayList<ContactsData> listContacts = new ArrayList<ContactsData>();
    private CoordinatorLayout coordinateLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        if (android.os.Build.VERSION.SDK_INT < 21) {

            setContentView(R.layout.activity_dash_board_sdk19);
        }
        else
        {
        setContentView(R.layout.activity_dash_board);

        }





        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                MyFirebaseInstanceIDService mfi = new MyFirebaseInstanceIDService();

                mfi.onTokenRefresh();
                mfi.onTokenRefreshNew(context);

            }
        });







       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }




        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);


        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);


        sessionmanager = new SessionManager(context);
        userDetails = sessionmanager.getUserDetails();

        db = new dbhandler(context);
        sd = db.getWritableDatabase();
        sd = db.getReadableDatabase();


        if(userDetails.get(sessionmanager.KEY_USERID).equals("0")){

            Intent i = new Intent(context, EasyIntroActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
            finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        int res = sessionmanager.checkLogin();

//        res=1;
        if (res == 0) {

            if (userDetails.get(SessionManager.KEY_VERSTATUS).equals("1")) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            MyFirebaseInstanceIDService mfs = new MyFirebaseInstanceIDService();
                            mfs.onTokenRefresh();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "OnTokenRefresh Error : " + e.getMessage());
                        }
                        // mfs.onTokenRefreshNew(context);


                    }
                });


                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            AllKeys.MY_PERMISSIONS_REQUEST_WRTE_EXTERNAL_STORAGE);

                } else {


                    db.backupDB(context);


                }


            } else {

                if (userDetails.get(SessionManager.KEY_USER_MOBILE).length() != 10) {

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


            }

        }


        /**
         * Navigation Drawer Handler
         */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
       /* ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.abc_action_bar_home_description, R.string.abc_action_bar_home_description) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };*/
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        /*final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();*/



        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //app:headerLayout="@layout/nav_header_menu"
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_dash_board);

        if (android.os.Build.VERSION.SDK_INT < 21) {

        headerLayout.setVisibility(View.GONE);

        }



        try {


            txtname = (TextView) headerLayout.findViewById(R.id.txtName);
            txtemail = (TextView) headerLayout.findViewById(R.id.txtEmail);
            imgProfilePic = (ImageView) headerLayout.findViewById(R.id.imgProfilePic);

            SetUserProfilePictireFromBase64EnodedString();


            imgProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    /*getMenuInflater().inflate(R.menu.activity_dash_board_drawer, menu);
                    MenuItem mProfileFrag = menu.findItem(R.id.nav_profile);

                    onNavigationItemSelected(mProfileFrag);*/


                    /*MenuItem mDefaultFrag = (MenuItem) navigationView.findViewById(R.id.nav_profile);
                    onNavigationItemSelected(mDefaultFrag);*/


                }
            });


            txtemail.setText("" + userDetails.get(SessionManager.KEY_EMAIL));
            txtname.setText("" + userDetails.get(SessionManager.KEY_USERNAME));
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * Compelte Navigation drawer handler
         */


        if (!userDetails.get(SessionManager.KEY_USERID).equals("0") && userDetails.get(SessionManager.KEY_VERSTATUS).equals("1")) {


            Fragment fragment = new FragmentHome();
            //getSupportActionBar().setTitle(getString(R.string.app_name));


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            getGroupMasterDetailsFromServer();
            getGroupMasterDataDetailsFromServer();
            getTemplateMasterDetailsFromServer();
            getReminderMasterDetailsFromServer();
            getSMSSettingMasterDetailsFromServer();
            GetSMSCategoryDetailsFromServer();
            GetSMSSubCategoryDetailsFromServer();


            /**
             * Get all contact derails from device
             */
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                        AllKeys.MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBERS);

            } else {


                /**
                 * Chekc Conatct table if empty then Fetch contacts and insert into table
                 */
                Cursor managedCursor = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


                    /*String query = "select * from "+ dbhandler.TABLE_CONTACTMASTER +"";
                    Cursor cur = sd.rawQuery(query , null);*/
                Log.d(TAG, "Total " + managedCursor.getCount() + " Records Found in Device");
                if (managedCursor.getCount() != Integer.parseInt(userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_CONTACTS)) || userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_CONTACTS).equals("0")) {

                    new GetContactUsingAsyncTask().execute();

                }
                managedCursor.close();
                ;


            }


            if (!userDetails.get(SessionManager.KEY_DAY_OF_MONTH).equals(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))) {
                getGoldRateFromServer();
                getDollorRateFromServer();


            } else {

                //txtDollorRate.setText(userDetails.get(SessionManager.KEY_DOLLOR_PRICE));
                //txtGoldRate.setText(userDetails.get(SessionManager.KEY_GOLD_RATE));
            }


        }


        /**
         * Check Sender id Exist or not
         */
        //sd.delete(dbhandler.TABLE_SMSSETTINGS , null,null);

        String qry_Senderid="Select * from "+ dbhandler.TABLE_SMSSETTINGS +"";
        Log.d(TAG , "Query Check SenderID : "+qry_Senderid);
        Cursor cur = sd.rawQuery(qry_Senderid,null);
        Log.d(TAG , "Total "+cur.getCount()+" Records Found in "+dbhandler.TABLE_SMSSETTINGS);
        if(cur.getCount() == 0)
        {
            /**
             * Display Sender ID Request
             */
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

            final TextView txtError = (TextView)dialog.findViewById(R.id.txtError);


            final Button btnCancelRequest = (Button) dialog.findViewById(R.id.btnCancelRequest);
            Button btnSendRequest = (Button) dialog.findViewById(R.id.btnSendRequest);


            btnSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(spnSenderidType.getSelectedItemPosition() == 0)
                    {
                        txtError.setText("Please select Route");

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
            dialog.show();


        }

        /**
         * Complete CheckSender ID
         */


    }
    //OnCreate Completed


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.dash_board, menu);


        try {
            this.menu = menu;
            getMenuInflater().inflate(R.menu.dash_board, menu);
            // sync_data = (MenuItem) menu.findItem(R.id.action_sync);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_account) {

/*
            Intent in = new Intent(context, NewLoginActivity.class);
            startActivity(in);
            finish();
*/


           // Toast.makeText(getApplicationContext(), "Account", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_contactus) {



            Intent intent = new Intent(getApplicationContext(),
                    ContactUsActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

           // Toast.makeText(getApplicationContext(), "ContactUs", Toast.LENGTH_SHORT).show();
            Log.d(TAG , "Clicked On ContactUs");

            return true;

        } else if (id == R.id.action_logout) {

        //    Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
            Log.d(TAG , " Clicked On Logout");

            try {
                context.deleteDatabase(dbhandler.dbname);

                sessionmanager.logoutUser();
                Intent i = new Intent(context, EasyIntroActivity.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_aboutus)
        {
            sessionmanager.setActivityname("DashBoardActivity");

            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra("TITLE","AboutUs");
            intent.putExtra("URL",AllKeys.URL_TermsAndConditions);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_right, R.anim.slide_left);

        }
       else if (id == R.id.nav_sendsms) {

  //          Toast.makeText(context, "Send SMS", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.nav_referandearn) {
            Log.d(TAG, "Clinked on Menu : Refer & Earn");
            //Toast.makeText(context, "Refer and Earn", Toast.LENGTH_SHORT).show();

            try {
                String invite = "You're invited! use referal code " + userDetails.get(SessionManager.KEY_REF_CODE) + " and  Enjoy special balance of 100 SMS at Tech9teen SMSApp. Install now! https://play.google.com/store/apps/details?id=" + context.getPackageName() + "";
                Log.d(TAG, " Invite Message : " + invite);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        invite);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (id == R.id.nav_group) {
//            Toast.makeText(context, "Group Master", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Clinked on Menu : Group Master");


            Intent intent = new Intent(getApplicationContext(),
                    GroupMasterActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


        } else if (id == R.id.nav_template) {


            Intent intent = new Intent(getApplicationContext(),
                    MessageTemplateActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


//            Toast.makeText(context, "Template Master", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Clinked on Menu : Template Master");

        } else if (id == R.id.nav_smsmanager) {

            Intent intent = new Intent(getApplicationContext(),
                    SMSGroupActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


         //   Toast.makeText(context, "SMS Manager", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Clinked on Menu : SMS Manager");

        } else if (id == R.id.nav_reports) {

            Intent inte = new Intent(getApplicationContext() ,RemindersAcivity.class);
            startActivity(inte);
            finish();



            Toast.makeText(context, "Reports", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Clinked on Menu : Reports");

        } else if (id == R.id.nav_rates) {

            /*Toast.makeText(context, "Rates", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Clinked on Menu : Rates");*/
            sessionmanager.setActivityname("DashBoardActivity");

            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra("TITLE","Rates");
            intent.putExtra("URL",AllKeys.URL_Rates);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(getApplicationContext(),
                    SettingsActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);



            //Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Clinked on Menu : Settings");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SetUserProfilePictireFromBase64EnodedString() {
        try {
            userDetails = sessionmanager.getUserDetails();
            String myBase64Image = userDetails.get(SessionManager.KEY_ENODEDED_STRING);
            if (!myBase64Image.equals("")) {

                Bitmap myBitmapAgain = dbhandler.decodeBase64(myBase64Image);

                imgProfilePic.setImageBitmap(myBitmapAgain);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Decode Img Exception : ", e.getMessage());
        }
    }


    /**
     * Get Current user contacts from device
     */
    public class GetContactUsingAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                dbhandler.DeleteTableData(sd, dbhandler.TABLE_CONTACTMASTER);


                Cursor managedCursor = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                Log.d(TAG, "Total " + managedCursor.getCount() + " Contact Records  Found ");
                sessionmanager.setPreviousContactsTotalCount(String.valueOf(managedCursor.getCount()));

                if (managedCursor.getCount() > 0) {
                    int cnt = 0;
                    listContacts.clear();
                    while (managedCursor.moveToNext()) {
                        ++cnt;

                        String mobilenumber = managedCursor.getString(2);
                        mobilenumber = mobilenumber.replaceAll("\\D+", "");

                        //Log.d(TAG, cnt + " ID : " + managedCursor.getString(0) + " Number : " + mobilenumber + " Name  : " + managedCursor.getString(1));

                        ContentValues cv = new ContentValues();
                        cv.put(dbhandler.CONTACTMASTER_NAME, managedCursor.getString(1));
                        cv.put(dbhandler.CONTACTMASTER_MOBILE_NO, mobilenumber);
                        cv.put(dbhandler.CONTACTMASTER_ID, cnt);

                        dbhandler.InsertData(sd, dbhandler.TABLE_CONTACTMASTER, cv);

                        ContactsData cd = new ContactsData(mobilenumber, managedCursor.getString(1), cnt, false);
                        listContacts.add(cd);


                    }

                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideDialog();
            getContactDetailsAndSendtoServer();


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == AllKeys.MY_PERMISSIONS_REQUEST_WRTE_EXTERNAL_STORAGE) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted
                db.backupDB(context);
            } else {
                //Denied permission
                Toast.makeText(context, "Write External Storage Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }
        }

        if (requestCode == AllKeys.MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBERS) {
            if (dbhandler.verifyPermissions(grantResults)) {
                //Perform your action after permission has been granted

                /**
                 * Chekc Conatct table if empty then Fetch contacts and insert into table
                 */


                Cursor managedCursor = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


                    /*String query = "select * from "+ dbhandler.TABLE_CONTACTMASTER +"";
                    Cursor cur = sd.rawQuery(query , null);*/
                Log.d(TAG, "Total " + managedCursor.getCount() + " Records Found in Device");
                if (managedCursor.getCount() != Integer.parseInt(userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_CONTACTS)) || userDetails.get(SessionManager.KEY_COUNT_PREVIOUS_CONTACTS).equals("0")) {

                    new GetContactUsingAsyncTask().execute();

                }
                managedCursor.close();
                ;


                //getContactDetailsAndSendtoServer();


            } else {
                //Denied permission
                Toast.makeText(context, "Read Contacts Permission not granted", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout , "Call Permission not granted" , Snackbar.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void getContactDetailsAndSendtoServer() {

//        showDialog();



       /*         try {

                    dbhandler.DeleteTableData(sd, dbhandler.TABLE_CONTACTMASTER);


                    Cursor managedCursor = getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                    Log.d(TAG, "Total Records : " + managedCursor.getCount());
                    if (managedCursor.getCount() > 0) {
                        int cnt = 0;
                        listContacts.clear();
                        while (managedCursor.moveToNext()) {
                            ++cnt;

                            String mobilenumber = managedCursor.getString(2);
                            mobilenumber = mobilenumber.replaceAll("\\D+", "");

                            Log.d(TAG, cnt + " ID : " + managedCursor.getString(0) + " Number : " + mobilenumber + " Name  : " + managedCursor.getString(1));

                            ContentValues cv = new ContentValues();
                            cv.put(dbhandler.CONTACTMASTER_NAME, managedCursor.getString(1));
                            cv.put(dbhandler.CONTACTMASTER_MOBILE_NO, mobilenumber);
                            cv.put(dbhandler.CONTACTMASTER_ID, cnt);
                            ContactsData cd = new ContactsData(mobilenumber, managedCursor.getString(1), cnt, false);
                            listContacts.add(cd);

                            dbhandler.InsertData(sd, dbhandler.TABLE_CONTACTMASTER, cv);

                        }

                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
*/

        final String url_sendcontacts = AllKeys.TAG_WEBSITE_HAPPY + "/GetJSONForInsertContactDetail";
        StringRequest str_sendcontacts = new StringRequest(Request.Method.POST, url_sendcontacts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Contacts Sync Response : " + response.toString());

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Contact Sync Error :" + error.getMessage());

                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = null;
                try {
                    params = new HashMap<String, String>();


                    params.put("action", "synccontact");
                    params.put("userid", userDetails.get(SessionManager.KEY_USERID));


                    String json = "";


                    Log.d(TAG, "Total " + listContacts.size() + " Records From ContactMaster");
                    for (int i = 0; i < listContacts.size(); i++) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("ContactId", listContacts.get(i).getContacactid());
                            jsonObject.accumulate("ContactName", listContacts.get(i).getName());


                            jsonObject.accumulate("MobileNo", listContacts.get(i).getMobilenumber());

                            json = json + jsonObject.toString() + ",";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    json = json.substring(0, json.length() - 1);
                    json = "[" + json + "]";
                    Log.d("CalllogMst Data : ", json);
                    Log.d("Json Data : ", url_sendcontacts + "?action=synccontact&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&Data=" + json);
                    params.put("Data", json);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return params;


            }
        };

        MyApplication.getInstance().addToRequestQueue(str_sendcontacts);


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


    private void getDollorRateFromServer() {

        showDialog();
        String url_dollorrate = AllKeys.TAG_DOLLOR_RATE_API;
        Log.d(TAG, "Dollor Rate API URL :" + url_dollorrate);

        StringRequest str_dollorrate = new StringRequest(Request.Method.GET, url_dollorrate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Dollor Rate API Response : " + response.toLowerCase());


                try {
                    String res = response.replace("\"dataset\":{", "\"dataset\": [{");
                    Log.d(TAG, "Before Data : " + res);
                    res = res.replace("}}", "}]}");
                    Log.d(TAG, "After Data : " + res);

                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.getJSONArray("dataset");


                    Log.d(TAG, "Total Records : " + arr.length());
                    for (int i = 0; i < arr.length(); i++) {

                        JSONObject c = arr.getJSONObject(i);
                        Log.d(TAG, "Dollor ID : " + c.getString("id"));

                        JSONArray cc = c.getJSONArray("data");
                        Log.d(TAG, "Dollor data : " + cc.toString());
                        Log.d(TAG, "Dollor data Records : " + cc.length());

                        for (int j = 0; j <= 0; j++) {

                            Log.d(TAG, "Dollor  RAte : " + cc.get(j));

                            String data = cc.get(j).toString();
                            Log.d(TAG, "Data Before :" + data);
                            data = data.substring(1, data.length() - 1);

                            Log.d(TAG, "Data After :" + data);
                            List<String> myList = new ArrayList<String>(Arrays.asList(data.split(",")));


                          /*  txtDollorRate.setText(myList.get(1));*/
                            Log.d(TAG, "Dolor Rate : " + myList.get(1));

                            userDetails = sessionmanager.getUserDetails();

                            sessionmanager.setDollorAndGoldRateFromAPI(myList.get(1), userDetails.get(SessionManager.KEY_GOLD_RATE), "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));


                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Dollor Rate Error  : " + error.getMessage());

                hideDialog();

            }
        });

        MyApplication.getInstance().addToRequestQueue(str_dollorrate);


    }

    private void getGoldRateFromServer() {
        showDialog();

        String url_goldrate = AllKeys.TAG_GOLD_RATE_API;
        Log.d(TAG, "Gold Rate API URL: " + url_goldrate);
        //JSONObject str_goldrate = new JSONObject(Request.Method.GET ,url_goldrate,null,new JSONObject() )
        JsonObjectRequest str_goldrate = new JsonObjectRequest(Request.Method.GET, url_goldrate, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Gold Rate Response : " + response.toString());

                try {
                    //Log.d(TAG ,  "Data :"+response.getString("errors"));
                    Log.d(TAG, "ID :" + response.getInt("id"));
                    Log.d(TAG, "Source Name :" + response.getString("source_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i <= 0; i++) {

                        Log.d(TAG, "Gold RAte : " + arr.get(i));

                        String data = arr.get(i).toString();
                        Log.d(TAG, "Data Before :" + data);
                        data = data.substring(1, data.length() - 1);

                        Log.d(TAG, "Data After :" + data);
                        List<String> myList = new ArrayList<String>(Arrays.asList(data.split(",")));

                        /*txtGoldRate.setText(myList.get(4));*/
                        Log.d(TAG, "Gold Rate : " + myList.get(4));


                        userDetails = sessionmanager.getUserDetails();

                        sessionmanager.setDollorAndGoldRateFromAPI(userDetails.get(SessionManager.KEY_DOLLOR_PRICE), myList.get(4), "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                    }
                    Log.d(TAG, "Array Size : " + arr.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Gold Rate Error : " + error.toString());

                hideDialog();
            }
        });

        MyApplication.getInstance().addToRequestQueue(str_goldrate);

    }


    private void getGroupMasterDataDetailsFromServer() {


        showDialog();
        String url_getGroupMasterData = AllKeys.TAG_WEBSITE_HAPPY + "/GetGroupMasterDetail?action=getgroupdetail&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetGroupMasterDetails: " + url_getGroupMasterData);


        JsonArrayRequest str_getgroupsData = new JsonArrayRequest(url_getGroupMasterData, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GroupMasterDetails Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_GROUPMASTERDET, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv_grp = new ContentValues();


                            cv_grp.put(dbhandler.GROUPMASTER_DET_ID, c.getString(AllKeys.TAG_GROUP_DET_ID));
                            cv_grp.put(dbhandler.GROUPMASTER_DET_GROUPMASTER_ID, c.getString(AllKeys.TAG_GROUP_DET_GROUP_ID));
                            cv_grp.put(dbhandler.GROUPMASTER_DET_MOBILENUMBER, c.getString(AllKeys.TAG_GROUP_DET_MOBILENO));
                            cv_grp.put(dbhandler.GROUPMASTER_DET_CONTACTNAME, c.getString(AllKeys.TAG_GROUP_DET_CONTACT_NAME));
                            cv_grp.put(dbhandler.GROUPMASTER_DET_CONTACTID, c.getString(AllKeys.TAG_GROUP_DET_CONTACTID));

                            Log.d(TAG, "GroupMasterDet Data : " + cv_grp.toString());
                            sd.insert(dbhandler.TABLE_GROUPMASTERDET, null, cv_grp);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GroupMasterDet : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getgroupsData);


    }

    private void getGroupMasterDetailsFromServer() {

        showDialog();
        String url_getGroupMaster = AllKeys.TAG_WEBSITE_HAPPY + "/GetGroupMaster?action=getgroupmaster&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetGroupMaster : " + url_getGroupMaster);


        JsonArrayRequest str_getgroups = new JsonArrayRequest(url_getGroupMaster, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GroupMaster Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_GROUPMASTER, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv_grp = new ContentValues();

                            cv_grp.put(dbhandler.GROUPMASTER_ID, c.getString(AllKeys.TAG_GROUPID));
                            cv_grp.put(dbhandler.GROUPMASTER_NAME, c.getString(AllKeys.TAG_GROUPNAME));
                            cv_grp.put(dbhandler.GROUPMASTER_CONTACTS, c.getString(AllKeys.TAG_NO_OF_CONTACTS));
                            Log.d(TAG, "GroupMaster Data : " + cv_grp.toString());
                            sd.insert(dbhandler.TABLE_GROUPMASTER, null, cv_grp);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GroupMaster : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getgroups);


    }

    private void GetSMSSubCategoryDetailsFromServer() {


        showDialog();
        String url_smssubcategory = AllKeys.TAG_WEBSITE_HAPPY + "/GetSubCategory?action=subcategory&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL Get Subcategory : " + url_smssubcategory);
        JsonArrayRequest str_subcategory = new JsonArrayRequest(url_smssubcategory, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                Log.d(TAG, "Subcategory Response : " + response.toString());
                if (response != null) {
                    try {
                        //sd.delete(dbhandler.TABLE_SMS_SUBCATEGORY_MASTER, null, null);
                        sd.delete(dbhandler.TABLE_SENDERID_MASTER, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);

                            ContentValues cv = new ContentValues();

                            /*cv.put(dbhandler.SMS_SUBCATEGORY_MASTER_ID, c.getString(AllKeys.TAG_SMS_SUB_CATEGORY_ID));
                            cv.put(dbhandler.SMS_SUBCATEGORY_MASTER_NAME, c.getString(AllKeys.TAG_SMS_SUB_CATEGORY_NAME));
                            cv.put(dbhandler.SMS_SUBCATEGORY_MASTER_CATEGORYID, c.getString(AllKeys.TAG_SMS_CATEGORYID));
                            sd.insert(dbhandler.TABLE_SMS_SUBCATEGORY_MASTER, null, cv);*/

                            cv.put(dbhandler.SMS_SENDERID_ID, c.getString(AllKeys.TAG_SMS_SUB_CATEGORY_ID));
                            cv.put(dbhandler.SMS_SENDERID_NAME, c.getString(AllKeys.TAG_SMS_SUB_CATEGORY_NAME));
                            cv.put(dbhandler.SMS_SENDERID_CATEGORYID, c.getString(AllKeys.TAG_SMS_CATEGORYID));
                            sd.insert(dbhandler.TABLE_SENDERID_MASTER, null, cv);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error in smsSubcategory : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_subcategory);

    }

    private void GetSMSCategoryDetailsFromServer() {

        showDialog();
        String url_smscategory = AllKeys.TAG_WEBSITE_HAPPY + "/GetCategory?action=category&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GEt Category : " + url_smscategory);
        JsonArrayRequest str_getcategory = new JsonArrayRequest(url_smscategory, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response != null) {
                    Log.d(TAG, "SMS Category Response : " + response.toString());

                    sd.delete(dbhandler.TABLE_SMS_CATEGORY_MASTER, null, null);

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.getJSONObject(i);


                            ContentValues cv = new ContentValues();

                            cv.put(dbhandler.SMS_CATEGORY_MASTER_ID, c.getString(AllKeys.TAG_SMS_CATEGORYID));
                            cv.put(dbhandler.SMS_CATEGORY_MASTER_NAME, c.getString(AllKeys.TAG_SMS_CATEGORYNAME));
                            /*cv.put(dbhandler.SMS_CATEGORY_MASTER_TYPE, "W");*/


                            sd.insert(dbhandler.TABLE_SMS_CATEGORY_MASTER, null, cv);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }//Close if condition

                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "SMS Category Error : " + error.getMessage());
                hideDialog();

            }
        });

        MyApplication.getInstance().addToRequestQueue(str_getcategory);
    }


    private void getTemplateMasterDetailsFromServer() {


        showDialog();
        String url_getMsgTemplates = AllKeys.TAG_WEBSITE_HAPPY + "/GetMsgTemplate?action=getmsgtemplate&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetMsgTemplates: " + url_getMsgTemplates);


        JsonArrayRequest str_getMsgTemplates = new JsonArrayRequest(url_getMsgTemplates, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GetMsgTemplates Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_TEMPALTE_MASTER, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv = new ContentValues();


                            cv.put(dbhandler.TEMPLATE_TITLE, c.getString(AllKeys.TAG_TEMPALTE_TITLE));
                            cv.put(dbhandler.TEMPLATE_DESCR, c.getString(AllKeys.TAG_TEMPALTE_DESCR));
                            cv.put(dbhandler.TEMPLATE_ID, c.getString(AllKeys.TAG_TEMPALTEID));
                            Log.d(TAG, "Template Master Data : " + cv.toString());

                            //sd.insert(dbhandler.TABLE_TEMPALTE_MASTER, null, cv);
                            //dbhandler.InsertData(sd,dbhandler.TABLE_TEMPALTE_MASTER,cv);


                            sd.insert(dbhandler.TABLE_TEMPALTE_MASTER, null, cv);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GetMsgTemplates : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getMsgTemplates);


    }


    private void getReminderMasterDetailsFromServer() {


        showDialog();
        String url_getReminders = AllKeys.TAG_WEBSITE_HAPPY + "/GetAnniversaryBdayReminder?action=getreminder&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetReminders: " + url_getReminders);


        JsonArrayRequest str_getReminders = new JsonArrayRequest(url_getReminders, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GetReminders Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_REMINDER, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv = new ContentValues();

                            cv.put(dbhandler.REMINDER_Descr, c.getString(AllKeys.TAG_REMINDER_DESCR));
                            cv.put(dbhandler.REMINDER_DATE, c.getString(AllKeys.TAG_REMINDER_DATE));
                            cv.put(dbhandler.REMINDER_TIME, c.getString(AllKeys.TAG_REMINDER_TIME));
                            cv.put(dbhandler.REMINDER_ID, c.getString(AllKeys.TAG_REMINDER_ID));
                            Log.d(TAG, "Reminder Master Data : " + cv.toString());

                            sd.insert(dbhandler.TABLE_REMINDER, null, cv);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GetReminders : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getReminders);


    }


    private void getSMSSettingMasterDetailsFromServer() {


        showDialog();
        String url_getSMSSettings = AllKeys.TAG_WEBSITE_HAPPY + "/GetSMSSettings?action=smssetting&userid=" + userDetails.get(SessionManager.KEY_USERID) + "";
        Log.d(TAG, "URL GetSMSSettings: " + url_getSMSSettings);


        JsonArrayRequest str_getSMSSettings = new JsonArrayRequest(url_getSMSSettings, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "Get GetSMSSettings Response : " + response.toString());

                if (response != null) {

                    try {
                        sd.delete(dbhandler.TABLE_SMSSETTINGS, null, null);
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject c = (JSONObject) response.get(i);
                            ContentValues cv = new ContentValues();


                            cv.put(dbhandler.SMSSETTINGS_TYPE, c.getString(AllKeys.TAG_SETTINGS_TYPE));
                            cv.put(dbhandler.SMSSETTINGS_SENDERID, c.getString(AllKeys.TAG_SETTINGS_SENDERID));
                            cv.put(dbhandler.SMSSETTINGS_USERNAME, c.getString(AllKeys.TAG_SETTINGS_USERNAME));
                            cv.put(dbhandler.SMSSETTINGS_PASSWORD, c.getString(AllKeys.TAG_SETTINGS_PASSWORD));
                            Log.d(TAG, "SMSSettings Master Data : " + cv.toString());

                            sd.insert(dbhandler.TABLE_SMSSETTINGS, null, cv);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                hideDialog();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Log.d(TAG, "Error in Get GetSMSSettings : " + error.getMessage());

            }
        });
        MyApplication.getInstance().addToRequestQueue(str_getSMSSettings);


    }

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
                            Intent intent = new Intent(context, DashBoardActivity.class);
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




}
