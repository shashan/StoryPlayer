package com.storyplayer.adapter;

import android.content.Context;
//import android.media.Image;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.storyplayer.R;
import com.storyplayer.dbhandler.DownloadingTableHelper;
import com.storyplayer.models.Image;
import com.panwrona.downloadprogressbar.library.DownloadProgressBar;

import java.io.File;
import java.util.List;

/**
 * Created by shashank on 5/1/2016.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<Image> images;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView thumbnail;
        public TextView lineOne;
        public TextView lineTwo;
        public DownloadProgressBar downloadProgressBar;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            lineOne = (TextView) view.findViewById(R.id.line1);
            lineTwo = (TextView) view.findViewById(R.id.line2);
            downloadProgressBar = (DownloadProgressBar)view.findViewById(R.id.dpv3);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext,"This is an Android Toast Message", Toast.LENGTH_LONG).show();
        }
    }


    public GalleryAdapter(Context context, List<Image> images) {
        mContext = context;
        this.images = images;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Image image = images.get(position);

        Glide.with(mContext).load(image.getUrlImage())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);
        holder.lineOne.setText(image.getName());
        holder.lineTwo.setText(image.getLength()+" | "+ image.getSize());
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+File.separator+"Dadi",image.getName()+".mp3");
        if(storageDir.exists()) {
            holder.downloadProgressBar.setVisibility(View.GONE);
        }else if( DownloadingTableHelper.getInstance(mContext).getIsDownloading(image)){
            holder.downloadProgressBar.playManualProgressAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));

            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
