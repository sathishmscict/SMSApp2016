package com.tech9teen;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.tech9teen.helper.AllKeys;
import com.tech9teen.helper.ServiceHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Satish Gadde on 02-09-2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails;



    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        Context context=this;
        //sendRegistrationToServer(refreshedToken,context);

    }

    public void onTokenRefreshNew(Context context) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token new : " + refreshedToken);


        sendRegistrationToServer(refreshedToken,context);

    }

    private void sendRegistrationToServer(String token,Context context) {
        //You can implement this method to store the token on your server
        //Not required for current project

        try {
            sessionmanager = new SessionManager(context);
            userDetails = new HashMap<String, String>();
            userDetails = sessionmanager.getUserDetails();
            //Log.i(TAG, "registering device (regId = " + regId + ")");
            String serverUrl = AllKeys.TAG_WEBSITE_HAPPY+"/UpdateRegIdForNotifications";
            Log.d("Server URL : " , serverUrl);
            Map<String, String> params = new HashMap<String, String>();
            /*String stdid = userDetails.get(SessionManager.KEY_EMPID);


            params.put("type", "updategcm");
            params.put("device", "and");
            params.put("regid", token);
            params.put("empid", userDetails.get(SessionManager.KEY_EMPID));

            params.put("mobile", userDetails.get(SessionManager.KEY_MOBILE));
            params.put("clientid", AllKeys.CLIENT_ID);
            params.put("branch", userDetails.get(SessionManager.KEY_BRANCHID));*/
            Log.d("Params ", params.toString());


       /* try {
            post(serverUrl, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
            ServiceHandler sh= new ServiceHandler();
            List<NameValuePair> detailValue = new ArrayList<NameValuePair>(
                    2);
            detailValue.add(new BasicNameValuePair("type", "updategcm"));
            detailValue.add(new BasicNameValuePair("userid", userDetails.get(SessionManager.KEY_USERID)));
            detailValue.add(new BasicNameValuePair("regid", token));
            /*detailValue.add(new BasicNameValuePair("empid", stdid));*/
            /*detailValue.add(new BasicNameValuePair("mobile", userDetails.get(SessionManager.KEY_MOBILE)));
            detailValue.add(new BasicNameValuePair("clientid", AllKeys.CLIENT_ID));
            detailValue.add(new BasicNameValuePair("branch", userDetails.get(SessionManager.KEY_BRANCHID)));*/
            Log.d("detailsValue : ",detailValue.toString());

            Log.d("FCM Response : " , "FCM :");
            String res = sh.makeServiceCall(serverUrl,ServiceHandler.POST,detailValue);
            Log.d("FCM Response : " , "FCM :"+res);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Send FCM Error : " , e.getMessage());
        }


    }


    /**
     * Issue a POST request to the server.
     *
     * @param endpoint
     *            POST address.
     * @param params
     *            request parameters.
     *
     * @throws IOException
     *             propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;

        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url + "?" + body);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
           String response = EntityUtils.toString(httpEntity);
            Log.d("Response ; " , "Res :"+response);
            // Toast.makeText(context, "SMS sent ", Toast.LENGTH_LONG).show();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
