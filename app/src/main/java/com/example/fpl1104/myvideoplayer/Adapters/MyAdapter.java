package com.example.fpl1104.myvideoplayer.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fpl1104.myvideoplayer.LocalResources;
import com.example.fpl1104.myvideoplayer.MainActivity;
import com.example.fpl1104.myvideoplayer.R;

import java.util.List;

/**
 * Created by fpl1104 on 16/6/23.
 */
public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<String> filePaths;
    private List<String> fileNames;
    private List<String> times;
    private LocalResources localResources;
    private Bitmap bitmap;

    public MyAdapter(Context context,List<String> filePaths,LocalResources localResources) {
        this.context = context;
        this.filePaths=filePaths;
        this.localResources=localResources;
    }

    @Override
    public int getCount() {
        return filePaths.size();
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
            view = inflater.inflate(R.layout.listview_item4local, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        TextView textView = holder.getTextView();
        ImageView imageView = holder.getImageView();
        ViewGroup.LayoutParams ps = imageView.getLayoutParams();
        ps.height = 200;
        ps.width=200;
        imageView.setLayoutParams(ps);
        ViewGroup.LayoutParams tv = textView.getLayoutParams();
        tv.height = 200;

        textView.setText(localResources.filrNames.get(position));
        textView.setLayoutParams(tv);

        imageView.setImageBitmap(localResources.getVideoThumbnail(filePaths.get(position)));
        return view;

    }
}

class ViewHolder {
    private View view;
    private TextView textView;
    private ImageView imageView;

    public ViewHolder(View view) {
        this.view = view;
    }

    public TextView getTextView() {
        if (textView == null) {
            textView = (TextView) view.findViewById(R.id.listview_item_textview4lacal);
        }
        return textView;
    }

    public ImageView getImageView() {
        if (imageView == null) {
            imageView = (ImageView) view.findViewById(R.id.listview_item_imageview4local);
        }
        return imageView;
    }
}
