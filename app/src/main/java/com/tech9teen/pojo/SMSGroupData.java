package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 26-09-2016.
 */

public class SMSGroupData {

    int group_id;
    String groupname;
    int smscount;


    public SMSGroupData(String groupname, int group_id, int smscount) {
        this.groupname = groupname;
        this.group_id = group_id;
        this.smscount = smscount;
    }


    public int getSmscount() {
        return smscount;
    }

    public void setSmscount(int smscount) {
        this.smscount = smscount;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }





}
