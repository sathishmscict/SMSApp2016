package com.tech9teen;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tech9teen.R;
import com.tech9teen.database.dbhandler;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Satish Gadde on 02-09-2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private SessionManager sessionamanger;
    private HashMap<String, String> userDetails = new HashMap<String, String>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        try {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "From: " + remoteMessage.getFrom());

            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().get("notification"));

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG , "Error FCM : "+e.getMessage());
        }

        Log.d(TAG, "Received message");

        sessionamanger = new SessionManager(this);
        userDetails = sessionamanger.getUserDetails();

        dbhandler db = new dbhandler(this);
        SQLiteDatabase sd = db.getWritableDatabase();





        String message = remoteMessage.getData().get("message");
        String title = remoteMessage.getData().get("title");
        String img_url = remoteMessage.getData().get("image");
        String type = remoteMessage.getData().get("type");

        String intenttype = ""+remoteMessage.getData().get("intenttype");

        String screentype = "";
        String webfileurl = "";


        //Random r = new Random();
        //int max_notificationid = r.nextInt(9999 - 1000) + 1000;
        int  max_notificationid = 0;
        try {
            max_notificationid = Integer.parseInt(remoteMessage.getData().get("notificationid"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        max_notificationid=7;

        Log.d("Max Id ", " Of Notification : " + max_notificationid);

        System.out.println("MESSAGE : " + message + " TITLE :" + title + " Image URL : " + img_url + " Type : " + type + " Intent Type : " + intenttype + " Screen Type : " + screentype + " webfileurl :" + webfileurl+" NotificationID : "+max_notificationid);
        Log.d("Notification Data : ", "MESSAGE : " + message + " TITLE :" + title + " Image URL : " + img_url + " Type : " + type + " Intent Type : " + intenttype + " Screen Type : " + screentype + " webfileurl :" + webfileurl);
        String cdate = getDateTime();


        try {
            if (intenttype.equals("")) {


                String query = "delete from Notification_Mst where notification ='" + message + "' and ndate='" + cdate + "'";
                Log.d("Notifi Delete Query : ", query);
                sd.execSQL(query);
                query = "insert into Notification_Mst values(null,'" + title
                        + "','" + message + "','" + cdate + "')";
                Log.d("Notifi Insert Query : ", query);

                sd.execSQL(query);

                System.out.print("Current Date : " + cdate);
                System.out.print("Notification Query : " + query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // String dismessage = title + "GSS" + message;
        // displayMessage(context, dismessage);

        // notifies user

        // dbhandler db=new dbhandler(context);
        // SQLiteDatabase sd=db.getWritableDatabase();
        // sd.execSQL("INSERT into notificationmsg values('','"+message+"')");
        //displayMessage(context, message);
        // notifies user
        // generateNotification(context, message);
        sendNotification(message, title, intenttype, type, img_url, max_notificationid, screentype, webfileurl);


        //Calling method to generate notification
       // sendNotification(remoteMessage.getNotification().getBody());
    }





    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String message,
                                    String title, String intenttype, String type, String img_url, int notification_id, String ScreenType, String webfile) {
        /*Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Push Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());*/

        /**
         * Simple Notification
         */


        Intent notificationIntent = null;
        try {
            if (intenttype.toLowerCase().equals("")) {
                notificationIntent = new Intent(this, MenuActivity.class);

            }
            notificationIntent = new Intent(this, MenuActivity.class);


/*
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
*/

            //BigTextStyle, InboxStyle,BigPictureStyle,MediaStyle

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setAutoCancel(true);


            String textMessage = "" + message;

            //Set Sound
            builder.setSound(
                    RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


            //Set Vibration
            //Vibration
            builder.setVibrate(new long[]{300, 300, 200, 300});


            //Set Color
            int color = getResources().getColor(R.color.colorPrimaryDark);
            builder.setColor(color);

            //Set Profile picture
            Drawable drawable = this.getResources().getDrawable(R.mipmap.ic_launcher);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            builder.setLargeIcon(bitmap);


            //Set Intent to notification action
/*        int mNotificationId = 001;*/
/*
        Intent notificationIntent = new Intent(context,
                NotificationActivity.class);
*/


            PendingIntent resultPendingIntent = null;
            try {
                resultPendingIntent = PendingIntent.getActivity(this,
                        0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            } catch (Exception e) {
                e.printStackTrace();
            }


            builder.setContentIntent(resultPendingIntent);

            builder.addAction(R.drawable.icon_notification, "Show", resultPendingIntent);
            builder.setAutoCancel(true);
            builder.setWhen(0);

            builder.setContentText(textMessage);


            //Set BigText Style  Display as multiline notification
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(textMessage));


            //Set BigPictureStyle ,Display Image as notification
            Bitmap image = null;
            if (type.equals("Image")) {
                try {
                    URL url = new URL(img_url);
                    Log.d("Image URL : ", "" + img_url);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            builder.setAutoCancel(true);

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(title)
                    .setContentText(textMessage);


            Notification notification1 = builder.build();


            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notification_id);
            notificationManager.notify(notification_id, notification1);
            //builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        }
        catch (Exception e)
        {
            Log.d(TAG , "Firebase Notification error : "+e.getMessage());
        }

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault());

        Date date = new Date();
        return dateFormat.format(date);
    }

}
