package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 30-09-2016.
 */

public class SMSGroupDetailsData {
    String body;
    String address;
    String time;
    String id;
    String date;

    public SMSGroupDetailsData(String body, String address, String id, String date, String time) {
        this.body = body;
        this.address = address;
        this.id = id;
        this.date = date;
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
