package com.tech9teen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.tech9teen.R;
import com.tech9teen.database.dbhandler;

/**
 * Created by Satish Gadde on 19-09-2016.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {

        String defaultPath = Settings.System.DEFAULT_NOTIFICATION_URI.getPath();

        Uri defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(context.getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        Ringtone defaultRingtone = RingtoneManager.getRingtone(context, defaultRintoneUri);


        mp=MediaPlayer.create(context,defaultRintoneUri);


        mp.start();



        Log.d("Intent Data " , "Data : "+intent.getStringExtra("SATISH")+"Alaram ID : "+intent.getStringExtra("ALARAMID"));
        Intent ii = new Intent(context , DisplayReminder.class);
        ii.putExtra("SATISH",intent.getStringExtra("SATISH"));
        ii.putExtra("ALARAMID" , intent.getStringExtra("ALARAMID"));
        ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(ii);



        dbhandler.Notify(context.getString(R.string.app_name), ""+intent.getStringExtra("SATISH") , context);




        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
    }


    public  void stopAlaram()
    {

        try {
            if(mp.isPlaying())
            {
            mp.stop();

            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
