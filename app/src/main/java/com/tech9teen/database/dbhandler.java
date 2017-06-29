package com.tech9teen.database;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import com.tech9teen.MenuActivity;
import com.tech9teen.R;
import com.tech9teen.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Satish Gadde on 07-09-2016.
 */
public class dbhandler extends SQLiteOpenHelper {

    public static final String dbname = "smsapp.db";
    private static final int dbversion = 18;

    private static String TAG = dbhandler.class.getSimpleName();

    private String query;


    //Calllog Master Related Keys
    public static final String TABLE_CALLOGMASTER = "CalllogMaster";
    public static final String CALLOG_ID = "CallogId";
    public static final String CALLOG_DATE = "Date";
    public static final String CALLOG_TIME = "Time";
    public static final String CALLOG_MOBILE = "MobileNumber";
    public static final String CALLOG_NAME = "ContactNAME";
    public static final String CALLOG_DURATION = "CallDuration";
    public static final String CALLOG_CALLTYPE = "CallType";


    /**
     * MessageLog Master
     */
    public static final String TABLE_MESSAGELOGMASTER = "MessageLogMaster";
    public static final String MESSAGELOG_ID = "Id";
    public static final String MESSAGELOG_BODY = "MessageBody";
    public static final String MESSAGELOG_ADDRESS = "Address";
    public static final String MESSAGELOG_NAME = "NAme";
    public static final String MESSAGELOG_DATE = "Date";
    public static final String MESSAGELOG_TIME = "Time";
    public static final String MESSAGELOG_TYPE = "Type";
    public static final String MESSAGELOG_SENDERID = "SenderId";



    /**
     * ContactMaster Related Keys
     */
    public static final String TABLE_CONTACTMASTER = "ContactMaster";
    public static final String CONTACTMASTER_ID = "ContactId";
    public static final String CONTACTMASTER_NAME = "ContactName";
    public static final String CONTACTMASTER_MOBILE_NO = "ContactMobileNo";




    /**
     * SMS CategoryMaster Related Keys
     */
    public static final String TABLE_SMS_CATEGORY_MASTER = "SMSCategoryMaster";
    public static final String SMS_CATEGORY_MASTER_ID = "CategotyID";
    public static final String SMS_CATEGORY_MASTER_NAME = "CategotyName";


    /**
     * SMS SubCategoryMaster Related Keys
     */
    /*public static final String TABLE_SMS_SUBCATEGORY_MASTER = "SenderMaster";
    public static final String SMS_SUBCATEGORY_MASTER_ID = "ID";
    public static final String SMS_SUBCATEGORY_MASTER_CATEGORYID = "CategoryId";
    public static final String SMS_SUBCATEGORY_MASTER_NAME = "SenderIdName";
    */

    public static final String TABLE_SENDERID_MASTER = "SenderMaster";
    public static final String SMS_SENDERID_ID = "ID";
    public static final String SMS_SENDERID_CATEGORYID = "CategoryId";
    public static final String SMS_SENDERID_NAME = "SenderIdName";
    //public static final String SMS_SUBCATEGORY_MASTER_TYPE = "Type";




    /**
     * SMS Template Master Related Keys
     */
    public static final String TABLE_TEMPALTE_MASTER = "TemplateMaster";
    public static final String TEMPLATE_ID = "TemplateId";
    public static final String TEMPLATE_TITLE = "TemplateTitle";
    public static final String TEMPLATE_DESCR = "TemplateDescr";

    /**
     * Group Master Related Keys
     */
    public static final String TABLE_GROUPMASTER = "GroupMaster";
    public static final String GROUPMASTER_ID = "GroupMasterID";
    public static final String GROUPMASTER_NAME = "GroupMasterName";
    public static final String GROUPMASTER_CONTACTS = "NoOfContacts";


    /**
     * Group Master Under contacts Related Keys
     */
    public static final String TABLE_GROUPMASTERDET = "GroupMasterDetails";
    public static final String GROUPMASTER_DET_ID = "GroupMasterDetailsID";
    public static final String GROUPMASTER_DET_GROUPMASTER_ID = "GroupMasterID";
    public static final String GROUPMASTER_DET_MOBILENUMBER = "GroupMasterDetailsMobileNumber";
    public static final String GROUPMASTER_DET_CONTACTNAME = "GroupMasterDetailsContactName";
    public static final String GROUPMASTER_DET_CONTACTID = "GroupMasterDetailsContactID";

