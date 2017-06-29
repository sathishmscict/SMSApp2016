package com.tech9teen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import com.android.volley.toolbox.NetworkImageView;

import com.tech9teen.R;
import com.tech9teen.helper.CustomVolleyRequest;
import com.tech9teen.pojo.GalleryAlbum;
import com.tech9teen.SessionManager;


import java.util.HashMap;
import java.util.List;

/**
 * Created by Satish Gadde on 25-07-2016.
 */
public class GalleryAlbumAdapterRecyclerView extends RecyclerView.Adapter<GalleryAlbumAdapterRecyclerView.MyViewHolder> {


    private final List<GalleryAlbum> galleryList;
    private final Context mContext;
    private final SessionManager sessionmanager;
    private final LayoutInflater inflater;
    private HashMap<String, String> userDetails= new HashMap<String, String>();

    private ImageLoader imageLoader;

    public GalleryAlbumAdapterRecyclerView(Context mContext , List<GalleryAlbum> gyanlist)
    {
        this.mContext = mContext;
        this.galleryList = gyanlist;
        sessionmanager = new SessionManager(mContext);
        userDetails =sessionmanager.getUserDetails();
        inflater = LayoutInflater.from(mContext);

    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View itemView = inflater.inflate(R.layout.row_single_allery_album_cardview, parent, false);
        View itemView =LayoutInflater.from(mContext).inflate(R.layout.row_single_allery_album_cardview, parent, false);
        return  new MyViewHolder(itemView);


    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        private TextView albumname , description;

        private NetworkImageView album_thumbnail;


        public MyViewHolder(View itemView) {
            super(itemView);
            albumname = (TextView)itemView.findViewById(R.id.albumname);
            description = (TextView)itemView.findViewById(R.id.longdescr);
            album_thumbnail = (NetworkImageView)itemView.findViewById(R.id.thumbnail);



        }
    }





    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        GalleryAlbum gc = galleryList.get(position);
        holder.albumname.setText(gc.getClassname());
        holder.description.setText(gc.getDescription());

        // loading album cover using Glide library
        //Glide.with(mContext).load(gc.getThumbnail()).into(holder.student_thumbnail);


        Log.d("Thumb URL : " , gc.getThumbnail());

        imageLoader = CustomVolleyRequest.getInstance(mContext.getApplicationContext())
                .getImageLoader();


        imageLoader.get(gc.getThumbnail(), imageLoader.getImageListener(holder.album_thumbnail,
                R.mipmap.ic_launcher, R.mipmap.ic_launcher));

        holder.album_thumbnail.setImageUrl(gc.getThumbnail(), imageLoader);
        //holder.student_thumbnail.setImageUrl(String.valueOf(gc.getThumbnail(AllKeys.KEY_THUMB_URL)), imageLoader);



    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

}
