package com.gmri.master.hottable;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.gmri.master.hottable.utils.CustomVideoView;


public class videoActivity extends Activity {
    private CustomVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_video);
        mVideoView = (CustomVideoView) findViewById(R.id.videoView);
        /** 隐藏华为系列底部虚拟按钮 */
        hideBottomUIMenu();
        /** 演示视屏 */
        initVideoView();
    }

    private void initVideoView() {
        //设置播放加载路径
        mVideoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.guide_video));
        //播放
        mVideoView.start();
        mVideoView.resume();
        //循环播放
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start();
            }
        });

        //触摸事件
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mVideoView.pause();
                Intent intent = new Intent(videoActivity.this, MainActivity.class);
                startActivity(intent);
                videoActivity.this.finish();
                return false;
            }
        });

    }

    @Override
    protected void onRestart() {
        mVideoView.start();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        mVideoView.destroyDrawingCache();
        super.onDestroy();

    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
