package com.tech9teen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
	private SessionManager sessionmanager;
	 String smsBody;
	 String code="";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                 smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                if(address.toLowerCase().contains("dnsite") || address.toLowerCase().contains("ctflcc"))
                {
                    smsMessageStr += "SMS From: " + address + "\n";
                    smsMessageStr += smsBody + "\n";
                }
            }

            //Intent intet=new Intent(context,VerificationCodeActivity.class);
            
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(smsBody);
            
            while (m.find()) {
            	
              System.out.println(m.group());
              code = code + m.group();
            } 
            if(code.length() == 4 || code.length() == 6)
            {
            	 try {
					//Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
					sessionmanager  = new SessionManager(context);
					HashMap<String, String> userDetails =new HashMap<String, String>();
					userDetails = sessionmanager.getUserDetails();
					sessionmanager.CheckSMSVerificationNoActivity(code , userDetails.get(SessionManager.KEY_VERSTATUS));
					
					//context.startActivity(new Intent(VerificationCodeActivity.class));
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            //this will update the UI with message
           // SmsActivity inst = SmsActivity.instance();
            //inst.updateList(smsMessageStr);
        }
    }
}