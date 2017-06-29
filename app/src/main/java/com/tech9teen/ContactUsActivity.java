package com.tech9teen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tech9teen.R;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.helper.ServiceHandler;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ContactUsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_MAP = 121;
    private CoordinatorLayout coordinatorLayout;
    private Context context = this;
    private TextView txtaddress;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private double latitude = 71.12132;
    private double longitude = 21.121212;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tech9teen.R.layout.activity_contact_us);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        } catch (Exception e) {
            Log.d("Error Actionbar", "" + e.getMessage());

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        txtaddress = (TextView) findViewById(R.id.txtaddress);


        sessionManager = new SessionManager(getApplicationContext());
        userDetails = new HashMap<String, String>();
        userDetails = sessionManager.getUserDetails();

        try {

            latitude = Double.parseDouble(userDetails
                    .get(SessionManager.KEY_LATTITUDE));
            longitude = Double.parseDouble(userDetails
                    .get(SessionManager.KEY_LONGTITUDE));
        } catch (NumberFormatException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        try {


            String aa = userDetails.get(SessionManager.KEY_CONTACT_ADDRESS);
            aa = aa.replace(",,", "\n");
            txtaddress.setText("" + aa);

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {

            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (NetConnectivity.isOnline(context)) {
            // new GetContactUsDetailsFromServer().execute();

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContactUsActivity.this);

            // set dialog message
            alertDialogBuilder
                    .setTitle("No Internet Connection")
                    .setMessage("Please Check Your Wi-Fi OR Mobile Network Connection And Try Again.")
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_error)

                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity

                            Intent ii = new Intent(context, DashBoardActivity.class);
                            startActivity(ii);
                            //finish();

                            try {
                                ConnectivityManager dataManager;
                                dataManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
                                dataMtd.setAccessible(true);
                                dataMtd.invoke(dataManager, true);
                            } catch (NoSuchMethodException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            //Toast.makeText(getActivity(),"Please enable wifi or mobile data",Toast.LENGTH_SHORT).show();
        }


    }


    private void initilizeMap() {
        try {

            // Changing marker icon
            // marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));

            // adding marker
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map)).getMap();

                //latitude =21.231212;
                //longitude=72.324231;
                // create marker
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latitude, longitude)).title(
                        getString(R.string.app_name));

                // Changing marker icon
                MarkerOptions icon = marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                icon = marker.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.ic_launcher));

                // adding marker

                LatLng latLng = new LatLng(latitude, longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        latLng, 15);
                googleMap.animateCamera(cameraUpdate);
                // locationManager.removeUpdates(this);

                googleMap.addMarker(marker);
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ContactUsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_MAP);


                } else {


                    googleMap.setMyLocationEnabled(true);


                }


                // check if map is created successfully or not
                if (googleMap == null) {
                    Toast.makeText(context,
                            "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        } catch (Exception e) {
            System.out.print("Error :" + e.getMessage());
        }

    }


    private class GetContactUsDetailsFromServer extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;


        String jsonStr;
        private JSONArray allcontactusdetails;

        String Address, Email, Phone;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ContactUsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // /Write here statements

            ServiceHandler sh = new ServiceHandler();
            String url = AllKeys.WEBSITE
                    + "GetJSONForContactUsData?type=contact";
            jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            if (jsonStr != null && !jsonStr.equals("")) {
//                jsonStr = "{\"" + AllKeys.GENERAL_ARRAY + "\":" + jsonStr + "}";
                jsonStr = dbhandler.convertToJsonFormat(jsonStr);

                try {
                    Log.d("Contact Us Response: ", "> " + jsonStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(jsonStr);
                    //allcontactusdetails = jsonObj.getJSONArray(Allkeys.GENERAL_ARRAY);
                    allcontactusdetails = jsonObj.getJSONArray("data");
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                // Getting JSON Array node

                try {


                    // looping through All Contacts
                    for (int i = 0; i < allcontactusdetails.length(); i++) {
                        JSONObject c = allcontactusdetails.getJSONObject(i);


                        Address = "Contact Person :" + c.getString(AllKeys.TAG_CONTACT_PERSON) + ",," + c.getString(AllKeys.TAG_CONTACT_ADDRESS) + ",,Mobile No : "
                                + c.getString(AllKeys.TAG_CONTACT_MOBILE) + ",,Email : "
                                + c.getString(AllKeys.TAG_CONTACT_EMAIL);

                        latitude = c.getDouble(AllKeys.TAG_CONTACT_LATTITUDE);
                        longitude = c.getDouble(AllKeys.TAG_CONTACT_LONGTITUDE);


                        sessionManager.StoreContactUsDetails("" + c.getString(AllKeys.TAG_CONTACT_LATTITUDE),
                                "" + c.getString(AllKeys.TAG_CONTACT_LONGTITUDE),
                                "" + c.getString(AllKeys.TAG_CONTACT_ADDRESS) + ",,Phone No : "
                                        + c.getString(AllKeys.TAG_CONTACT_PHONE) + ",,Email : "
                                        + c.getString(AllKeys.TAG_CONTACT_EMAIL), Email, Phone);
                        Log.d("Contact Us Data :", "Address :" + Address + " Lattitude " + latitude + " Longtitude " + longitude + " Email " + Email + " Phone " + Phone + "");


                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }


            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            // Write statement after background process execution


            try {


                Address = Address.replace(",,", "\n");

                txtaddress.setText("" + Address);
                initilizeMap();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }


        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(context, DashBoardActivity.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

    }
}
