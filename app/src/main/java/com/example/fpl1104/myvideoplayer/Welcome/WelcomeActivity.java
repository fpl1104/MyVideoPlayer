package com.example.fpl1104.myvideoplayer.Welcome;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.fpl1104.myvideoplayer.Fragment.MyFragment0;
import com.example.fpl1104.myvideoplayer.Fragment.MyFragment1;
import com.example.fpl1104.myvideoplayer.Fragment.MyFragment2;
import com.example.fpl1104.myvideoplayer.Fragment.MyFragment3;
import com.example.fpl1104.myvideoplayer.Interface.Fragment3;
import com.example.fpl1104.myvideoplayer.MainActivity;
import com.example.fpl1104.myvideoplayer.MyUtil.ArrowDownloadButton;
import com.example.fpl1104.myvideoplayer.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fpl1104 on 16/6/25.
 */
public class WelcomeActivity extends FragmentActivity {

    private ImageView[] imgTitles;
    private LinearLayout dot_layout;
    private ViewPager viewPager;
    private Fragment[] fragments;
    private Timer timer;
    Intent intent ;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity(intent);
            WelcomeActivity.this.finish();

        }
    };
//private Fragment3 fragment3=new Fragment3() {
//    @Override
//    public void MyOnClickListener() {
//        if ((count % 2) == 0) {
//           fragments[3].
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progress = progress + 1;
//                            button.setProgress(progress);
//
//                        }
//                    });
//                }
//            }, 800, 20);
//        } else {
//            button.reset();
//        }
//        count++;
//    }
//};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        init();
        intent = new Intent(this, MainActivity.class);
        viewPager.setAdapter(new MyAdpater1(getSupportFragmentManager()));


    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //selectedTitle(position);
                selectedImage(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        fragments = new Fragment[4];
        fragments[0] = new MyFragment0();
        fragments[1] = new MyFragment1();
        fragments[2] = new MyFragment2();
        fragments[3] = new MyFragment3();

        dot_layout = (LinearLayout) findViewById(R.id.dot_layout);
        int count = dot_layout.getChildCount();
        imgTitles = new ImageView[count];
        for (int i = 0; i < count; i++) {
            imgTitles[i] = (ImageView) dot_layout.getChildAt(i);
            imgTitles[i].setTag(i);
            imgTitles[i].setEnabled(true);
            imgTitles[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int item = (Integer) v.getTag();
                    viewPager.setCurrentItem(item);
                    //selectedTitle(item);
                    selectedImage(item);
                }
            });
        }
        imgTitles[0].setEnabled(false);


    }

    private void selectedImage(int index) {
        for (int i = 0; i < imgTitles.length; i++) {
            imgTitles[i].setEnabled(true);
            imgTitles[i].setImageResource(R.drawable.dot0);
        }
        imgTitles[index].setEnabled(false);
        imgTitles[index].setImageResource(R.drawable.dot1);
        Log.e("TTT",index+"");
        if (index==3){
            Log.e("TTT","111");



//            this.finish();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(001);

                }
            }.start();
//            timer = new Timer(true);
//            timer.schedule(task,3000, 1000); //延时1000ms后执行，1000ms执行一次
//            timer.cancel();
        }
    }

//    TimerTask task = new TimerTask() {
//        public void run() {
//
//            handler.sendEmptyMessage(001);
//        }
//    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //在欢迎界面屏蔽BACK键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }


    class MyAdpater1 extends FragmentPagerAdapter {
        public MyAdpater1(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }


    }

}
