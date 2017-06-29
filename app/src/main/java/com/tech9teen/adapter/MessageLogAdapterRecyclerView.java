package com.tech9teen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tech9teen.R;
import com.tech9teen.pojo.MessageLogData;

import java.util.ArrayList;

/**
 * Created by Satish Gadde on 17-09-2016.
 */
public class MessageLogAdapterRecyclerView extends RecyclerView.Adapter<MessageLogAdapterRecyclerView.MyViewHolder> {


    private final Context context;
    private final ArrayList<MessageLogData> listMessageLog;
    private final LayoutInflater inflater;

    public MessageLogAdapterRecyclerView(Context mContext, ArrayList<MessageLogData> messageLogData) {
        this.context = mContext;
        this.listMessageLog = messageLogData;
        inflater = LayoutInflater.from(mContext);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        private final TextView txtTitle, txtMessage, txtTime;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);


        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_single_message_cardview, parent, false);
        MyViewHolder ViewHolder = new MyViewHolder(view);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        MessageLogData msg = listMessageLog.get(position);


        holder.txtTitle.setText(msg.getTitle());
        holder.txtMessage.setText(msg.getMessage());
        holder.txtTime.setText(msg.getTime());


    }

    @Override
    public int getItemCount() {
        return listMessageLog.size();
    }


}
