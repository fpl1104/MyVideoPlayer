package com.example.fpl1104.myvideoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fpl1104.myvideoplayer.MyUtil.VerticalProgressBar;

import java.io.File;
import java.io.IOException;

/**
 * Created by fpl1104 on 16/6/21.
 */
public class SurfaceView_player extends Activity implements SurfaceHolder.Callback {
    SeekBar seekBar;
    TextView tv1, tv2;
    MediaPlayer player;
    SurfaceView surface;
    SurfaceHolder surfaceHolder;
    Button fast, slow, pause;
    LinearLayout layout;
    long sleeptime;

    AudioManager mAudioManager;
    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;
    private GestureDetector mGestureDetector;
    private VerticalProgressBar vpb_left, vpb_right;
    private int leftProgress = 0, rightProgress = 0;

    private String filePath;
    private int currentPosition = 0;//播放进度
    private boolean isPlaying;
    private SharedPreferences mPreferences;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (sleeptime < SystemClock.elapsedRealtime()) {
                layout.setVisibility(View.GONE);
            }
            String now=cal(msg.arg1 / 1000);
            tv1.setText(now);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePath = getIntent().getStringExtra("filePath");
        if (filePath==null) {
            Toast.makeText(this,"NO Viedo",Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.surfaceview);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        vpb_left = (VerticalProgressBar) findViewById(R.id.vpb_left);
        vpb_right = (VerticalProgressBar) findViewById(R.id.vpb_right);

        layout = (LinearLayout) findViewById(R.id.linearLayout1);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(change);
        tv1 = (TextView) findViewById(R.id.progressBar_piv);
        tv2 = (TextView) findViewById(R.id.progressBar_back);
        fast = (Button) findViewById(R.id.button1);
        pause = (Button) findViewById(R.id.button2);
        slow = (Button) findViewById(R.id.button4);
        surface = (SurfaceView) findViewById(R.id.surface);
        mPreferences = getSharedPreferences("surfacePlayer", Context.MODE_PRIVATE);

        surfaceHolder = surface.getHolder();//SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this); //因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        surfaceHolder.setFixedSize(320, 220);//显示的分辨率,不设置为视频默认
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//Surface类型

        fast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = seekBar.getProgress() + 10000;
                if (player != null && player.isPlaying()) {
// 设置当前播放的位置
                    player.seekTo(progress);
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                    pause.setBackgroundResource(android.R.drawable.ic_media_play);
                } else {
                    player.start();
                    pause.setBackgroundResource(android.R.drawable.ic_media_pause);
                }
            }
        });
        slow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = seekBar.getProgress() - 20000;
                if (player != null && player.isPlaying()) {
// 设置当前播放的位置
                    player.seekTo(progress);
                }
            }
        });



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                layout.setVisibility(View.VISIBLE);
                sleeptime = SystemClock.elapsedRealtime() + 3000;
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 显示
        rightProgress = (int) (mVolume * 100 / mMaxVolume);
        vpb_right.setProgress(rightProgress);

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        /**
         * 滑动
         */
        @SuppressWarnings("deprecation")
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float mOldX = e1.getRawX(), mOldY = e1.getRawY();
            int y = (int) e2.getRawY();
            int x= (int) e2.getRawX();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();


            if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth / 5.0)// 左边滑动
                onBrightnessSlide(distanceY / windowHeight);
            else if (mOldY>windowHeight/5.0)
                player.seekTo(player.getCurrentPosition()-(int)(mOldX-x));
