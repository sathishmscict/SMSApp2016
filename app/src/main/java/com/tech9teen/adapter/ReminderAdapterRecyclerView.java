package com.tech9teen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tech9teen.R;
import com.tech9teen.pojo.ReminderData;

import java.util.ArrayList;

/**
 * Created by Satish Gadde on 19-09-2016.
 */
public class ReminderAdapterRecyclerView extends RecyclerView.Adapter<ReminderAdapterRecyclerView.MyViewHolder> {


    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<ReminderData> listReminder;

    public ReminderAdapterRecyclerView(Context context, ArrayList<ReminderData> reminderData) {


        this.context = context;

        inflater = LayoutInflater.from(context);


        this.listReminder = reminderData;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDescr, txtDate, txtTime;

        public MyViewHolder(View itemView) {
            super(itemView);


            txtDescr = (TextView) itemView.findViewById(R.id.txtDescr);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);


        }
    }


    @Override
    public ReminderAdapterRecyclerView.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = inflater.inflate(R.layout.row_single_reminder_cardview, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReminderAdapterRecyclerView.MyViewHolder holder, int position) {

        try {
            ReminderData RD = listReminder.get(position);

            holder.txtDescr.setText(String.valueOf(RD.getReminder_desr()));
            holder.txtDate.setText(String.valueOf(RD.getReminder_date()));
            holder.txtTime.setText(String.valueOf(RD.getReminder_time()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listReminder.size();
    }


}
