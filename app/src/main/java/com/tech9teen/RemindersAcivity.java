package com.tech9teen;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.tech9teen.R;
import com.tech9teen.adapter.ReminderAdapterRecyclerView;
import com.tech9teen.app.MyApplication;
import com.tech9teen.database.dbhandler;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.pojo.ReminderData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class RemindersAcivity extends AppCompatActivity {


    Calendar calendar = Calendar.getInstance();


    private static final int DATE_PICKER_ID = 123;
    private static final int TIME_DIALOG_ID = 124;
    private RecyclerView recycler_view_reminders;
    private Button btnSaveData;
    private EditText txtDescr, txtDate, txtTime;
    private TextInputLayout input_layout_edtdescr, input_layout_edtdate, input_layout_edttime;
    private Context context = this;
    private ReminderAdapterRecyclerView adapter;
    private ArrayList<ReminderData> listRemidners = new ArrayList<ReminderData>();
    private String TAG = RemindersAcivity.class.getSimpleName();
    private int RECORD_ID = 0;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails = new HashMap<String, String>();
    private dbhandler db;
    private SQLiteDatabase sd;
    private CoordinatorLayout coordinateLayout;
    private LinearLayout lladddata;
    private LinearLayout llshowdata;
    private FloatingActionButton fab;
    private MenuItem menu_sync, menu_show;
    private Menu menu;
    private int y, m, d;
    private int spm;
    private String startDate, showsdate;
    private int hour, minute;

    InputMethodManager imm;
    private SpotsDialog spotDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_acivity);


        spotDialog = new SpotsDialog(context);
        spotDialog.setCancelable(false);


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


        setTitle("Reminders");


        recycler_view_reminders = (RecyclerView) findViewById(R.id.recycler_view_reminders);
        btnSaveData = (Button) findViewById(R.id.btnSaveData);
        txtDescr = (EditText) findViewById(R.id.edtdescr);
        txtDate = (EditText) findViewById(R.id.edtdate);
        txtTime = (EditText) findViewById(R.id.edttime);

        input_layout_edtdescr = (TextInputLayout) findViewById(R.id.input_layout_edtdescr);
        input_layout_edtdate = (TextInputLayout) findViewById(R.id.input_layout_edtdate);
        input_layout_edttime = (TextInputLayout) findViewById(R.id.input_layout_edttime);


        lladddata = (LinearLayout) findViewById(R.id.lladddata);
        llshowdata = (LinearLayout) findViewById(R.id.llshowdata);


        fab = (FloatingActionButton) findViewById(R.id.fab);


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

              /*  *//** Creating a bundle object to pass currently set time to the fragment *//*
                Bundle b = new Bundle();

                *//** Adding currently set hour to bundle object *//*
                b.putInt("set_hour", mHour);

                *//** Adding currently set minute to bundle object *//*
                b.putInt("set_minute", mMinute);

                *//** Instantiating TimePickerDialogFragment *//*
                TimePickerDialogFragment timePicker = new TimePickerDialogFragment(mHandler);

                *//** Setting the bundle object on timepicker fragment *//*
                timePicker.setArguments(b);

                *//** Getting fragment manger for this activity *//*
                FragmentManager fm = getSupportFragmentManager();

                *//** Starting a fragment transaction *//*
                FragmentTransaction ft = fm.beginTransaction();

                *//** Adding the fragment object to the fragment transaction *//*
                ft.add(timePicker, "time_picker");

                *//** Opening the TimePicker fragment *//*
                ft.commit();*/


                showDialog(TIME_DIALOG_ID);


            }
        });


        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //txtTime.setText("");
                //txtDate.setText("");
                txtDescr.setText("");

                btnSaveData.setText("save data");


                lladddata.setVisibility(View.VISIBLE);
                llshowdata.setVisibility(View.GONE);


                fab.setVisibility(View.GONE);

                menu_show.setVisible(true);
                menu_sync.setVisible(false);

            }
        });


        lladddata.setVisibility(View.GONE);
        llshowdata.setVisibility(View.VISIBLE);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view_reminders.setLayoutManager(mLayoutManager);
        recycler_view_reminders.addItemDecoration(new dbhandler.GridSpacingItemDecoration(1, dbhandler.dpToPx(4, context), true));
        recycler_view_reminders.setItemAnimator(new DefaultItemAnimator());


        adapter = new ReminderAdapterRecyclerView(context, listRemidners);
        recycler_view_reminders.setAdapter(adapter);


        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean errorFlag = false;
                if (txtDescr.getText().toString().equals("")) {

                    errorFlag = true;
                    input_layout_edtdescr.setErrorEnabled(true);
                    input_layout_edtdescr.setError("Please enter description");

                } else {

                    input_layout_edtdescr.setErrorEnabled(false);

                }

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


                if (errorFlag == false) {


                    if (NetConnectivity.isOnline(context)) {


                        showDialog();

                        if (btnSaveData.getText().toString().toLowerCase().equals("save data")) {


                            Log.d(TAG, "Insert Data Called");

                            /**
                             * Get Max Id Of Template Master Table
                             */
                            String query_maxid = "select MAX(" + dbhandler.REMINDER_ID + ") from " + dbhandler.TABLE_REMINDER + "";
                            Cursor cur_maxid = sd.rawQuery(query_maxid, null);
                            cur_maxid.moveToFirst();
                            int maxid = cur_maxid.getInt(0);
                            Log.d(TAG, "Before  MAxid : " + maxid);
                            ++maxid;
                            Log.d(TAG, "After  MAxid : " + maxid);

                            cur_maxid.close();


                            String url_sendreminderdata = AllKeys.TAG_WEBSITE_HAPPY + "/InsertAnniversaryBdayReminder?action=insertreminder&reminderid=" + maxid + "&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&type=&description=" + dbhandler.convertEncodedString(txtDescr.getText().toString()) + "&date=" + dbhandler.convertToJsonDateFormat(txtDate.getText().toString()) + "&time=" + dbhandler.convertEncodedString(txtTime.getText().toString()) + "";
                            Log.d(TAG, "URL Send ReminderInsert " + url_sendreminderdata);
                            final int finalMaxid = maxid;
                            StringRequest str_insertReminder = new StringRequest(Request.Method.GET, url_sendreminderdata, new Response.Listener<String>() {
                                @Override

                                public void onResponse(String response) {

                                    Log.d(TAG, "Insert Reminder Response : " + response.toString());


                                    if (response.equals("1")) {


                                        ContentValues cv = new ContentValues();

                                        cv.put(dbhandler.REMINDER_Descr, txtDescr.getText().toString());
                                        cv.put(dbhandler.REMINDER_DATE, txtDate.getText().toString());
                                        cv.put(dbhandler.REMINDER_TIME, txtTime.getText().toString());
                                        cv.put(dbhandler.REMINDER_ID, finalMaxid);
                                        Log.d(TAG, "Reminder Master Data : " + cv.toString());

                                        //sd.insert(dbhandler.TABLE_TEMPALTE_MASTER, null, cv);
                                        dbhandler.InsertData(sd, dbhandler.TABLE_REMINDER, cv);


                                        Snackbar.make(coordinateLayout, "Reminder has been added", Snackbar.LENGTH_SHORT).show();


                                        FillDataOnRecyclerView();


                                        // int i = 20;

                                        Intent intent = new Intent(RemindersAcivity.this, MyBroadcastReceiver.class);
                                        intent.putExtra("SATISH", txtDescr.getText().toString());
                                        intent.putExtra("ALARAMID", String.valueOf(finalMaxid));
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                                RemindersAcivity.this, finalMaxid, intent, 0);
                                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
                                                , pendingIntent);

                                        Log.d(TAG, "Alarm set in " + calendar.getTimeInMillis() + " seconds");
                                        //Toast.makeText(RemindersAcivity.this, "Alarm set in " + calendar.getTimeInMillis() + " seconds",Toast.LENGTH_LONG).show();


                                        txtDescr.setText("");
                                        txtDate.setText("");
                                        txtTime.setText("");

                                    } else {
                                        Snackbar.make(coordinateLayout, "Sorry, Reminder not added. Try Again", Snackbar.LENGTH_SHORT).show();
                                    }



                                    hideDialog();

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.d(TAG, "Error Inserting Reminder Data : " + error.getMessage());
                                    Snackbar.make(coordinateLayout, "Sorry, Reminder not added. Try Again", Snackbar.LENGTH_SHORT).show();


                                    hideDialog();


                                }
                            });

                            MyApplication.getInstance().addToRequestQueue(str_insertReminder);


                        }//closed Insert data if condition
                        else {
                            Log.d(TAG, "Update Data Called");





                            String url_sendreminderdara = AllKeys.TAG_WEBSITE_HAPPY + "/InsertAnniversaryBdayReminder?action=insertreminder&reminderid=" + RECORD_ID + "&userid=" + userDetails.get(SessionManager.KEY_USERID) + "&type=" + dbhandler.convertEncodedString(txtDescr.getText().toString()) + "&description=" + dbhandler.convertEncodedString(txtDescr.getText().toString()) + "&date=" + dbhandler.convertToJsonDateFormat(txtDate.getText().toString()) + "&time=" + dbhandler.convertEncodedString(txtTime.getText().toString()) + "";
                            Log.d(TAG, "URL Send Reminder Update " + url_sendreminderdara);

                            StringRequest str_insertReminder = new StringRequest(Request.Method.GET, url_sendreminderdara, new Response.Listener<String>() {
                                @Override

                                public void onResponse(String response) {

                                    Log.d(TAG, "Update Reminder Response : " + response.toString());


                                    if (response.equals("1")) {



                                        ContentValues cv = new ContentValues();


                                        cv.put(dbhandler.REMINDER_Descr, txtDescr.getText().toString());
                                        cv.put(dbhandler.REMINDER_DATE, txtDate.getText().toString());
                                        cv.put(dbhandler.REMINDER_TIME, txtTime.getText().toString());


                                        //cv.put(dbhandler.TEMPLATE_ID, RECORD_ID);
                                        Log.d(TAG, "Reminder Master Update Data : " + cv.toString());

                                        //sd.insert(dbhandler.TABLE_TEMPALTE_MASTER, null, cv);
                                        sd.update(dbhandler.TABLE_REMINDER, cv, dbhandler.REMINDER_ID + " =" + RECORD_ID, null);


                                        Snackbar.make(coordinateLayout, "Reminder has been updated", Snackbar.LENGTH_SHORT).show();


                                        txtDescr.setText("");
                                        txtDate.setText("");
                                        txtTime.setText("");


                                        FillDataOnRecyclerView();


                                        lladddata.setVisibility(View.GONE);
                                        llshowdata.setVisibility(View.VISIBLE);
                                        menu_show.setVisible(false);
                                        menu_sync.setVisible(true);
                                        btnSaveData.setText("save data");
                                        fab.setVisibility(View.VISIBLE);



                                    } else {
                                        Snackbar.make(coordinateLayout, "Sorry, Reminder not updated. Try Again", Snackbar.LENGTH_SHORT).show();
                                    }



                                    hideDialog();

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.d(TAG, "Error Updating Reminder Data : " + error.getMessage());
                                    Snackbar.make(coordinateLayout, "Sorry, Reminder not updated. Try Again", Snackbar.LENGTH_SHORT).show();


                                    hideDialog();


                                }
                            });

                            MyApplication.getInstance().addToRequestQueue(str_insertReminder);



                        }//Close update data if condition


                    }//Close check net connectivity condition
                    else {
                        checkInternet();

                    }

                }

            }
        });


        FillDataOnRecyclerView();

        /**
         * RecyclerView Swiping Gesture
         */
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recycler_view_reminders,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {


                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    /*listMessageTemplate.remove(position);
                                    adapter.notifyItemRemoved(position);*/

                                    //                             Toast.makeText(context, "Left Detected", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Left Detected");

                                    /**
                                     * Get Current Reminder Details edit on Controls
                                     */
                                    ReminderData rd = listRemidners.get(position);
                                    Log.d(TAG, "Edited Reminder Data : " + rd.toString());

                                    RECORD_ID = rd.getReminder_id();


                                    txtDate.setText(rd.getReminder_date());
                                    txtDescr.setText(rd.getReminder_desr());
                                    txtTime.setText(rd.getReminder_time());


                                    lladddata.setVisibility(View.VISIBLE);
                                    llshowdata.setVisibility(View.GONE);
                                    menu_show.setVisible(true);
                                    menu_sync.setVisible(false);
                                    btnSaveData.setText("update data");
                                    fab.setVisibility(View.GONE);


                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {

                                    Toast.makeText(context, "Right Detected", Toast.LENGTH_SHORT).show();


                                    Log.d(TAG ,"Right Detected");


                                    if(NetConnectivity.isOnline(context))
                                    {
                                        showDialog();



                                        String url_deleteReminder = AllKeys.TAG_WEBSITE_HAPPY+"/DeleteAnniversaryBdayReminder?action=deletereminder&reminderid="+ listRemidners.get(position).getReminder_id() +"&userid="+ userDetails.get(SessionManager.KEY_USERID) +"";
                                        Log.d(TAG , "URL Delete Reminder : "+url_deleteReminder);
                                        StringRequest str_deleterequest =new StringRequest(Request.Method.GET, url_deleteReminder, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                Log.d(TAG , "Delete Reminder Response : "+response.toString());

                                                if(response.equals("1"))
                                                {


                                                    sd.delete(dbhandler.TABLE_REMINDER, dbhandler.REMINDER_ID + " = " + listRemidners.get(position).getReminder_id(), null);
                                                    listRemidners.remove(position);
                                                    adapter.notifyItemRemoved(position);
                                                }
                                                else
                                                {
                                                    Snackbar.make(coordinateLayout,"Sorry, Reminder has not deleted .Try again...",Snackbar.LENGTH_SHORT).show();

                                                }


                                                hideDialog();
                                                Snackbar.make(coordinateLayout,"Reminder has been deleted",Snackbar.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Log.d(TAG , "Error in deleting Template : "+error.getMessage());

                                                hideDialog();
                                                Snackbar.make(coordinateLayout,"Sorry, Reminder has not deleted .Try again...",Snackbar.LENGTH_SHORT).show();

                                            }
                                        });
                                        MyApplication.getInstance().addToRequestQueue(str_deleterequest);



                                    }
                                    else
                                    {
                                        checkInternet();
                                    }










                                }
                                adapter.notifyDataSetChanged();
                            }
                        });


        recycler_view_reminders.addOnItemTouchListener(swipeTouchListener);


    }
    //OnCreate completed

    /**
     * Fill data on recycler view
     */
    private void FillDataOnRecyclerView() {

        String query_selectall_reminders = "Select * from " + dbhandler.TABLE_REMINDER + "";
        Log.d(TAG, "Query Select All Templates : " + query_selectall_reminders);
        Cursor cur_selectalldata = sd.rawQuery(query_selectall_reminders, null);
        Log.d(TAG, "Total Records :" + cur_selectalldata.getCount() + " " + dbhandler.TABLE_REMINDER);
        if (cur_selectalldata.getCount() > 0) {
            listRemidners.clear();

            while (cur_selectalldata.moveToNext()) {


                //ReminderData(String reminder_desr, String reminder_date, String reminder_time, int reminder_id) {
                ReminderData RD = new ReminderData(cur_selectalldata.getString(cur_selectalldata.getColumnIndex(dbhandler.REMINDER_Descr)), cur_selectalldata.getString(cur_selectalldata.getColumnIndex(dbhandler.REMINDER_DATE)), cur_selectalldata.getString(cur_selectalldata.getColumnIndex(dbhandler.REMINDER_TIME)), cur_selectalldata.getInt(cur_selectalldata.getColumnIndex(dbhandler.REMINDER_ID)));

                listRemidners.add(RD);


            }


            recycler_view_reminders.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }


    }

