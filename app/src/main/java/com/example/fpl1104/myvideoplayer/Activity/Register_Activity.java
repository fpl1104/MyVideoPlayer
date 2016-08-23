package com.example.fpl1104.myvideoplayer.Activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fpl1104.myvideoplayer.R;
import com.example.fpl1104.myvideoplayer.SQL.MyOpenHelper;
import com.example.fpl1104.myvideoplayer.SQL.User;

/**
 * Created by fpl1104 on 16/6/28.
 */
public class Register_Activity extends Dialog {
    private EditText username, password1, password2;
    private Button but1, but2;
    private MyOpenHelper helper;
    public Register_Activity(final Context context) {
        super(context);
        helper=new MyOpenHelper(context);
        setContentView(R.layout.register_dialog);

        username = (EditText) findViewById(R.id.editText_username_register);
        password1 = (EditText) findViewById(R.id.editText_password_register);
        password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        password2 = (EditText) findViewById(R.id.editText_password2_register);
        password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        but1 = (Button) findViewById(R.id.button_register);
        but2 = (Button) findViewById(R.id.button_canel_register);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
                insertStudent(context);


            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }


    private void insertStudent(Context context) {
        String name=username.getText().toString().trim();
        String pas=password1.getText().toString().trim();
        SQLiteDatabase db;
        if (password1.getText().toString().equals(password2.getText().toString())) {
        Log.i("insertStudent","走到这了");
            db = helper.getReadableDatabase();
            ContentValues values=new ContentValues();
            values.put("username",name);
            values.put("password",pas);

            long rowId=db.insert("User",null,values);
            if (rowId!=-1){
                Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
                Log.i("insertStudent","注册成功");
        dismiss();
            }
            db.close();
        }

    }
}