    /**
     * Reminder Master Relted Keys
     */
    public static final String TABLE_REMINDER = "ReminderMaster";
    public static final String REMINDER_ID = "ReminderId";
    public static final String REMINDER_Descr = "ReminderDescr";
    public static final String REMINDER_DATE = "Date";
    public static final String REMINDER_TIME = "Time";
    public static final String REMINDER_STATUS = "Status";

    /**
     * get SMSSettings Related Keys
     */
    public static final String TABLE_SMSSETTINGS = "SMSSettingsMaster";
    public static final String SMSSETTINGS_ID = "Id";
    public static final String SMSSETTINGS_TYPE = "Type";
    public static final String SMSSETTINGS_SENDERID = "SenderId";
    public static final String SMSSETTINGS_USERNAME = "Username";
    public static final String SMSSETTINGS_PASSWORD = "Password";

    /**
     *Upload Documents Related Table
     */
    public static final String TABLE_UPLOADDOC = "UploadDocMaster";
    public static final String UPLOADDOC_ID = "Id";
    public static final String UPLOADDOC_SENDERID = "SenderId";
    public static final String UPLOADDOC_TYPE = "Type";
    public static final String UPLOADDOC_PHOTO = "Photo";
    public static final String UPLOADDOC_DOCUMENT = "Document";
    public static final String UPLOADDOC_IDPROOF = "IdProof";
    public static final String UPLOADDOC_IDPROOF2 = "ExtraIdProof";
    public static final String UPLOADDOC_STATUS = "Status";
    public static final String UPLOADDOC_APPROVAL_DATE = "ApprovalDate";
    public static final String UPLOADDOC_REQUEST_DATE = "RequestDate";








    public dbhandler(Context context) {
        super(context, dbname, null, dbversion);
    }


