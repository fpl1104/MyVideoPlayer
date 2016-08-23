package com.example.fpl1104.myvideoplayer.SQL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by fpl1104 on 16/6/28.
 */
public class UserBiz {
    private User user;
    private MyOpenHelper helper;
    private Cursor cursor;

    public void insert(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        SQLiteDatabase db;
        db = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long rowId = db.insert("User", null, values);
        if (rowId != -1) {
            System.out.println("添加成功");
        }
        db.close();
    }
    public void delete(User user){
        cursor=helper.getReadableDatabase().query("User",
                null,null,null,null,null,null);
        String id=cursor.getString(cursor.getColumnIndex(String.valueOf(user.get_id())));
        SQLiteDatabase db=helper.getReadableDatabase();
        int line=db.delete("User","_id=?",new String[]{id});
        if (line>0){
            System.out.println("删除成功");
        }
        db.close();

    }
    public void updata(User user){
        cursor=helper.getReadableDatabase().query("User",
                null,null,null,null,null,null);
        String id=cursor.getString(cursor.getColumnIndex(String.valueOf(user.get_id())));
        SQLiteDatabase db=helper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("username",user.getUsername());
        values.put("password",user.getPassword());
        int line=db.update("User",values,"_id=?",new String[]{id});
        if (line>0){
            System.out.println("修改成功");
        }
        db.close();

    }
    public void quert(){
        cursor=helper.getReadableDatabase().query("User",
                null,null,null,null,null,null);
        while (cursor.moveToNext()) {
            int id=cursor.getInt(cursor.getColumnIndex("_id"));
            String name= cursor.getString(cursor.getColumnIndex("username"));
            String password= cursor.getString(cursor.getColumnIndex("password"));

            user=new User(id,name,password);
            System.out.println(user.toString());
        }


    }
}
