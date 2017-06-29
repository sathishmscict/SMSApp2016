package com.tech9teen;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.youtube.player.YouTubeIntents;
import com.tech9teen.R;
import com.tech9teen.adapter.GalleryAlbumAdapterRecyclerView;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.GalleryAlbum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class TrendingVideosAcivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GalleryAlbumAdapterRecyclerView adapter;

    private List<GalleryAlbum> galleryVideoList = new ArrayList<GalleryAlbum>();

    private List<Integer> galleryVideoListID = new ArrayList<Integer>();


    private Context context = this;
    private dbhandler db;
    private SQLiteDatabase sd;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private TextView txtnodata;
    private CoordinatorLayout coordinatorLayout;
    private String TAG = TrendingVideosAcivity.class.getSimpleName();
    private SpotsDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_videos_acivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);


        sessionmanager = new SessionManager(context);

        userDetails = sessionmanager.getUserDetails();

        db = new dbhandler(context);
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        txtnodata = (TextView) findViewById(R.id.txtnodata);


        adapter = new GalleryAlbumAdapterRecyclerView(context, galleryVideoList);

        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);


        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String videoname = ((TextView) view.findViewById(R.id.albumname)).getText().toString();

                Log.d("Video Name :", videoname);

                String version = YouTubeIntents.getInstalledYouTubeVersionName(context);
                if (version != null) {
                    String text = String.format(getString(R.string.youtube_currently_installed), version);
                    //youTubeVersionText.setText(text);
                    YouTubeIntents.canResolvePlayVideoIntent(context);

                    Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(context, galleryVideoList.get(position).getDelete(), true, false);
                    startActivity(intent);

                } else {
//                    youTubeVersionText.setText(getString(R.string.youtube_not_installed));
                    //Toast.makeText(context, "Youtube not installed", Toast.LENGTH_SHORT).show();
                    Snackbar.make(coordinatorLayout,"Youtube not installed",Snackbar.LENGTH_LONG).show();
                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        pDialog = new SpotsDialog(context);


        if (NetConnectivity.isOnline(context)) {

            try {
                pDialog.setCancelable(false);
                pDialog.show();
                GetVideosDetailsFromServer();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {

            checkInternet();
        }


    }

    /**
     * Get Image Gallery Album Details From Server
     */

    private void GetVideosDetailsFromServer() {

        pDialog.show();

        String url_videos = AllKeys.TAG_WEBSITE_HAPPY + "/GetVideos?action=videos";


        Log.d("URL GetVideos : ", url_videos);


        /**
         * Get Data using volley library
         */
        JsonArrayRequest req = new JsonArrayRequest(url_videos,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();


                        galleryVideoList.clear();
                        galleryVideoListID.clear();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject c = response.getJSONObject(i);


                                String video_thumbnail_url = "http://img.youtube.com/vi/" + c.getString(AllKeys.TAG_VIDEO_THUMNAIL) + "/sddefault.jpg";

                                GalleryAlbum GA = new GalleryAlbum(c.getString(AllKeys.TAG_VIDEO_NAME), c.getString(AllKeys.TAG_VIDEO_NAME), video_thumbnail_url, c.getString(AllKeys.TAG_VIDEO_THUMNAIL));

                                galleryVideoList.add(GA);
                                galleryVideoListID.add(c.getInt(AllKeys.TAG_VIDEOID));


                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }


                        if (galleryVideoList.size() > 0) {


                            recyclerView.setVisibility(View.VISIBLE);
                            txtnodata.setVisibility(View.GONE);

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            txtnodata.setVisibility(View.VISIBLE);
                            Snackbar.make(coordinatorLayout, "Sorry , no data found", Snackbar.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged();
                        if (pDialog.isShowing()) {

                            pDialog.dismiss();
                            pDialog.cancel();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                if (pDialog.isShowing()) {

                    pDialog.dismiss();
                    pDialog.cancel();
                }
            }
        });


        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }


    /**
     * Complete Get All Album Details From Server
     */


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "" + getString(R.string.no_network2), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, TrendingVideosAcivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_right, R.anim.slide_left);

                        } else {

                            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "" + getString(R.string.no_network2), Snackbar.LENGTH_SHORT);
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

    /**
     * RecyclerView Click Related Code
     */


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.common_sync, menu);
        MenuItem com = (MenuItem)menu.findItem(R.id.common_show).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.common_sync) {

            if (NetConnectivity.isOnline(context)) {

                pDialog.setCancelable(false);
                pDialog.show();
                GetVideosDetailsFromServer();


            } else {

                checkInternet();
            }
        }
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(context,
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
        Intent intent = new Intent(context,
                MenuActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }


}