    @Override
    public void onCreate(SQLiteDatabase sd) {

        try {
            query = "CREATE TABLE  IF NOT EXISTS " + TABLE_CONTACTMASTER + " (" + CONTACTMASTER_ID + " INTEGER PRIMARY KEY, " + CONTACTMASTER_NAME + " TEXT," + CONTACTMASTER_MOBILE_NO + " TEXT) ";
            Log.d(TAG, "Query " + TABLE_CONTACTMASTER + " " + query);
            sd.execSQL(query);


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_SMS_CATEGORY_MASTER + "(" + SMS_CATEGORY_MASTER_ID + " INTEGER PRIMARY KEY ," + SMS_CATEGORY_MASTER_NAME + " TEXT)";
            Log.d(TAG, "Query " + TABLE_SMS_CATEGORY_MASTER + " " + query);
            sd.execSQL(query);





            //query = "CREATE TABLE IF NOT EXISTS " + TABLE_SMS_SUBCATEGORY_MASTER + "(" + SMS_SUBCATEGORY_MASTER_ID + " INTEGER PRIMARY KEY," + SMS_SUBCATEGORY_MASTER_CATEGORYID + " INTEGER," + SMS_SUBCATEGORY_MASTER_NAME + " TEXT)";
            query = "CREATE TABLE IF NOT EXISTS " + TABLE_SENDERID_MASTER + "(" + SMS_SENDERID_ID + " INTEGER PRIMARY KEY," + SMS_SENDERID_CATEGORYID + " INTEGER," + SMS_SENDERID_NAME + " TEXT)";
            Log.d(TAG, "Query " + TABLE_SENDERID_MASTER + " " + query);
            sd.execSQL(query);


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_TEMPALTE_MASTER + " (" + TEMPLATE_ID + " INTEGER PRIMARY KEY , " + TEMPLATE_TITLE + " TEXT, " + TEMPLATE_DESCR + " TEXT)";
            Log.d(TAG, "Query " + TABLE_TEMPALTE_MASTER + "  " + query);
            sd.execSQL(query);


            query = "CREATE TABLE  IF NOT EXISTS " + TABLE_GROUPMASTER + " (" + GROUPMASTER_ID + " INTEGER PRIMARY KEY , " + GROUPMASTER_NAME + " TEXT," + GROUPMASTER_CONTACTS + " INTEGER)";
            Log.d(TAG, "Query " + TABLE_GROUPMASTER + "  " + query);
            sd.execSQL(query);


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_GROUPMASTERDET + " (" + GROUPMASTER_DET_ID + " INTEGER PRIMARY KEY," + GROUPMASTER_DET_GROUPMASTER_ID + " INTEGER," + GROUPMASTER_DET_MOBILENUMBER + " TEXT," + GROUPMASTER_DET_CONTACTNAME + " TEXT," + GROUPMASTER_DET_CONTACTID + " INTEGER)";
            Log.d(TAG, "Query " + TABLE_GROUPMASTERDET + "  " + query);
            sd.execSQL(query);


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_REMINDER + "(" + REMINDER_ID + " INTEGER PRIMARY KEY , " + REMINDER_Descr + " TEXT," + REMINDER_DATE + " TEXT," + REMINDER_TIME + " TEXT," + REMINDER_STATUS + " TEXT)";
            Log.d(TAG, "Query " + TABLE_REMINDER + "  " + query);
            sd.execSQL(query);


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_SMSSETTINGS + " (" + SMSSETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + SMSSETTINGS_TYPE + " TEXT," + SMSSETTINGS_SENDERID + " TEXT," + SMSSETTINGS_USERNAME + " TEXT," + SMSSETTINGS_PASSWORD + " TEXT)";
            Log.d(TAG, "Query " + TABLE_SMSSETTINGS + "  " + query);
            sd.execSQL(query);


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGELOGMASTER + "(" + MESSAGELOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MESSAGELOG_BODY + " TEXT,"+ MESSAGELOG_ADDRESS +" TEXT, " + MESSAGELOG_NAME + " TEXT," + MESSAGELOG_DATE + " TEXT," + MESSAGELOG_TIME + " TEXT," + MESSAGELOG_TYPE + " TEXT ,"+ MESSAGELOG_SENDERID +" INTEGER)";
            Log.d(TAG, "Query " + TABLE_MESSAGELOGMASTER + "  " + query);
            sd.execSQL(query);




            query = "CREATE TABLE IF NOT EXISTS "+ TABLE_UPLOADDOC +"("+ UPLOADDOC_ID +" INTEGER PRIMARY KEY , "+ UPLOADDOC_SENDERID +" TEXT,"+ UPLOADDOC_TYPE +" TEXT,"+ UPLOADDOC_PHOTO +" TEXT,"+ UPLOADDOC_DOCUMENT +" TEXT,"+ UPLOADDOC_IDPROOF +" TEXR,"+ UPLOADDOC_IDPROOF2 +" TEXT," + UPLOADDOC_STATUS +" TEXT,"+ UPLOADDOC_APPROVAL_DATE +" TEXT,"+ UPLOADDOC_REQUEST_DATE +" TEXT)";
            Log.d(TAG, "Query " + TABLE_UPLOADDOC+ "  " + query);
            sd.execSQL(query);


            query = "CREATE TABLE IF NOT EXISTS " + TABLE_CALLOGMASTER + "(" + CALLOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CALLOG_DATE + " TEXT," + CALLOG_TIME + " TEXT," + CALLOG_MOBILE + " TEXT," + CALLOG_NAME + " TEXT," + CALLOG_DURATION + " INTEGER," + CALLOG_CALLTYPE + " TEXT)";
            Log.d(TAG, "Query " + TABLE_CALLOGMASTER + "  " + query);
            sd.execSQL(query);


        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error in Table Creation :" + e.getMessage());
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {


/*


*/
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGELOGMASTER);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SENDERID_MASTER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_MESSAGELOGMASTER);



        sqLiteDatabase.setVersion(newVersion);
        onCreate(sqLiteDatabase);


    }


    /**
     * Insert data in any table
     *
     * @param sd
     * @param tablename
     * @param cv
     */
    public static void InsertData(SQLiteDatabase sd, String tablename, ContentValues cv) {

        try {
            Log.d(TAG, "Table Name :" + tablename);
            Log.d(TAG, "Table Data :" + cv.toString());
            sd.insert(tablename, null, cv);
        } catch (Exception e) {
            e.printStackTrace();

            Log.d(TAG, "Error Insert  : " + e.getMessage());

        }

    }