//Compelte Filling DAta

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {

            this.menu = menu;
            getMenuInflater().inflate(R.menu.common_sync, menu);
            // sync_data = (MenuItem) menu.findItem(R.id.action_sync);


            menu_sync = (MenuItem) menu.findItem(R.id.common_sync);
            menu_show = (MenuItem) menu.findItem(R.id.common_show);

            menu_sync.setVisible(true);
            menu_show.setVisible(false);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(getApplicationContext(),
                    DashBoardActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

        } else if (item.getItemId() == R.id.common_show) {

            fab.setVisibility(View.VISIBLE);
            lladddata.setVisibility(View.GONE);
            llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(true);
            menu_show.setVisible(false);


            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        } else if (item.getItemId() == R.id.common_sync) {


            //fab.setVisibility(View.VISIBLE);
            lladddata.setVisibility(View.GONE);
            llshowdata.setVisibility(View.VISIBLE);
            menu_sync.setVisible(true);
            menu_show.setVisible(false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        Intent intent = new Intent(getApplicationContext(),
                DashBoardActivity.class);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }


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

        txtTime.setText(aTime);
    }


    public void checkInternet() {
        Snackbar snackbar = Snackbar
                .make(coordinateLayout, "" + getString(R.string.no_network), Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NetConnectivity.isOnline(context)) {
                            Intent intent = new Intent(context, RemindersAcivity.class);
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


}
