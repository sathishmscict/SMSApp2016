package com.tech9teen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tech9teen.R;
import com.tech9teen.pojo.MenuItems;

import java.util.ArrayList;

/**
 * Created by Satish Gadde on 12-09-2016.
 */
public class MenuAdapterRecyclerView extends RecyclerView.Adapter<MenuAdapterRecyclerView.MyViewHolder> {


    private final LayoutInflater inflater;
    private final Context context;
    private final ArrayList<MenuItems> menudata;

    public MenuAdapterRecyclerView(Context con, ArrayList<MenuItems> menu)
    {
        this.context = con;
        this.menudata= menu;
        inflater = LayoutInflater.from(con);


    }


    public class MyViewHolder extends  RecyclerView.ViewHolder
    {

        private final TextView txtMenuName;
        private final ImageView imgMenu;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtMenuName = (TextView)itemView.findViewById(R.id.txtMenuName);
            imgMenu = (ImageView)itemView.findViewById(R.id.img_menu);
        }

    }


    @Override
    public MenuAdapterRecyclerView.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = inflater.inflate(R.layout.row_single_menu_dashboard, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MenuAdapterRecyclerView.MyViewHolder holder, int position) {

        MenuItems mi = menudata.get(position);

        try {
            holder.txtMenuName.setText(mi.getMenuname());
            holder.imgMenu.setImageResource(mi.getMenu_icon());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return menudata.size();
    }
}
