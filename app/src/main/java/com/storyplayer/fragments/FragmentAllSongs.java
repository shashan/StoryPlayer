package com.storyplayer.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.storyplayer.ApplicationStoryPlayer;
import com.storyplayer.R;
import com.storyplayer.activities.StoryPlayerBaseActivity;
import com.storyplayer.activities.SongDownloadService;
import com.storyplayer.adapter.GalleryAdapter;
import com.storyplayer.manager.MediaController;
import com.storyplayer.models.Image;
import com.panwrona.downloadprogressbar.library.DownloadProgressBar;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.android.volley.Response.*;

/**
 * Created by shashank on 5/1/2016.
 */
public class FragmentAllSongs  extends Fragment {
    private String TAG = FragmentAllSongs.class.getSimpleName();
//    private static final String endpoint = "http://api.androidhive.info/json/glide.json";
    private static final String endpoint = "https://spreadsheets.google.com/feeds/list/1Lw6E-Rfd7vtFZ6SWKLRLPPh4uJLjvI2Wn6SzGrvTKcc/od6/public/values?alt=json";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private RelativeLayout emptylayout;
    DownloadProgressBar downloadProgressBar;
    private IntentFilter mIntentFilter;
    public FragmentAllSongs() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_allsongs, null);
        setupInitialViews(v);
        fetchImages();;
        return v;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }
    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }
    private void setupInitialViews(View inflatreView) {
        recyclerView = (RecyclerView) inflatreView.findViewById(R.id.recycler_view);
        emptylayout = (RelativeLayout) inflatreView.findViewById(R.id.empty_view);
        pDialog = new ProgressDialog(getActivity());
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(), images);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(StoryPlayerBaseActivity.mBroadcastStartedAction);
        mIntentFilter.addAction(StoryPlayerBaseActivity.mBroadcastFailedAction);
        mIntentFilter.addAction(StoryPlayerBaseActivity.mBroadcastSucessAction);
        Intent serviceIntent = new Intent(getActivity(), SongDownloadService.class);
        getActivity().startService(serviceIntent);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getActivity(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Image image = images.get(position);
                File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+File.separator+"Dadi",image.getName()+".mp3");
            if(!storageDir.exists()) {

//                downloadProgressBar = (DownloadProgressBar) view.findViewById(R.id.dpv3);
//                downloadProgressBar.playManualProgressAnimation();
                MediaController.getInstance().storeDownloading(getActivity(), image);


                if (!MediaController
                        .isMyServiceRunning(ApplicationStoryPlayer.applicationContext)) {
                    Intent service = new Intent(ApplicationStoryPlayer.applicationContext,
                            SongDownloadService.class);
                    ApplicationStoryPlayer.applicationContext.startService(service);
                }
            } else {
//                    Toast.makeText(getActivity(), "Song is already Downloaded",
//                            Toast.LENGTH_LONG).show();
                }
//                downloadProgressBar.setOnProgressUpdateListener(new DownloadProgressBar.OnProgressUpdateListener() {
//                @Override
//                public void onProgressUpdate(float currentPlayTime) {
//                // Here we are setting % value on our text view.
////                startBtn.setText(Math.round(currentPlayTime / 3.6) + " %");
//                }
//
//                @Override
//                public void onAnimationStarted() {
//                // Here we are disabling our view because of possible interactions while animating.
//                downloadProgressBar.setEnabled(false);
//                }
//
//                @Override
//                public void onAnimationEnded() {
////                startBtn.setText("Click to download");
//                downloadProgressBar.setEnabled(true);
//                 }
//
//                @Override
//                public void onAnimationSuccess() {
//    //                startBtn.setText("Downloaded!");
//                }
//
//                @Override
//                public void onAnimationError() {
//
//                }
//
//                @Override
//                public void onManualProgressStarted() {
//
//                }
//
//                @Override
//                public void onManualProgressEnded() {
//
//                }
//            });

