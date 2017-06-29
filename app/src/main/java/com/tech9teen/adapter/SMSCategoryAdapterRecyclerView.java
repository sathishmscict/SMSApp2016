package com.tech9teen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tech9teen.R;
import com.tech9teen.pojo.SMSGroupData;

import java.util.ArrayList;

/**
 * Created by Satish Gadde on 26-09-2016.
 */

public class SMSCategoryAdapterRecyclerView extends RecyclerView.Adapter<SMSCategoryAdapterRecyclerView.MyViewHolder> {


    private final Context context;
    private final ArrayList<SMSGroupData> list_smsgroupdata;
    private final LayoutInflater inflater;

    public SMSCategoryAdapterRecyclerView(Context context, ArrayList<SMSGroupData> smsgroupdata)
    {
        this.context = context;
        this.list_smsgroupdata= smsgroupdata;
        inflater = LayoutInflater.from(context);


    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        private final TextView txtGroup;
        private final TextView txtSmsCounter;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtGroup = (TextView)itemView.findViewById(R.id.txtGroup);
            txtSmsCounter = (TextView)itemView.findViewById(R.id.txtSmsCounter);

            txtSmsCounter.setVisibility(View.GONE);


        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view= inflater.inflate(R.layout.row_single_smsgroup_cardview , parent,false);
        MyViewHolder viewGroup= new MyViewHolder(view);
        return viewGroup;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        SMSGroupData sms = list_smsgroupdata.get(position);

        holder.txtGroup.setText(sms.getGroupname());
        holder.txtSmsCounter.setText(String.valueOf(sms.getSmscount()));



    }

    @Override
    public int getItemCount() {
        return list_smsgroupdata.size();
    }


}