    /**
     * Get Database backup
     */
    public void backupDB(Context context) {

        boolean result = Utility.checkPermission_WriteExternalStorage(context);
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + context.getPackageName() + "/databases/"
                + dbhandler.dbname;
        String backupDBPath = dbhandler.dbname;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            //Toast.makeText(context, "DataBase Exported!",Toast.LENGTH_LONG).show();
            Log.d("DatabaseBackup : ", "DataBase Exported!");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Backup Error :" + e.getMessage());
        }
    }


    /**
     * @param sd
     * @param tablename
     */
    public static void DeleteTableData(SQLiteDatabase sd, String tablename) {
        Log.d(TAG, "Table Name :" + tablename);
        sd.delete(tablename, null, null);
        Log.d(TAG, "Table : " + tablename + " has been deleted");


    }

    /**
     * Check Permission
     *
     * @param grantResults
     * @return
     */

    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

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
    public static int dpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @SuppressLint("NewApi")
    public static void Notify(String title, String textMessage, Context context) {
        try {
            /**
             * Simple Notification
             */

            //BigTextStyle, InboxStyle,BigPictureStyle,MediaStyle
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            //NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setAutoCancel(true);


            //builder.setAutoCancel(true);

            //Set Sound
            builder.setSound(
                    RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            //Set Vibration
            //Vibration
            builder.setVibrate(new long[]{300, 300, 200, 300});


            //Set Color
            int color = context.getResources().getColor(R.color.colorPrimaryDark);
            builder.setColor(color);

            //Set Profile picture
            Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            builder.setLargeIcon(bitmap);


            //Set Intent to notification action
            int mNotificationId = 1;
            Intent notificationIntent = null;
            try {

                notificationIntent = new Intent(context,
                        MenuActivity.class);

                /*notificationIntent.putExtra("rowid", mNotificationId);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);*/

           /*     Log.d("Activation Status : "  , ""+users.get(SessionManager.KEY_ACTSTATUS));
                users= session.getUserDetails();

                if(users.get(SessionManager.KEY_ACTSTATUS).equals("0"))
                {
                    notificationIntent = new Intent(context,
                            MainActivity.class);
                }
                else
                {

                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                    mNotificationId, notificationIntent, 0);

            builder.setContentIntent(resultPendingIntent);
            builder.addAction(R.drawable.icon_notification, "Show", resultPendingIntent);
            builder.setAutoCancel(true);
            /*builder.setWhen(0);*/

            builder.setContentText(textMessage);

            //Set BigText Style  Display as multiline notification
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(textMessage));

/*
            Bitmap image = null;
            try {

                URL url = new URL("https://www.delta.edu/images/gps/World.JPG");
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            //Set BigPictureStyle ,Display Image as notification
//            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));


            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(title)
                    .setContentText(textMessage);
            //builder.setContentIntent(resultPendingIntent);


            Notification notification1 = builder.build();


/*
            NotificationManagerCompat.from(this).notify(mNotificationId, notification1);
            notification1.flags |= Notification.FLAG_AUTO_CANCEL;
*/


            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(mNotificationId);
            notificationManager.notify(mNotificationId, notification1);
            // notification1.flags |= Notification.FLAG_AUTO_CANCEL;


            /**
             * Complete Simple Notification
             *
             */


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static final String convertToJsonDateFormat(String cur_date) {

        Log.d("Passed Date : ", cur_date);
        SimpleDateFormat dateFormat = null;
        Date date = null;
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault());

//String string = "January 2, 2010";
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            date = format.parse(cur_date);
            System.out.println(date);
        } catch (Exception e) {
            Log.d("Convert DataFormat :: ", e.getMessage());
        }


        //Date date = new Date();

        return dateFormat.format(date);


    }


    public static final String convertToAppDateFormat(String cur_date) {

        Log.d("Passed Date : ", cur_date);
        SimpleDateFormat dateFormat = null;
        Date date = null;
        try {
            dateFormat = new SimpleDateFormat("dd-MM-yyyy",
                    Locale.getDefault());

//String string = "January 2, 2010";
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            date = format.parse(cur_date);
            System.out.println(date);
        } catch (Exception e) {
            Log.d("Convert DataFormat :", e.getMessage());
        }


        //Date date = new Date();

        return dateFormat.format(date);


    }

    //Methods
    public static String convertToJsonFormat(String json_data) {

        String response = "{\"data\":" + json_data + "}";
        return response;

    }

    public static String convertEncodedString(String str) {
        String enoded_string = null;
        try {
            enoded_string = URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return enoded_string;
    }


    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d("Encoded String : ", encodedImage);
        return encodedImage;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        Log.d("Decoded String   : ", "" + BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Get File PAth From URI
     */
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content" .equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file" .equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isValidEmail(EditText argEditText) {

        try {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher matcher = pattern.matcher(argEditText.getText());
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