//                someImage.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        someText.setText("Selected");
//                    }
//                });
//                someText.setText("Selected");


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private void fetchImages() {

        pDialog.setMessage("Fetching all Songs");
        pDialog.setCancelable(false);
        pDialog.show();
//        JsonObjectRequest req = new JsonObjectRequest();
        JSONObject feedmain = null;
        try {
            feedmain = new JSONObject("feed");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest  req = new JsonObjectRequest(endpoint,feedmain,
                new Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(TAG, jsonObject.toString());

                        recyclerView.setVisibility(View.VISIBLE);
                        emptylayout.setVisibility(View.GONE);
                        try {
                            JSONObject feed = jsonObject.getJSONObject("feed");
                            JSONArray entry = feed.getJSONArray("entry");

                            for (int i = 0; i < entry.length(); i++) {
                                JSONObject object = entry.getJSONObject(i);
                                Image image = new Image();
                                JSONObject idobject = object.getJSONObject("gsx$id");
                                image.setId(Integer.parseInt(idobject.getString("$t")));
                                JSONObject nameobject = object.getJSONObject("gsx$name");
                                image.setName(nameobject.getString("$t"));
                                JSONObject imageobject = object.getJSONObject("gsx$urlimage");
                                image.setUrlImage(imageobject.getString("$t"));
                                JSONObject timeobject = object.getJSONObject("gsx$timestamp");
                                image.setTimestamp(timeobject.getString("$t"));
                                JSONObject sizeobject = object.getJSONObject("gsx$size");
                                image.setSize(sizeobject.getString("$t"));
                                JSONObject songobject = object.getJSONObject("gsx$urlsong");
                                image.setUrlSong(songobject.getString("$t"));
                                JSONObject lengthobject = object.getJSONObject("gsx$length");
                                image.setLength(lengthobject.getString("$t"));

                                images.add(image);

                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.hide();
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Errors: " + volleyError.getMessage());
                pDialog.hide();
                recyclerView.setVisibility(View.GONE);
                emptylayout.setVisibility(View.VISIBLE);
            }
        }
        );
//        JsonArrayRequest req = new JsonArrayRequest(endpoint,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d(TAG, response.toString());
//                        pDialog.hide();
//
//                        images.clear();
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
//                                JSONObject object = response.getJSONObject(i);
//                                Image image = new Image();
//                                image.setName(object.getString("name"));
//
//                                JSONObject url = object.getJSONObject("url");
//                                image.setSmall(url.getString("small"));
//                                image.setMedium(url.getString("medium"));
//                                image.setLarge(url.getString("large"));
//                                image.setTimestamp(object.getString("timestamp"));
//
//                                images.add(image);
//
//                            } catch (JSONException e) {
//                                Log.e(TAG, "Json parsing error: " + e.getMessage());
//                            }
//                        }
//
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Error: " + error.getMessage());
//                pDialog.hide();
//            }
//        });

        // Adding request to request queue
        ApplicationStoryPlayer.getInstance().addToRequestQueue(req);
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(StoryPlayerBaseActivity.mBroadcastStartedAction)) {
                for(int i =0; i < recyclerView.getChildCount();i++){
                   int position = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i));
                    if(images.get(position).getName().equals(intent.getStringExtra("SongName"))){
                        DownloadProgressBar dpBar = (DownloadProgressBar) recyclerView.getChildAt(i).findViewById(R.id.dpv3);
                        dpBar.playManualProgressAnimation();
                    }

                }

//                downloadProgressBar = (DownloadProgressBar) view.findViewById(R.id.dpv3);
//                downloadProgressBar.playManualProgressAnimation();
//                intent.getStringExtra("SongName");
            } else if (intent.getAction().equals(StoryPlayerBaseActivity.mBroadcastSucessAction)) {
                Toast.makeText(getActivity(), intent.getStringExtra("SongName") +" Sucess",
                        Toast.LENGTH_SHORT).show();
                for(int i =0; i < recyclerView.getChildCount();i++){
                    int position = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i));
                    if(images.get(position).getName().equals(intent.getStringExtra("SongName"))){
                        DownloadProgressBar dpBar = (DownloadProgressBar) recyclerView.getChildAt(i).findViewById(R.id.dpv3);
                        dpBar.setSuccessResultState();
                    }

                }
//                 intent.getIntExtra("SongName", 0);
            } else if (intent.getAction().equals(StoryPlayerBaseActivity.mBroadcastFailedAction)) {
                Toast.makeText(getActivity(), intent.getStringExtra("SongName") +" Failed",
                        Toast.LENGTH_SHORT).show();
                for(int i =0; i < recyclerView.getChildCount();i++){
                    int position = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i));
                    if(images.get(position).getName().equals(intent.getStringExtra("SongName"))){
                        DownloadProgressBar dpBar = (DownloadProgressBar) recyclerView.getChildAt(i).findViewById(R.id.dpv3);
                        dpBar.setErrorResultState();
                    }

                }

            } else if (intent.getAction().equals(StoryPlayerBaseActivity.mBroadcastProgressAction)) {
//                Toast.makeText(getActivity(), intent.getStringExtra("SongName") +" Failed",
//                        Toast.LENGTH_SHORT).show();
//                 intent.getIntExtra("Amount", 0);
                for(int i =0; i < recyclerView.getChildCount();i++){
                    int position = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i));
                    if(images.get(position).getName().equals(intent.getStringExtra("SongName"))){
                        DownloadProgressBar dpBar = (DownloadProgressBar) recyclerView.getChildAt(i).findViewById(R.id.dpv3);
                        dpBar.setProgress(intent.getIntExtra("Amount", 0));
                    }

                }
            }
        }
    };
}
