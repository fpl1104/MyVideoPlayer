package com.example.fpl1104.myvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fpl1104.myvideoplayer.Activity.Login_Activity;
import com.example.fpl1104.myvideoplayer.Activity.Register_Activity;
import com.example.fpl1104.myvideoplayer.Adapters.MyAdapter;
import com.example.fpl1104.myvideoplayer.Adapters.MyAdapter4personage;
import com.example.fpl1104.myvideoplayer.Adapters.MyAdapterForInterNet;
import com.example.fpl1104.myvideoplayer.Interface.Dialog2Activity;
import com.example.fpl1104.myvideoplayer.SQL.MyOpenHelper;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String APP_ID="wxd65c91803fe0cf5e";
    private IWXAPI api;
    private ListView listView;
    private SwipeRefreshLayout mSwipeLayout;
    private LocalResources localResources;
    private SharedPreferences mPreferences;
    private List<String> filePaths;
    private MyAdapter myAdapter;
    private MyAdapterForInterNet myAdapterForInterNet;
    private static final int REFRESH_COMPLETE = 0X110;
    private TextView tv1,tv2;
    private MyOpenHelper helper;
    private String timestamp;

    private String url;
    public RequestQueue mQueue;
    private String[] text;
    private String[] video_uri;
    private String[] profile_image;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    url = setMyUil();
                    getResult(MainActivity.this, url);
                    mSwipeLayout.setRefreshing(false);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        regTowx();

    }
    private void regTowx(){
        api= WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.registerApp(APP_ID);
    }
    private void wechatShare(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "这里填写链接url";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "我觉得这个软件不错";
        msg.description = "你也来装一个试试吧!";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_share);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);

    }
    private void init() {
        helper = new MyOpenHelper(this);
        filePaths = new ArrayList<>();
        mPreferences = getSharedPreferences("list", Context.MODE_PRIVATE);
        for (int i = 0; i < 100; i++) {
            String filePath = mPreferences.getString("item" + i, "");
            Log.e("ERR", filePath);
            if (filePath.isEmpty()) {
                break;
            }
            filePaths.add(filePath);
        }
        localResources = new LocalResources(this);
        filePaths = localResources.getData();
        if (filePaths.isEmpty()) {
//            Log.e("ERR","11111111111111111");
        reflish();

        }
        listView = (ListView) findViewById(R.id.listview);
        //listView.setBackgroundResource(R.drawable.top);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wechatShare(0);
//                Snackbar.make(view, "还没写分享功能", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void reflish() {

        SharedPreferences.Editor editor = mPreferences.edit();
        for (int i = 0; i < filePaths.size(); i++) {
            editor.putString("item" + i, filePaths.get(i));

        }
        editor.commit();
    }

    private String setMyUil() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        timestamp = df.format(new Date());
        String url1 = "https://route.showapi.com/255-1?page=&showapi_appid=20969&showapi_timestamp=" + timestamp + "&title=&type=&showapi_sign=fabe0d012072468cb789a11af39b1842";
        return url1;
    }

    private void getResult(Context context, String url) {
        mQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        String data = jsonObject.toString();
                        JSONArray result = null;
                        JSONObject M1 = null;
                        JSONObject M2 = null;
                        JSONObject M3 = null;
                        try {
                            M1 = new JSONObject(data);
                            M2 = M1.getJSONObject("showapi_res_body");
                            M3 = M2.getJSONObject("pagebean");
                            result = M3.getJSONArray("contentlist");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("LENGTH", result.length() + "");

                        text = new String[result.length()];
                        video_uri = new String[result.length()];
                        profile_image = new String[result.length()];
                        for (int index = 0; index < result.length(); index++) {
                            try {
                                JSONObject air = result.getJSONObject(index);
                                text[index] = air.getString("text");
                                profile_image[index] = air.getString("profile_image");
                                video_uri[index] = air.getString("video_uri");
                                Log.e("video_uri",video_uri[index]+"");
                            } catch (JSONException e) {

                                e.printStackTrace();

                            }
                        }

                        if (tv1==null) {
                            myAdapterForInterNet = new MyAdapterForInterNet(MainActivity.this, "Admin",text, video_uri, profile_image);

                        }else {
                            myAdapterForInterNet = new MyAdapterForInterNet(MainActivity.this, tv1.getText().toString(),text, video_uri, profile_image);

                        }
                        listView.setAdapter(myAdapterForInterNet);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, SurfaceView_player.class);
                                intent.putExtra("filePath", video_uri[position]);
                                startActivity(intent);
                            }
                        });
                        listView.setOnItemLongClickListener(myAdapterForInterNet);
                        myAdapterForInterNet.notifyDataSetChanged();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent =  new Intent(Settings.ACTION_DATE_SETTINGS);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, SurfaceView_player.class);
            intent.putExtra("filePath", filePaths.get(position));
            startActivity(intent);
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
            listView.setBackgroundResource(R.drawable.white);

        if (id == R.id.nav_localfile) {
            myAdapter = new MyAdapter(this, filePaths, localResources);
            listView.setAdapter(myAdapter);
            listView.setOnItemClickListener(onItemClickListener);

            // Handle the camera action
        } else if (id == R.id.nav_selectfile) {
            startActivity(new Intent(MainActivity.this, FileBrowser.class));


        } else if (id == R.id.nav_internet) {
            url = setMyUil();
            getResult(MainActivity.this, url);
            Log.e("我在这儿","网络播放了");

        } else if (id == R.id.nav_manage) {
            MyAdapter4personage myAdapter4personage= null;
            if (tv1==null) {
                myAdapter4personage = new MyAdapter4personage(MainActivity.this,"Admin");

            }else {
                myAdapter4personage = new MyAdapter4personage(MainActivity.this,tv1.getText().toString());

            }
            listView.setAdapter(myAdapter4personage);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, SurfaceView_player.class);
                    intent.putExtra("filePath", video_uri[position]);
                    startActivity(intent);
                }
            });
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private Dialog2Activity dialog2Activity=new Dialog2Activity() {
        boolean flag1;
        @Override
        public void OnBtnClickListener(boolean flag) {
            Log.e("TTT",flag+"");
            flag1=flag;
        }

        @Override
        public void getusername(String name) {
            Log.e("TTT",name+"");
            if (flag1) {
                tv1.setText(name);
                tv2.setText("欢迎你");
            }else {
                Toast.makeText(MainActivity.this,"登陆失败",Toast.LENGTH_SHORT).show();
            }
        }
    };
    public void onClick(View view){
        tv1= (TextView) findViewById(R.id.textView4nav_header);
        tv2= (TextView) findViewById(R.id.textView24nav_header);
       switch (view.getId()){
           case R.id.textView4nav_header:
//               Toast.makeText(this,"zhuce",Toast.LENGTH_SHORT).show();
            Register_Activity register_activity=new Register_Activity(this);
               register_activity.setTitle("注册页面");

               register_activity.show();

               break;
           case R.id.textView24nav_header:
               Toast.makeText(this,"denglu",Toast.LENGTH_SHORT).show();
               Login_Activity login_activity=new Login_Activity(this);
               login_activity.setTitle("登陆界面");
               login_activity.senddialog2Activity(dialog2Activity);
               login_activity.show();
               break;
       }
    }



}
