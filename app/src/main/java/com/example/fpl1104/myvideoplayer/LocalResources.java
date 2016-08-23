package com.example.fpl1104.myvideoplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fpl1104 on 16/6/21.
 */
public class LocalResources {
    private Context mContext;
    private String filePath;
     String fileName;
     String time;
    public List<String > filrNames=new ArrayList<>();
    public List<String > times=new ArrayList<>();
    public LocalResources(Context mContext) {
        this.mContext = mContext;
    }
//取得本地文件路径
   public List<String > getData(){
       List<String > filePaths=new ArrayList<>();
       ContentResolver contentResolver = mContext.getContentResolver();
       String[] projection = new String[]{MediaStore.Video.Media.DATA,MediaStore.Video.Media.DISPLAY_NAME,
               MediaStore.Video.Media.DURATION};

       Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
               null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
       cursor.moveToFirst();
       int fileNum = cursor.getCount();
       //这里是由延时的
//       if(fileNum==0){
//           Log.e("TEST","没有电影");
//       }

       for(int counter = 0; counter < fileNum; counter++){
//           Log.w("TEST", "----------------------file is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
            filePath=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
//            fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
//          Log.e("TEST",fileName+"");
            fileName=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            time=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
           Log.e("TEST",fileName+"");
           times.add(time);
           filrNames.add(fileName);
           filePaths.add(filePath);
           cursor.moveToNext();
       }
       cursor.close();
       return filePaths;
   }
    //获取缩略图
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
