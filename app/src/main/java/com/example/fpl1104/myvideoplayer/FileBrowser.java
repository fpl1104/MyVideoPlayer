package com.example.fpl1104.myvideoplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by fpl1104 on 16/6/22.
 */
public class FileBrowser extends FragmentActivity {
    private static final String ROOT_PATH = "/";
    //存储文件名称
    private ArrayList<String> names = null;
    //存储文件路径
    private ArrayList<String> paths = null;
    private View view;
    private EditText editText;
    private ListView listView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        init();
        //显示文件列表
        showFileDir(ROOT_PATH);
    }

    private void init() {
        listView = (ListView) findViewById(R.id.listview);
    }

    private void showFileDir(String path) {
        names = new ArrayList<String>();
        paths = new ArrayList<String>();
        File file = new File(path);
        //Returns an array of files contained in the directory represented by this file.
        File[] files = file.listFiles();

        //如果当前目录不是根目录
        if (!ROOT_PATH.equals(path)) {
            names.add("@1");
            paths.add(ROOT_PATH);

            names.add("@2");
            paths.add(file.getParent());
        }
        //添加所有文件
        for (File f : files) {
            names.add(f.getName());
            paths.add(f.getPath());
        }
        listView.setAdapter(new MyAdapter(this, names, paths));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = paths.get(position);
                File file = new File(path);
                // 文件存在并可读
                if (file.exists() && file.canRead()) {
                    if (file.isDirectory()) {
                        //显示子目录及文件
                        showFileDir(path);
                    } else {
                        //处理文件
                        fileHandle(file);
                    }
                }
                //文件不可读
                else {

                    new AlertDialog.Builder(FileBrowser.this).setTitle("Message")
                            .setMessage("打不开")
                            .setPositiveButton("OK", null).show();
                }

            }
        });
    }


    //对文件进行增删改
    private void fileHandle(final File file) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 打开文件
                if (which == 0) {
                    openFile(file);
                }
                //修改文件名
                else if (which == 1) {
                    LayoutInflater factory = LayoutInflater.from(FileBrowser.this);
                    view = factory.inflate(R.layout.rename_dialog, null);
                    editText = (EditText) view.findViewById(R.id.editText);
                    editText.setText(file.getName());

                    DialogInterface.OnClickListener listener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String modifyName = editText.getText().toString();
                            final String fpath = file.getParentFile().getPath();
                            final File newFile = new File(fpath + "/" + modifyName);
                            if (newFile.exists()) {
                                //排除没有修改情况
                                if (!modifyName.equals(file.getName())) {
                                    new AlertDialog.Builder(FileBrowser.this)
                                            .setTitle("注意!")
                                            .setMessage("文件名已存在，是否覆盖？")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (file.renameTo(newFile)) {
                                                        showFileDir(fpath);
                                                        displayToast("重命名成功！");
                                                    } else {
                                                        displayToast("重命名失败！");
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", null)
                                            .show();
                                }
                            } else {
                                if (file.renameTo(newFile)) {
                                    showFileDir(fpath);
                                    displayToast("重命名成功！");
                                } else {
                                    displayToast("重命名失败！");
                                }
                            }
                        }
                    };
                    AlertDialog renameDialog = new AlertDialog.Builder(FileBrowser.this).create();
                    renameDialog.setView(view);
                    renameDialog.setButton("确定", listener2);
                    renameDialog.setButton2("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
                    renameDialog.show();
                }
                //删除文件
                else {
                    new AlertDialog.Builder(FileBrowser.this)
                            .setTitle("注意!")
                            .setMessage("确定要删除此文件吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (file.delete()) {
                                        //更新文件列表
                                        showFileDir(file.getParent());
                                        displayToast("删除成功！");
                                    } else {
                                        displayToast("删除失败！");
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            }
        };
        //选择文件时，弹出增删该操作选项对话框
        String[] menu = {"打开文件", "重命名", "删除文件"};
        new AlertDialog.Builder(FileBrowser.this)
                .setTitle("请选择要进行的操作!")
                .setItems(menu, listener)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        startActivity(intent);
    }

    //获取文件mimetype
    private String getMIMEType(File file) {
        String type = "";
        String name = file.getName();
        //文件扩展名
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("mp4") || end.equals("3gp")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")) {
            type = "image";
        } else {
            //如果无法直接打开，跳出列表由用户选择
            type = "*";
        }
        type += "/*";
        return type;
    }

    private void displayToast(String message) {
        Toast.makeText(FileBrowser.this, message, Toast.LENGTH_SHORT).show();
    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Bitmap directory, file,home;
        //文件名称
        private ArrayList<String> names = null;
        //文件路径
        private ArrayList<String> paths = null;


        public MyAdapter(Context context, ArrayList<String> na, ArrayList<String> pa) {
            names = na;
            paths = pa;
            directory = BitmapFactory.decodeResource(context.getResources(), R.drawable.d);
            file = BitmapFactory.decodeResource(context.getResources(), R.drawable.e);
            home=BitmapFactory.decodeResource(context.getResources(), R.drawable.f);
            //缩小图片
            directory = small(directory, 0.16f);
            file = small(file, 0.1f);
            home=small(home,0.18f);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return names.size();
        }

        @Override
        public Object getItem(int position) {
            return names.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.listview_item4local, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.listview_item_textview4lacal);
                holder.image = (ImageView) convertView.findViewById(R.id.listview_item_imageview4local);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            File f = new File(paths.get(position).toString());
            if (names.get(position).equals("@1")) {
                holder.text.setText("/");
                holder.image.setImageBitmap(home);
            } else if (names.get(position).equals("@2")) {
                holder.text.setText("返回上一级");
                holder.image.setImageBitmap(home);
            } else {
                holder.text.setText(f.getName());
                if (f.isDirectory()) {
                    holder.image.setImageBitmap(directory);
                } else if (f.isFile()) {
                    holder.image.setImageBitmap(file);
                } else {
                    System.out.println(f.getName());
                }
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView text;
            private ImageView image;
        }

        private Bitmap small(Bitmap map, float num) {
            Matrix matrix = new Matrix();
            matrix.postScale(num, num);
            return Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(), matrix, true);
        }
    }
}

