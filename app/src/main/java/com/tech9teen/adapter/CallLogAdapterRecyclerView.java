package com.tech9teen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tech9teen.R;
import com.tech9teen.pojo.CallLogData;

import java.util.ArrayList;

/**
 * Created by Satish Gadde on 17-09-2016.
 */
public class CallLogAdapterRecyclerView  extends RecyclerView.Adapter<CallLogAdapterRecyclerView.MyViewHolder>{


    private final Context context;
    private final ArrayList<CallLogData> listCallLog;
    private final LayoutInflater inflater;

    public CallLogAdapterRecyclerView(Context mContext, ArrayList<CallLogData> callLogData)
    {
        this.context = mContext;
        this.listCallLog = callLogData;
        inflater= LayoutInflater.from(mContext);

    }
    public class MyViewHolder  extends RecyclerView.ViewHolder{
        private final TextView txtContactname,txtDate,txtIncomingCalls,txtOutgoingCalls,txtMissedCalls;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtContactname = (TextView)itemView. findViewById(R.id.txtContactName);
            txtDate = (TextView)itemView. findViewById(R.id.txtDate);
            txtIncomingCalls = (TextView)itemView. findViewById(R.id.txtIncoming);
            txtOutgoingCalls = (TextView) itemView.findViewById(R.id.txtOutgoing);

            txtMissedCalls = (TextView) itemView.findViewById(R.id.txtMissed);

            txtMissedCalls.setVisibility(View.GONE);





        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_single_calllog_cardview,parent,false);
        MyViewHolder ViewHolder = new MyViewHolder(view);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        CallLogData CLD = listCallLog.get(position);

        holder.txtContactname.setText(CLD.getContactname()+"\n"+CLD.getMobilenumber());
        holder.txtIncomingCalls.setText("Incoming Calls Duration : "+ CLD.getIncomingcalls()+" Seconds.");
        holder.txtOutgoingCalls.setText("Outgoing Calls Duration: "+ CLD.getOutgoingcalls()+" Seconds.");
        holder.txtDate.setText("Date : "+CLD.getCalllogdate());




    }

    @Override
    public int getItemCount() {
        return listCallLog.size();
    }


}
