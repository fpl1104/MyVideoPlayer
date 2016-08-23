package com.example.fpl1104.myvideoplayer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fpl1104.myvideoplayer.Interface.Fragment3;
import com.example.fpl1104.myvideoplayer.MainActivity;
import com.example.fpl1104.myvideoplayer.MyUtil.ArrowDownloadButton;
import com.example.fpl1104.myvideoplayer.R;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by fpl1104 on 16/6/13.
 */
public class MyFragment3 extends Fragment {
    int count = 0;
    int progress = 0;
    ArrowDownloadButton button;
    private Fragment3 fragment3;

android.os.Handler handler=new android.os.Handler();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.view3,null);
        button = (ArrowDownloadButton) view.findViewById(R.id.arrow_download_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((count % 2) == 0) {
                    button.startAnimating();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progress = progress + 1;
                                    button.setProgress(progress);


                                }
                            });
                        }
                    }, 800, 20);
                } else {
                    button.reset();
                }
                count++;
            }
        });
        return view;
    }





}
