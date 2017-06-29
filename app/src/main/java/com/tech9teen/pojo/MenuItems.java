package com.tech9teen.pojo;

import android.graphics.drawable.Drawable;

/**
 * Created by Satish Gadde on 12-09-2016.
 */
public class MenuItems {

    String menuname;



    int menu_icon;

    public MenuItems(String menuname, int imagename) {

        this.menuname = menuname;
        this.menu_icon = imagename;

    }


    public int getMenu_icon() {
        return menu_icon;
    }

    public void setMenu_icon(int menu_icon) {
        this.menu_icon = menu_icon;
    }


    public String getMenuname() {
        return menuname;
    }

    public void setMenuname(String menuname) {
        this.menuname = menuname;
    }


}
