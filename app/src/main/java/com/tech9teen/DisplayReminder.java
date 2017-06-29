package com.tech9teen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tech9teen.R;

public class DisplayReminder extends AppCompatActivity {

    private Button btnCancel;
    private TextView txtDescr;
    private int alaramid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_reminder);

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

        btnCancel = (Button) findViewById(R.id.btnCancel);
        txtDescr = (TextView) findViewById(R.id.txtDescr);


        Intent ii = getIntent();
        String descr = ii.getStringExtra("SATISH");
        String id = ii.getStringExtra("ALARAMID");
        Log.d("Alaram Title  :", descr);
        Log.d("Alaram Id : ", String.valueOf(id));
        alaramid = Integer.parseInt(id);

        txtDescr.setText("Data" + descr);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                onStop();



                /*PendingIntent alarmIntent;
                alarmIntent = PendingIntent.getBroadcast(DisplayReminder.this, alaramid,
                        new Intent(DisplayReminder.this, MenuActivity.class),
                        PendingIntent.FLAG_CANCEL_CURRENT);
                //cancelAlarm(alarmIntent);



                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                alarmManager.cancel(alarmIntent);
*/


/*

                Intent myIntent = new Intent(getBaseContext(),
                        MyScheduledReceiver.class);

                PendingIntent pendingIntent
                        = PendingIntent.getBroadcast(getBaseContext(),
                        0, myIntent, 0);

                AlarmManager alarmManager
                        = (AlarmManager)getSystemService(ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);

                Intent intent = new Intent();
                intent.setClass(MyScheduledActivity.this,
                        AndroidScheduledActivity.class);
                startActivity(intent);
                finish();

*/


                try {
                    MyBroadcastReceiver mb = new MyBroadcastReceiver();
                    mb.stopAlaram();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                finish();


                Intent myIntent = new Intent(getBaseContext(),

                        MyBroadcastReceiver.class);

                PendingIntent pendingIntent
                        = PendingIntent.getBroadcast(getBaseContext(),
                        alaramid, myIntent, 0);

                AlarmManager alarmManager
                        = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);


                Intent ii = new Intent(DisplayReminder.this, MenuActivity.class);
                ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(ii);

                finish();
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);


            }
        });


    }

}
