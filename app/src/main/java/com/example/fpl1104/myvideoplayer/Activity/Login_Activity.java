package com.example.fpl1104.myvideoplayer.Activity;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fpl1104.myvideoplayer.Interface.Dialog2Activity;
import com.example.fpl1104.myvideoplayer.R;
import com.example.fpl1104.myvideoplayer.SQL.MyOpenHelper;

/**
 * Created by fpl1104 on 16/6/28.
 */
public class Login_Activity extends Dialog {
    private MyOpenHelper helper;
    private EditText username, password;
    private Button but1, but2;
    private Dialog2Activity dialog2Activity;

    public Login_Activity(Context context) {
        super(context);
        helper = new MyOpenHelper(context);
        setContentView(R.layout.login_dialog);
        username = (EditText) findViewById(R.id.editText_username);
        password = (EditText) findViewById(R.id.editText_password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        but1 = (Button) findViewById(R.id.button_login);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag=finduser(username.getText().toString());
                dialog2Activity.OnBtnClickListener(flag);
                dialog2Activity.getusername(username.getText().toString());
                dismiss();
            }
        });
        but2 = (Button) findViewById(R.id.button_canel);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private boolean finduser(String username) {
        Cursor cur = helper.getReadableDatabase().query("User",
                null, "username=?", new String[]{username}, null, null, null);
        while (cur.moveToNext()) {
            String name = cur.getString(cur.getColumnIndex("username"));
            String pas = cur.getString(cur.getColumnIndex("password"));
            if (pas.equals(password.getText().toString())) {
                return true;
            }

        }
            return false;
    }
    public void senddialog2Activity(Dialog2Activity Activity){
        dialog2Activity=Activity;
    }
}


