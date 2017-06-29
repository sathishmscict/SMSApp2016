package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 19-09-2016.
 */
public class ReminderData {


    String reminder_desr;
    String reminder_date;



    String reminder_time;
    int reminder_id;


    public ReminderData(String reminder_desr, String reminder_date, String reminder_time, int reminder_id) {
        this.reminder_desr = reminder_desr;
        this.reminder_date = reminder_date;
        this.reminder_time = reminder_time;
        this.reminder_id = reminder_id;
    }



    public int getReminder_id() {
        return reminder_id;
    }

    public void setReminder_id(int reminder_id) {
        this.reminder_id = reminder_id;
    }

    public String getReminder_time() {
        return reminder_time;
    }

    public void setReminder_time(String reminder_time) {
        this.reminder_time = reminder_time;
    }

    public String getReminder_date() {
        return reminder_date;
    }

    public void setReminder_date(String reminder_date) {
        this.reminder_date = reminder_date;
    }

    public String getReminder_desr() {
        return reminder_desr;
    }

    public void setReminder_desr(String reminder_desr) {
        this.reminder_desr = reminder_desr;
    }




}
