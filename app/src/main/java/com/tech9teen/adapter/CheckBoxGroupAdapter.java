package com.tech9teen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tech9teen.R;
import com.tech9teen.pojo.GroupMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Satish Gadde on 21-09-2016.
 */

public class CheckBoxGroupAdapter extends RecyclerView.Adapter<CheckBoxGroupAdapter.MyViewHolder> {


    private final Context context;
    private final ArrayList<GroupMaster> list_groupData;
    private final LayoutInflater inflater;

    public CheckBoxGroupAdapter(Context context, ArrayList<GroupMaster> grpData) {
        this.context = context;
        this.list_groupData = grpData;

        inflater = LayoutInflater.from(context);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox chkGroup;

        public MyViewHolder(View itemView) {
            super(itemView);

            chkGroup = (CheckBox) itemView.findViewById(R.id.chkGroup);



        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = inflater.inflate(R.layout.row_single_group_cardview_dialog, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final int pos = position;
        GroupMaster GM = list_groupData.get(position);
        holder.chkGroup.setText(GM.getGroupname() + "( " + GM.getGroup_contacts() + " ) ");

        holder.chkGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                list_groupData.get(pos).setSeleted(isChecked);


            }
        });

        holder.chkGroup.setChecked(GM.isSeleted());


    }

    @Override
    public int getItemCount() {
        return list_groupData.size();
    }

    // method to access in activity after updating selection
    public List<GroupMaster> getGroupList() {
        return list_groupData;
    }


}
