package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 17-09-2016.
 */
public class CallLogData {

    String contactname;
    String mobilenumber;
    String incomingcalls;
    String outgoingcalls;
    String missedcalls;
    String calllogdate;
    String callDate;


    public CallLogData(String contactname, String mobilenumber, String incomingcalls, String outgoingcalls, String missedcalls, String calllogdate) {
        this.contactname = contactname;
        this.mobilenumber = mobilenumber;
        this.incomingcalls = incomingcalls;
        this.outgoingcalls = outgoingcalls;
        this.missedcalls = missedcalls;
        this.calllogdate = calllogdate;
    }


    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getCalllogdate() {
        return calllogdate;
    }

    public void setCalllogdate(String calllogdate) {
        this.calllogdate = calllogdate;
    }

    public String getMissedcalls() {
        return missedcalls;
    }

    public void setMissedcalls(String missedcalls) {
        this.missedcalls = missedcalls;
    }

    public String getOutgoingcalls() {
        return outgoingcalls;
    }

    public void setOutgoingcalls(String outgoingcalls) {
        this.outgoingcalls = outgoingcalls;
    }

    public String getIncomingcalls() {
        return incomingcalls;
    }

    public void setIncomingcalls(String incomingcalls) {
        this.incomingcalls = incomingcalls;
    }

    public String getContactname() {
        return contactname;
    }

    public void setContactname(String contactname) {
        this.contactname = contactname;
    }


}
