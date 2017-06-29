package com.tech9teen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import com.tech9teen.R;

import java.util.HashMap;

public class WebviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener   {

    private WebView wv;
    private Context context=this;
    private String title,url;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails=new HashMap<String,String>();
    private ProgressBar progress;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String contactus="";
    private String TAG = WebviewActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinateLayout);




        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);

        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(WebviewActivity.this);



        wv=(WebView)findViewById(R.id.wv);

        progress = (ProgressBar) findViewById(R.id.progressBar);

        progress.setMax(100);
        progress.setVisibility(View.GONE);

        sessionmanager =new SessionManager(context);
        userDetails =sessionmanager.getUserDetails();


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }



        try
        {


            Intent ii =getIntent();
            title = ii.getStringExtra("TITLE");
            title = title.toUpperCase();
            setTitle(title);
            url = ii.getStringExtra("URL");
            contactus= ""+ii.getStringExtra("CONTACTUS");
            //url="http://radiant.dnsitexperts.com/result.aspx?deptid=9&empid=342";
            Log.d("WebviewActivity URL :",url);

        }
        catch (Exception e)
        {
            Log.d("WebViewActivity : ", e.getMessage());
        }



        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {


                                        if (NetConnectivity.isOnline(context)) {
                                            //new AsyncData1().execute();
                                            swipeRefreshLayout.setRefreshing(true);
                                            if (swipeRefreshLayout.isRefreshing()) {
                                                swipeRefreshLayout.setRefreshing(false);
                                            }

                                            LoadWebVieeFromURL();
                                            //  new SendAllDetialToServer().execute();
                                            Log.d("oncreate", "from run method");


                                        } else {
                                            swipeRefreshLayout.setRefreshing(false);
                                            checkInternet();
                                        }
                                    }
                                }
        );








    }

    private void LoadWebVieeFromURL() {


        wv.getSettings().setJavaScriptEnabled(true);
        //  wv.getSettings().setDisplayZoomControls(true);
        wv.getSettings().setAppCacheEnabled(true);



        wv.loadUrl(url);
        wv.setWebViewClient(new MyWebViewClient());
        WebviewActivity.this.progress.setProgress(0);

    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {



            if(Uri.parse(url).getHost().contains("http://") || Uri.parse(url).getHost().contains("ims") || Uri.parse(url).getHost().contains("co"))
            {
                //open url contain in webview
                return  false;

            }

            Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(url));
            startActivity(intent);

            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            WebviewActivity.this.progress.setProgress(100);
            super.onPageFinished(view, url);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
           //
           // progress.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            WebviewActivity.this.progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }








   /* public boolean onKeyDownssdadasdsad(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if(wv.canGoBack())
                    {
                        wv.goBack();

                    }
                    else
                    {
                        finish();
                    }
                    return true;
            }

        }
        return onKeyDown(keyCode, event);
    }*/
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {



            try {
                String str = context.getPackageName()+"."+userDetails.get(SessionManager.KEY_ACTIVITYNAME);
                Log.d(TAG ,"Class Name : "+str);
                Intent ii= new Intent(getApplicationContext(),Class.forName(context.getPackageName()+"."+userDetails.get(SessionManager.KEY_ACTIVITYNAME)));
                startActivity(ii);
                finish();
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }




        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {



            String str = context.getPackageName()+"."+userDetails.get(SessionManager.KEY_ACTIVITYNAME);
            Log.d(TAG ,"Class Name : "+str);
            Intent ii= new Intent(getApplicationContext(),Class.forName(context.getPackageName()+"."+userDetails.get(SessionManager.KEY_ACTIVITYNAME)));
            startActivity(ii);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRefresh() {
        if (NetConnectivity.isOnline(context)) {
            //new AsyncData1().execute();
            //  swipeRefreshLayout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3);
            swipeRefreshLayout.setRefreshing(true);
            //swipeRefreshLayout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3);

            Log.d("swipe from ", "onrefresh method");
            LoadWebVieeFromURL();





        } else {
            swipeRefreshLayout.setRefreshing(false);


            checkInternet();

        }
    }

    public void checkInternet() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WebviewActivity.this);
        alertDialogBuilder.setMessage(getString(R.string.no_network3));

        alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), WebviewActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

            }
        });



        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
