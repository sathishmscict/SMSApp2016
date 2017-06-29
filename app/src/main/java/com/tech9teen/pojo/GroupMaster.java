package com.tech9teen.pojo;

import java.io.Serializable;

/**
 * Created by Satish Gadde on 14-09-2016.
 */
public class GroupMaster implements Serializable {


    String groupname;



    int groupid;
    int group_contacts;
    boolean isSeleted;


    public GroupMaster(String groupname, int groupid, int group_contacts) {
        this.groupname = groupname;
        this.groupid = groupid;
        this.group_contacts = group_contacts;
    }

    public GroupMaster(String groupname, int groupid, int group_contacts, boolean isSeleted) {
        this.groupname = groupname;
        this.groupid = groupid;
        this.group_contacts = group_contacts;
        this.isSeleted = isSeleted;
    }



    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }




    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getGroup_contacts() {
        return group_contacts;
    }

    public void setGroup_contacts(int group_contacts) {
        this.group_contacts = group_contacts;
    }





}