//            	onSystemBrightnessSlide(distanceY / windowHeight);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            vpb_left.setVisibility(View.GONE);
            vpb_right.setVisibility(View.GONE);
        }
    };

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;
            // 显示
            rightProgress = (int) (mVolume * 100 / mMaxVolume);
            vpb_right.setProgress(rightProgress);
        }
        vpb_right.setVisibility(View.VISIBLE);

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        int progress = rightProgress + (int) (percent * 100);
        if (progress > 100) {
            progress = 100;
        } else if (progress < 0) {
            progress = 0;
        }
        vpb_right.setProgress(progress);
    }

    /**
     * 滑动改变屏幕亮度
     *
     * @param percent
     */
    public void onBrightnessSlide(float percent) {
        mBrightness = getWindow().getAttributes().screenBrightness;
        if (mBrightness <= 0.00f)
            mBrightness = 0.50f;
        if (mBrightness < 0.01f)
            mBrightness = 0.01f;

        // 显示
        vpb_left.setVisibility(View.VISIBLE);
        leftProgress = (int) (mBrightness * 100);
        vpb_left.setProgress(leftProgress);
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        leftProgress = leftProgress + (int) (percent * 100);
        if (leftProgress > 100) {
            leftProgress = 100;
        } else if (leftProgress < 0) {
            leftProgress = 0;
        }
        vpb_left.setProgress(leftProgress);
    }

    /**
     * 滑动改变系统亮度
     *
     * @param percent
     */
    public void onSystemBrightnessSlide(float percent) {
        try {
            mBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (mBrightness < 0) {
            mBrightness = 0;
        }
        // 显示  系统屏幕亮度最大值为255
        vpb_left.setVisibility(View.VISIBLE);
        leftProgress = (int) (mBrightness * 100 / 255);
        vpb_left.setProgress(leftProgress);

        int brightness = (int) (mBrightness + percent * 255);
        if (brightness > 255) {
            brightness = 255;
        } else if (brightness < 0) {
            brightness = 0;
        }
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);

        leftProgress = leftProgress + (int) (percent * 100);
        if (leftProgress > 100) {
            leftProgress = 100;
        } else if (leftProgress < 0) {
            leftProgress = 0;
        }
        vpb_left.setProgress(leftProgress);
    }

//获取系统亮度  Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//设置系统亮度  Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,systemBrightness);
/** 开启自动亮度调节后，改不了系统亮度(可以改变屏幕亮度)，要先关闭自动亮度调节 ***/
    /**
     * 停止自动亮度调节
     */

    public void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * * 开启亮度自动调节 *
     *
     * @param activity
     */

    public void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        currentPosition = mPreferences.getInt(filePath, 0);
        if (currentPosition > 0) {
// 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
            play(currentPosition);
            currentPosition = 0;
        } else {
            play(0);
        }
//必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
//        player = new MediaPlayer();
//        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        player.setDisplay(surfaceHolder);
//        //设置显示视频显示在SurfaceView上
//        try {
//            player.setDataSource(filePath);
//            player.prepare();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        player.start();
    }

    private void play(final int currentPosition) {
//        Log.i("MediaPlayer", "TEST");
//        if (!filePath.contains("http")) {
//            File file = new File(filePath);
//            if (!file.exists()) {
//                Toast.makeText(this, "视频文件丢失了", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
// 设置播放的视频源
        player.setDisplay(surfaceHolder);
        Log.e("filePath","filePath========"+filePath);
        try {


                player.setDataSource(filePath);


            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i("MediaPlayer", "player.getDuration()===="+player.getDuration());
                player.start();
// 按照初始位置播放
                player.seekTo(currentPosition);
// 设置进度条的最大进度为视频流的最大播放时长
                seekBar.setMax(player.getDuration());
                String max=cal(player.getDuration() / 1000);
                tv2.setText(max);
// 开始线程，更新进度条的刻度
                new Thread() {

                    @Override
                    public void run() {
                        try {
                            isPlaying = true;
                            while (isPlaying) {
                                int current = player
                                        .getCurrentPosition();
                                seekBar.setProgress(current);
//                                    tv1.setText(current+"");

                                Message msg = handler.obtainMessage();
                                msg.arg1 = current;
                                handler.sendMessage(msg);
                                sleep(500);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();


            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
// 在播放完毕被回调
//                pause.setBackgroundResource(R.drawable.start);
                player.start();

            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
// 发生错误重新播放
                play(0);
                isPlaying = false;
                return false;
            }
        });


    }

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
// 当进度条停止修改的时候触发
// 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (player != null) {
// 设置当前播放的位置
                player.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        if (player != null && player.isPlaying()) {
            player.stop();
        }

        try {
            currentPosition = player.getCurrentPosition();
        } catch (Exception e) {
            currentPosition=0;
            e.printStackTrace();
        }
//Log.e("Media","我已经取得"+currentPosition);

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(filePath, currentPosition);
        editor.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.v("Media","onDestroy");
        if (player!=null) {
            player.release();
        }


    }
    public static String cal(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        return h + ":" + d + ":" + s ;
    }


}
