package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 14-09-2016.
 */
public class ContactsData {
    String mobilenumber, name;
    int contacactid;



    boolean isSeleted;


    public ContactsData(String mobilenumber, String name, int contacactid , boolean selected) {
        this.mobilenumber = mobilenumber;
        this.name = name;
        this.contacactid = contacactid;
        this.isSeleted = selected;
    }

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }



    public int getContacactid() {
        return contacactid;
    }

    public void setContacactid(int contacactid) {
        this.contacactid = contacactid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }


}
