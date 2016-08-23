package com.example.fpl1104.myvideoplayer.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.fpl1104.myvideoplayer.R;
import com.example.fpl1104.myvideoplayer.SQL.MyOpenHelper1;

/**
 * Created by fpl1104 on 16/6/30.
 */
public class MyAdapter4personage extends BaseAdapter {
    private MyOpenHelper1 helper;
    LayoutInflater layoutInflater;
    Context context;
    private String username;
    private String[] text;
    private String[] video_uri;
    private String[] profile_image;

    public MyAdapter4personage(Context context,  String username) {
        this.username=username;
        this.context = context;
        helper = new MyOpenHelper1(context);
        finddata(username);
        layoutInflater = LayoutInflater.from(context);

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
        ViewHolder1 holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.listview_item, parent, false);
            holder = new ViewHolder1(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder1) view.getTag();
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
    private boolean finddata (String username) {
        Cursor cur = helper.getReadableDatabase().query("Personage",
                null, "username=?", new String[]{username}, null, null, null);
        int i=0;
        int rom=1;
        text=new String[rom];
        video_uri=new String[rom];
        profile_image=new String[rom];
        while (cur.moveToNext()) {
          String []  text1=new String[text.length+1];
            String [] video_uri1=new String[text.length+1];
            String [] profile_image1=new String[text.length+1];
            for (int r=0;r<text.length;r++){
                text1[r]=text[r];
                video_uri1[r]=video_uri[r];
                profile_image1[r]=profile_image[r];
            }
            text=text1;
            video_uri=video_uri1;
            profile_image=profile_image1;
            text[i] = cur.getString(cur.getColumnIndex("text"));
            video_uri[i] = cur.getString(cur.getColumnIndex("video_uri"));
            profile_image[i] = cur.getString(cur.getColumnIndex("profile_image"));
            i++;
        }
        return false;
    }

    class ViewHolder1 {
        private View view;
        private TextView textView;
        private NetworkImageView imageView;

        public ViewHolder1(View view) {
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
}


