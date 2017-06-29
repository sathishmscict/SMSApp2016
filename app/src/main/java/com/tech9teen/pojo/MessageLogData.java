package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 29-09-2016.
 */

public class MessageLogData {

    String Message, Title, Time, Id;
    String MessageId, Date, Address, Name, Type;

    public MessageLogData(String message, String time, String messageId, String date, String address, String name, String type) {
        Message = message;

        Time = time;

        MessageId = messageId;
        Date = date;
        Address = address;
        Name = name;
        Type = type;
    }

    public MessageLogData(String message, String title, String time, String id) {
        Message = message;
        Title = title;
        Time = time;
        Id = id;
    }


    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }


    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


}
