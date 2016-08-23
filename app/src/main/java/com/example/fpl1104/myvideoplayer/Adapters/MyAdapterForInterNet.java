package com.example.fpl1104.myvideoplayer.Adapters;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.fpl1104.myvideoplayer.MainActivity;
import com.example.fpl1104.myvideoplayer.R;
import com.example.fpl1104.myvideoplayer.SQL.MyOpenHelper;
import com.example.fpl1104.myvideoplayer.SQL.MyOpenHelper1;
import com.example.fpl1104.myvideoplayer.SurfaceView_player;

/**
 * Created by fpl1104 on 16/6/23.
 */
public class MyAdapterForInterNet extends BaseAdapter implements AdapterView.OnItemLongClickListener {
    LayoutInflater layoutInflater;
    Context context;
    DownloadManager manager ;
    DownloadCompleteReceiver receiver;
    private MyOpenHelper1 helper;
    private String username;
    private String[] text;
    private String[] video_uri;
    private String[] profile_image;

    public MyAdapterForInterNet(Context context, String username, String[] text, String[] video_uri, String[] profile_image) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        helper = new MyOpenHelper1(context);
        this.username = username;
        this.text = text;
        this.video_uri = video_uri;
        this.profile_image = profile_image;

    }


    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.listview_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        TextView textView = holder.getTextView();
        NetworkImageView imageView = holder.getImageView();
        RequestQueue mQueue = Volley.newRequestQueue(context);
        ImageLoader imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String s) {
                return null;
            }

            @Override
            public void putBitmap(String s, Bitmap bitmap) {

            }
        });


        textView.setText(text[position]);
        imageView.setImageUrl(profile_image[position], imageLoader);


        return view;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//        Log.i("我在这儿","!!!!!!!!!!!!!!!!!!!!");
        new AlertDialog.Builder(context)
                .setTitle("收藏")
                .setPositiveButton("收藏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db;
                        db = helper.getReadableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("username", username);
                        values.put("text", text[position]);
                        values.put("video_uri", video_uri[position]);
                        values.put("profile_image", profile_image[position]);

                        long rowId = db.insert("Personage", null, values);
                        if (rowId != -1) {
                            Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
//                Log.i("insertStudent","注册成功");

                        }
                        db.close();

                    }
                }).setNegativeButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager =(DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                receiver = new DownloadCompleteReceiver();
                context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                if (video_uri[position]!=null) {
                    DownloadManager.Request down=new DownloadManager.Request (Uri.parse(video_uri[position]));
                    //设置允许使用的网络类型，这里是移动网络和wifi都可以
//                down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
                    //禁止发出通知，既后台下载
//                    down.setShowRunningNotification(false);
                    //不显示下载界面
//                    down.setVisibleInDownloadsUi(false);
                    //设置下载后文件存放的位置
                    down.setDestinationInExternalFilesDir(context, null, text[position]);
                    //将下载请求放入队列
                    manager.enqueue(down);
                }else {
                    Toast.makeText(context, "无视频", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .show();

        return true;
    }


    class ViewHolder {
        private View view;
        private TextView textView;
        private NetworkImageView imageView;

        public ViewHolder(View view) {
            this.view = view;
        }

        public TextView getTextView() {
            if (textView == null) {
                textView = (TextView) view.findViewById(R.id.listview_item_textview);
            }
            return textView;
        }

        public NetworkImageView getImageView() {
            if (imageView == null) {
                imageView = (NetworkImageView) view.findViewById(R.id.listview_item_imageview);
            }
            return imageView;
        }
    }
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.v("I m Here"," download complete! id : "+downId);
                Toast.makeText(context, intent.getAction()+"id : "+downId, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
