package com.tech9teen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tech9teen.R;
import com.tech9teen.pojo.UploadDocuments;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Satish Gadde on 14-10-2016.
 */

public class UploadDocumentRecyclerViewAdapter extends RecyclerView.Adapter<UploadDocumentRecyclerViewAdapter.MyViewHolder> {


    private final Context mContext;
    private final LayoutInflater inflater;
    private List<UploadDocuments> listDocuemntsData= new ArrayList<UploadDocuments>();
    private String TAG = UploadDocumentRecyclerViewAdapter.class.getSimpleName();

    public UploadDocumentRecyclerViewAdapter(Context context , List<UploadDocuments> lstData)
    {
        mContext = context;
        listDocuemntsData = lstData;

        inflater = LayoutInflater.from(context);



    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtStatus;
        private final EditText txtRouteType;
        private final EditText txtSenderID;
        private final EditText txtRequestDate,txtApprovalDate;

        public MyViewHolder(View itemView) {

            super(itemView);

            /*txtStatus = (TextView)itemView.findViewById(R.id.txtStatus);
            txtRouteType =(TextView)itemView.findViewById(R.id.txtRouteType);
            txtSenderID =(TextView)itemView.findViewById(R.id.txtSenderId);
            txtRequestDate =(TextView)itemView.findViewById(R.id.txtRequestDate);
            txtApprovalDate=(TextView)itemView.findViewById(R.id.txtApprovalDate);
*/
            txtStatus = (TextView)itemView.findViewById(R.id.txtStatus);
            txtRouteType =(EditText)itemView.findViewById(R.id.txtRouteType);
            txtSenderID =(EditText)itemView.findViewById(R.id.txtSenderId);
            txtRequestDate =(EditText)itemView.findViewById(R.id.txtRequestDate);
            txtApprovalDate=(EditText)itemView.findViewById(R.id.txtApprovalDate);



        }

    }

    @Override
    public UploadDocumentRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View vview = inflater.inflate(R.layout.row_single_senderid_status,parent,false);
        MyViewHolder view = new MyViewHolder(vview);

        return view;
    }

    @Override

    public void onBindViewHolder(UploadDocumentRecyclerViewAdapter.MyViewHolder holder, int position) {

        UploadDocuments ud = listDocuemntsData.get(position);


        try {

            if(ud.getStatus().toLowerCase().equals("approved"))
            {


                holder.txtStatus.setTextColor(Color.parseColor("#006DF0"));
                holder.txtStatus.setText("Status : "+ud.getStatus());




                Drawable img = mContext.getResources().getDrawable( R.drawable.icon_approved );
                holder.txtStatus.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);



            }
            else
            {

                holder.txtStatus.setTextColor(Color.parseColor("#006DF0"));
                holder.txtStatus.setText("Status : "+ud.getStatus());

                Drawable img = mContext.getResources().getDrawable( R.drawable.icon_disapproved);
                holder.txtStatus.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
            }

            holder.txtRouteType.setText(""+ud.getRoutetype());

            Log.d(TAG , " Approved Date : "+ud.getApprovaldate());

           /* if(ud.getApprovaldate().equals(""))
            {

                holder.txtApprovalDate.setHint("");
                holder.txtApprovalDate.setText(ud.getApprovaldate());
            }
            else
            {
                holder.txtApprovalDate.setHint("Approved Date");
            holder.txtApprovalDate.setText(ud.getApprovaldate());
            }*/

            holder.txtApprovalDate.setText(ud.getApprovaldate());
            holder.txtRequestDate.setText(ud.getRequestdate());
            holder.txtSenderID.setText(ud.getSenderid());
        } catch (Exception e) {
            Log.d(TAG , " Error in Binding Data");
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return listDocuemntsData.size();
    }
}
