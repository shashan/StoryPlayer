package com.storyplayer.activities;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.storyplayer.R;
import com.storyplayer.dbhandler.DownloadingTableHelper;
import com.storyplayer.models.Image;
import com.storyplayer.phonemidea.PhoneMediaControl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by shashank on 5/21/2016.
 */

public class SongDownloadService extends Service {
    private ArrayList<Image> downloadList = new ArrayList<Image>();
    private Cursor cursor = null;
    public final String RESPONCE_SUCESS = "Sucess";
    public final String RESPONCE_FAIL = "Fail";
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mRetrievePendingUploadingFromDb();
        } catch (Exception e) {
            Log.e("FTPService", e.toString());
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void mRetrievePendingUploadingFromDb() {
        cursor = DownloadingTableHelper.getInstance(this).getDownloadList();
        downloadList.clear();
        downloadList = getSongsFromSQLDBCursor(cursor);
        Image downloadingSong;
        if(downloadList.size()>0){

            downloadingSong = downloadList.get(0);
            DownloadSongHttpHelper httpHelper = new DownloadSongHttpHelper(this,downloadingSong);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                httpHelper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else {
                httpHelper.execute();
            }
        } else {
            stopSelf();
        }
    }

    private ArrayList<Image> getSongsFromSQLDBCursor(Cursor cursor) {
        ArrayList<Image> generassongsList = new ArrayList<Image>();
        try {
            if (cursor != null && cursor.getCount() >= 1) {

                while (cursor.moveToNext()) {
                    long ID = cursor.getLong(cursor.getColumnIndex(DownloadingTableHelper.ID));
                    String name = cursor.getString(cursor.getColumnIndex(DownloadingTableHelper.NAME));
                    String urlimage = cursor.getString(cursor.getColumnIndex(DownloadingTableHelper.URL_IMAGE));
                    String timestamp = cursor.getString(cursor.getColumnIndex(DownloadingTableHelper.TIMESTAMP));
                    String size = cursor.getString(cursor.getColumnIndex(DownloadingTableHelper.SIZE));
                    String urlsong = cursor.getString(cursor.getColumnIndex(DownloadingTableHelper.URL_SONG));
                    String length = cursor.getString(cursor.getColumnIndex(DownloadingTableHelper.LENGTH));

                    Image downloadingSongDetail = new Image((int) ID, name, urlimage, timestamp, size, urlsong, length);
                    generassongsList.add(downloadingSongDetail);
                }
            }
            closeCrs();
        } catch (Exception e) {
            closeCrs();
            e.printStackTrace();
        }
        return generassongsList;
    }
    private void closeCrs() {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }
    public class DownloadSongHttpHelper extends AsyncTask<Void, String, String> {
        private Context mContext;
        Image downloadingSong;
        int progressMain =0;
        public DownloadSongHttpHelper(Context context, Image image){
            this.mContext = context;
            this.downloadingSong =image;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNotifyManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setContentTitle(downloadingSong.getName() + " is Downloading")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.ic_action_favorite_on);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(StoryPlayerBaseActivity.mBroadcastStartedAction);
            broadcastIntent.putExtra("SongName", downloadingSong.getName());
            sendBroadcast(broadcastIntent);
        }

        @Override
        protected String doInBackground(Void... params) {
            int count;
            String responceStatus = RESPONCE_FAIL;
            try {

                URL url = new URL(downloadingSong.getUrlSong());
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+File.separator+"Dadi");
                if(!storageDir.exists()){
                    storageDir.mkdirs();
                }

                OutputStream output = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"Dadi")+"/"+downloadingSong.getName()+".mp3");
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                responceStatus = RESPONCE_SUCESS;
            } catch (Exception e) {
                responceStatus = RESPONCE_FAIL;
            }
            return responceStatus;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            if (progressMain+5 > Integer.parseInt(progress[0])){
                mBuilder.setProgress(100, Integer.parseInt(progress[0]), false);
                // Displays the progress bar for the first time.
                mNotifyManager.notify(downloadingSong.getId(), mBuilder.build());
                progressMain =Integer.parseInt(progress[0]);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(StoryPlayerBaseActivity.mBroadcastProgressAction);
                broadcastIntent.putExtra("SongName", downloadingSong.getName());
                broadcastIntent.putExtra("Amount", Integer.parseInt(progress[0]));
                sendBroadcast(broadcastIntent);
//			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
//            downloadProgressBar.setProgress(Integer.parseInt(progress[0]));
            }

        }
        @Override
        protected void onPostExecute(String responceStatus) {
            if(responceStatus == RESPONCE_SUCESS){
            try {
                mBuilder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0,0,false);
                mNotifyManager.notify(downloadingSong.getId(), mBuilder.build());
                DownloadingTableHelper.getInstance(mContext).deleteStatus(downloadingSong);
                mRetrievePendingUploadingFromDb();
                PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
//                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+"/"+"Dadi"+"/"+downloadingSong.getName()+".mp3";
                mPhoneMediaControl.addMusicItem(getBaseContext(),downloadingSong.getName());
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(StoryPlayerBaseActivity.mBroadcastSucessAction);
                broadcastIntent.putExtra("SongName", downloadingSong.getName());
                sendBroadcast(broadcastIntent);
            } catch (Exception e) {
                Log.e("FTPService", e.toString());
//                Toast.makeText(getBaseContext(), "Song is Failed",
//                        Toast.LENGTH_LONG).show();
            }
            }else {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(StoryPlayerBaseActivity.mBroadcastFailedAction);
                broadcastIntent.putExtra("SongName", downloadingSong.getName());
                sendBroadcast(broadcastIntent);
                stopSelf();
            }
//			mProgressDialog.dismiss();
//            downloadProgressBar.setSuccessResultState();
        }

    }
}
