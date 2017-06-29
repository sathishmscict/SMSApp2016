package com.tech9teen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tech9teen.R;
import com.tech9teen.pojo.GroupMaster;

import java.util.ArrayList;

/**
 * Created by Satish Gadde on 13-09-2016.
 */


public class GroupMasterAdapterRecyclerView extends RecyclerView.Adapter<GroupMasterAdapterRecyclerView.MyViewHolder> {


    private final LayoutInflater inflater;
    private final Context context;
    private final ArrayList<GroupMaster> GroupData;

    public GroupMasterAdapterRecyclerView(Context context, ArrayList<GroupMaster> templateData) {
        this.context = context;

        inflater = LayoutInflater.from(context);

        this.GroupData = templateData;


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtTitle;
        private final TextView txtContacts;
        private final Button btnDelete;
        private final Button btnEdit;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtContacts = (TextView) itemView.findViewById(R.id.txtDescr);
            btnDelete = (Button) itemView.findViewById(R.id.btndelete);
            btnEdit = (Button) itemView.findViewById(R.id.btnedit);


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = inflater.inflate(R.layout.row_single_template_cardview, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        GroupMaster MT = GroupData.get(position);

        holder.txtTitle.setText(MT.getGroupname());
        holder.txtContacts.setText(String.valueOf(MT.getGroup_contacts())+" Contacts");


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Delete Called" + position, Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Edit Called" + position, Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return GroupData.size();
    }


}
