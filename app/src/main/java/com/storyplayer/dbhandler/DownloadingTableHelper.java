package com.storyplayer.dbhandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.storyplayer.ApplicationStoryPlayer;
import com.storyplayer.models.Image;

/**
 * Created by shashank on 5/21/2016.
 */
public class DownloadingTableHelper {

    public static final String TABLENAME = "Downloading";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String URL_IMAGE = "urlimage";
    public static final String TIMESTAMP = "timestamp";
    public static final String SIZE = "size";
    public static final String URL_SONG = "urlsong";
    public static final String LENGTH = "length";

    private static StoryPLayerDBHelper dbHelper = null;
    private static DownloadingTableHelper mInstance;
    private SQLiteDatabase sampleDB;

    public static synchronized DownloadingTableHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DownloadingTableHelper(context);
        }
        return mInstance;
    }

    public Context context;

    public DownloadingTableHelper(Context context_) {
        this.context = context_;
        if (dbHelper == null) {
            dbHelper = ((ApplicationStoryPlayer) context.getApplicationContext()).DB_HELPER;
        }
    }

    public void inserDownloadingSong(Image image) {
        try {

            sampleDB = dbHelper.getDB();
            sampleDB.beginTransaction();

            String sql = "Insert or Replace into " + TABLENAME + " values(?,?,?,?,?,?,?);";
            SQLiteStatement insert = sampleDB.compileStatement(sql);

            try {
                if (image != null) {
                    insert.clearBindings();
                    insert.bindLong(1, image.getId());
                    insert.bindString(2, image.getName());
                    insert.bindString(3, image.getUrlImage());
                    insert.bindString(4, image.getTimestamp());
                    insert.bindString(5, image.getSize());
                    insert.bindString(6, image.getUrlSong());
                    insert.bindString(7, image.getLength());

                    insert.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            sampleDB.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e("XML:", e.toString());
        } finally {
            sampleDB.endTransaction();
        }
    }

    private void closeCurcor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public Cursor getDownloadList() {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLENAME ;
            sampleDB = dbHelper.getDB();
            mCursor = sampleDB.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            closeCurcor(mCursor);
            e.printStackTrace();
        }
        return mCursor;
    }

    public boolean getIsDownloading(Image image) {
        Cursor mCursor = null;
        try {
            String sqlQuery = "Select * from " + TABLENAME + " where " + ID + "=" + image.getId() ;
            sampleDB = dbHelper.getDB();
            mCursor = sampleDB.rawQuery(sqlQuery, null);
            if (mCursor != null && mCursor.getCount() >= 1) {
                closeCurcor(mCursor);
                return true;
            }
        } catch (Exception e) {
            closeCurcor(mCursor);
            e.printStackTrace();
        }
        return false;
    }

    public int deleteStatus(Image image) {
//        try {
            System.out.println("Deleting data.." + image.getName());
            sampleDB = dbHelper.getDB();

            return sampleDB.delete(TABLENAME, NAME +" = ?",
                    new String[] { image.getName() });

//            String sqlQuery = "Delete from " + TABLENAME + " where " + ID + "=" + image.getId();
//
//            sampleDB.rawQuery(sqlQuery, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
