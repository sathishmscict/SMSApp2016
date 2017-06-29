package com.tech9teen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "SMSAppPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // All Shared Preferences Keys
    private static final String IS_ACTIVATED = "IsActivated";


    public static final String KEY_ACTIVITYNAME = "ActivityName",KEY_DEFAULT_SENDERID="DefaultSenderId",KEY_DEFAULT_SENDERID_POS="DefaultSenderIdPos";


    //savemenukey
    public static final String KEY_MENUKEY = "menukey",
            KEY_RECEIVECODE = "reccode", /*KEY_ACT_STATUS = "actstatus",*/
            KEY_VERSTATUS = "verification_status";
    public static final String KEY_LOGINTYPE = "logintype";
    public static final String KEY_PUSH_TITLE = "push_title";
    public static final String KEY_PUSH_DESCR = "push_descr";
    public static final String KEY_PUSH_DATE = "push_date";
    public static final String KEY_ENODEDED_STRING = "enoded_string", KEY_STATUS = "status", KEY_NEWUSER = "IsNewUSer", KEY_USERTYPE = "UserType";


    public static final String KEY_DOLLOR_PRICE = "dollorRate", KEY_GOLD_RATE = "goldRate", KEY_DAY_OF_MONTH = "dayofmonth";


    //save gmail details
    //public static final String KEY_PROFILE_NAME="name";
    public static final String KEY_PROFILE_PIC_URL = "imageurl";
    //public static final String KEY_PROFILE_EMAIL="email";


    //save fb details
    /*public static final String KEY_FB_NAME="name";
    public static final String KEY_FB_URL="imageurl";
    public static final String KEY_FB_EMAIL="email";*/

    //maintain FACKBOOK AND GMAIL KEY WITH USER CAN LOGIN
    public static final String KEY_LOGIN_KEY = "LoginType";
    public static final String KEY_ISGUJARATI_MENU = "IsGujarati";


    public static final String KEY_CURRENT_DATE = "current_date",
            KEY_WEBSITE = "website";

    // Check For Activation
    public static final String KEY_CODE = "code", KEY_SMSURL = "smsurl";

    public static final String KEY_DEVICEID = "DeviceID",
            KEY_VERIFICATION_COUNTER = "ver_counter";

    public static final String KEY_LATTITUDE = "lattitude",
            KEY_LONGTITUDE = "longttude", KEY_CONTACT_ADDRESS = "contact_address", KEY_CONTACT_EMAIL = "contact_email", KEY_CONTACT_PHONE = "contact_phone";
    ;
    public static final String KEY_USER_MOBILE = "mobileno", KEY_USERID = "UserId", KEY_DOB = "DOB", KEY_DOA = "DOA", KEY_REF_POINTS = "ReferralPoints", KEY_REF_CODE = "ReferralCode", KEY_USERNAME = "UserName";


    public static final String KEY_MESSAGETYE = "MessageType";


    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MESSAGE_GROUPID = "MessageGroupId";
    public static final String KEY_COUNT_PREVIOUS_MESSAGE = "PreviousMessageCount";
    public static final String KEY_COUNT_PREVIOUS_CONTACTS = "ContactsCount";
    public static final  String KEY_DEFAULT_ROUTETYPE="DefaultRoute",KEY_BALANCE_REMINDER="BalanceReminder";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void StorePushNotification(String title, String descr, String dtime) {
        editor.putString(KEY_PUSH_TITLE, title);
        editor.putString(KEY_PUSH_DESCR, descr);

        editor.putString(KEY_PUSH_DATE, dtime);
        editor.commit();
    }


    /* public void setLoginType(String key)
     {
         editor.putString(KEY_LOGINTYPE,key);
         editor.commit();
     }*/
    public void createUserSendSmsUrl(String code, String websiteurl) {

        editor.putString(KEY_CODE, code);
        editor.putString(KEY_SMSURL, websiteurl);// http://radiant.dnsitexperts.com/JSON_Data.aspx?type=otp&mobile=9825681802&code=7692
        editor.commit();

    }

    public void CheckSMSVerificationNoActivity(String reccode, String actstatus) {

        editor.putString(KEY_RECEIVECODE, reccode);
        editor.putString(KEY_VERSTATUS, actstatus);
        editor.commit();

    }


    public void setDollorAndGoldRateFromAPI(String dollor, String gold, String dayofmonth) {


        editor.putString(KEY_DOLLOR_PRICE, dollor);
        editor.putString(KEY_GOLD_RATE, gold);
        editor.putString(KEY_DAY_OF_MONTH, dayofmonth);
        editor.commit();

    }


    /**
     * Check login method wil check user login status If false it will redirect
     * user to login page Else won't do anything
     */
    public int checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            //Intent i = new Intent(_context, NewLoginActivity.class);
            Intent i = new Intent(_context, EasyIntroActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
            return 1;
        }
        return 0;

    }
    public void forgotpass(String userid, String mobileno, String newUser) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERID, userid);
        editor.putString(KEY_USER_MOBILE, mobileno);
        editor.putString(KEY_NEWUSER, newUser);
        // commit changes
        editor.commit();

    }
    public void clearuserid(String userid) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERID, userid);
        // commit changes
        editor.commit();

    }
    /**
     * Create login session
     */
    //public void createLoginSession(String name, String email,String logintype)
    public void createLoginSession(String userid, String username, String userEmail, String mobileno, String dob, String doa, String logintype, String refpoints, String refcode, String status, String newUser, String userType) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);


        editor.putString(KEY_USERID, userid);
        editor.putString(KEY_USER_MOBILE, mobileno);


        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_DOA, doa);
        editor.putString(KEY_LOGINTYPE, logintype);
        editor.putString(KEY_REF_POINTS, refpoints);
        editor.putString(KEY_REF_CODE, refcode);
        // Storing name in pref
        editor.putString(KEY_USERNAME, username);
        // Storing email in pref
        editor.putString(KEY_EMAIL, userEmail);
        editor.putString(KEY_LOGINTYPE, logintype);
        editor.putString(KEY_STATUS, status);

        editor.putString(KEY_NEWUSER, newUser);

        editor.putString(KEY_USERTYPE, userType);


        // commit changes
        editor.commit();
    }


    public void setEncodedImage(String encodeo_image) {


        editor.putString(KEY_ENODEDED_STRING, encodeo_image);

        editor.commit();
    }


    public void createsavegmaildetails(String name, String imageurl, String email) {//dee
        /**/

        editor.putString(KEY_USERNAME, name);
        editor.putString(KEY_PROFILE_PIC_URL, imageurl);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

   /* public void createsaveFBdetails(String name,String imageurl,String email)
    {//dee
        *//*editor.putBoolean(IS_LOGIN, true);*//*
        editor.putString(KEY_FB_NAME,name);
        editor.putString(KEY_FB_URL,imageurl);
        editor.putString(KEY_FB_EMAIL,email);
        editor.commit();
    }*/


    public void setMessageGroupId(String group_id) {

        editor.putString(KEY_MESSAGE_GROUPID, group_id);
        editor.commit();


    }


    /*public void checkloginornot()
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }*/
    public void createSAVEmenukey(String var) {
        editor.putString(KEY_MENUKEY, var);
        editor.commit();
    }


    /**
     * Create Activation session
     */


    public void createContactUsDetails(String lattitude, String longtitude,
                                       String address) {
        // Storing login value as TRUE
        // editor.putBoolean(IS_ACTIVATED, true);

        // Storing name in pref
        editor.putString(KEY_LATTITUDE, lattitude);
        editor.putString(KEY_LONGTITUDE, longtitude);
        editor.putString(KEY_CONTACT_ADDRESS, address);


        // commit changes
        editor.commit();
    }


    //useVerification Number//use in cityPlus
    public void setUserMobileNumber(String mobile) {
        // Storing login value as TRUE
        // Storing name in pref

        editor.putString(KEY_USER_MOBILE, mobile);

        // commit changes
        editor.commit();
    }


    public void CreateMenuLanguage(String lan) {
        editor.putString(KEY_ISGUJARATI_MENU, lan);
        editor.commit();


    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        /*user.put(KEY_NAME, pref.getString(KEY_NAME, null));*/


        user.put(KEY_ACTIVITYNAME , pref.getString(KEY_ACTIVITYNAME, ""));

        user.put(KEY_DEFAULT_SENDERID , pref.getString(KEY_DEFAULT_SENDERID , "STUDYF"));
        user.put(KEY_DEFAULT_SENDERID_POS , pref.getString(KEY_DEFAULT_SENDERID_POS , "0"));




        user.put(KEY_BALANCE_REMINDER , pref.getString(KEY_BALANCE_REMINDER , "false"));


        //Default Route as route3=Transactional  and route2 = Promotional
        user.put(KEY_DEFAULT_ROUTETYPE , pref.getString(KEY_DEFAULT_ROUTETYPE , "route3"));

        user.put(KEY_MESSAGETYE, pref.getString(KEY_MESSAGETYE, ""));

        user.put(KEY_COUNT_PREVIOUS_CONTACTS, pref.getString(KEY_COUNT_PREVIOUS_CONTACTS, "0"));
        user.put(KEY_COUNT_PREVIOUS_MESSAGE, pref.getString(KEY_COUNT_PREVIOUS_MESSAGE, "0"));
        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        user.put(KEY_CODE, pref.getString(KEY_CODE, "0"));

        user.put(KEY_SMSURL, pref.getString(KEY_SMSURL, ""));

        user.put(KEY_WEBSITE, pref.getString(KEY_WEBSITE, ""));
        user.put(KEY_DEVICEID, pref.getString(KEY_DEVICEID, ""));

        user.put(KEY_LATTITUDE, pref.getString(KEY_LATTITUDE, ""));
        user.put(KEY_LONGTITUDE, pref.getString(KEY_LONGTITUDE, ""));
        user.put(KEY_CONTACT_ADDRESS, pref.getString(KEY_CONTACT_ADDRESS, ""));


        user.put(KEY_CONTACT_EMAIL, pref.getString(KEY_CONTACT_EMAIL, ""));
        user.put(KEY_CONTACT_PHONE, pref.getString(KEY_CONTACT_PHONE, ""));


        user.put(KEY_PUSH_DESCR, pref.getString(KEY_PUSH_DESCR, ""));
        user.put(KEY_PUSH_TITLE, pref.getString(KEY_PUSH_TITLE, ""));

        user.put(KEY_STATUS, pref.getString(KEY_STATUS, "0"));

        user.put(KEY_PUSH_DATE, pref.getString(KEY_PUSH_DATE, ""));

        user.put(KEY_CURRENT_DATE, pref.getString(KEY_CURRENT_DATE, ""));

        user.put(KEY_RECEIVECODE, pref.getString(KEY_RECEIVECODE, ""));
        /*user.put(KEY_ACT_STATUS, pref.getString(KEY_ACT_STATUS, "false"));*/

        user.put(KEY_USER_MOBILE, pref.getString(KEY_USER_MOBILE, "0"));

        user.put(KEY_NEWUSER, pref.getString(KEY_NEWUSER, "1"));


        user.put(KEY_VERIFICATION_COUNTER,
                pref.getString(KEY_VERIFICATION_COUNTER, "0"));


        user.put(KEY_MESSAGE_GROUPID, pref.getString(KEY_MESSAGE_GROUPID, "0"));


        user.put(KEY_VERSTATUS, pref.getString(KEY_VERSTATUS, "0"));
/*        user.put(KEY_ACT_STATUS, pref.getString(KEY_ACT_STATUS, "0"));*/

        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, ""));


        user.put(KEY_ISGUJARATI_MENU, pref.getString(KEY_ISGUJARATI_MENU, "0"));

        user.put(KEY_ENODEDED_STRING, pref.getString(KEY_ENODEDED_STRING, ""));
        user.put(KEY_MENUKEY, pref.getString(KEY_MENUKEY, "0"));


        user.put(KEY_LOGINTYPE, pref.getString(KEY_LOGINTYPE, ""));

        //user.put(KEY_PROFILE_NAME,pref.getString(KEY_PROFILE_NAME,""));
        user.put(KEY_PROFILE_PIC_URL, pref.getString(KEY_PROFILE_PIC_URL, ""));
        //user.put(KEY_PROFILE_EMAIL,pref.getString(KEY_PROFILE_EMAIL,""));


        user.put(KEY_DAY_OF_MONTH, pref.getString(KEY_DAY_OF_MONTH, "0"));
        user.put(KEY_GOLD_RATE, pref.getString(KEY_GOLD_RATE, "0"));
        user.put(KEY_DOLLOR_PRICE, pref.getString(KEY_DOLLOR_PRICE, "0"));


        user.put(KEY_USERID, pref.getString(KEY_USERID, "0"));
        user.put(KEY_DOB, pref.getString(KEY_DOB, ""));
        user.put(KEY_DOA, pref.getString(KEY_DOA, ""));
        user.put(KEY_REF_POINTS, pref.getString(KEY_REF_POINTS, "0"));
        user.put(KEY_REF_CODE, pref.getString(KEY_REF_CODE, ""));


        user.put(KEY_USERTYPE, pref.getString(KEY_USERTYPE, "demo"));


        // return user
        return user;
    }


    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        // After logout redirect user to Loing Activity
        //Intent i = new Intent(_context, NewLoginActivity.class);
        /*Intent i = new Intent(_context, EasyIntroActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        // Staring Login Activity
        _context.startActivity(i);*/
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Quick check for Activation
     **/
    // Get Login State
    public boolean isActivated() {
        return pref.getBoolean(IS_ACTIVATED, false);
    }


    public void setPreviousMessageTotalCount(String s) {

        editor.putString(KEY_COUNT_PREVIOUS_MESSAGE, s);
        editor.commit();
    }

    public void setPreviousContactsTotalCount(String contactsCount) {

        editor.putString(KEY_COUNT_PREVIOUS_CONTACTS, contactsCount);
        editor.commit();

    }


    //To Store Lattitude and Longtitude
    public void StoreContactUsDetails(String lattitude, String longtitude, String address, String con_email, String con_phone) {


        editor.putString(KEY_CONTACT_ADDRESS, address);
        editor.putString(KEY_LATTITUDE, lattitude);
        editor.putString(KEY_LONGTITUDE, longtitude);
        editor.putString(KEY_CONTACT_EMAIL, con_email);
        editor.putString(KEY_CONTACT_PHONE, con_phone);

        editor.commit();

    }

    public void setMessageType(String msgType) {

        editor.putString(KEY_MESSAGETYE, msgType);
        editor.commit();

    }

    public void setDefaultRouteType(String routeType) {

        editor.putString(KEY_DEFAULT_ROUTETYPE , routeType);
        editor.commit();
    }


    public void setBalanceReminder(String balReminder) {



        editor.putString(KEY_BALANCE_REMINDER , balReminder);
        editor.commit();

    }


    public void setActivityname(String actname)
    {
        editor.putString(KEY_ACTIVITYNAME , actname);
        editor.commit();
    }

    public void setDefaultSenderID(String defaultSenderid,String Idpos) {

        editor.putString(KEY_DEFAULT_SENDERID , defaultSenderid);

        editor.putString(KEY_DEFAULT_SENDERID_POS , Idpos);

        editor.commit();
    }
}
