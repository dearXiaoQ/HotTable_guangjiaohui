package com.gmri.master.hottable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;


public class MainActivity extends Activity  {
    public static final String KEY_FIRST_START = "is_first_start";//标记是否第一次打开的key

    private ImageView QEcodeIV;

    private Button qrBtn,settingBtn;
    private Button ctrlViewBtn;


    private static int QRcodeWidth  = 512;
    private static int QRcodeHeight = 512;

    private Context mContext;

    boolean isFirstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        GizWifiSDK.sharedInstance().startWithAppID(this,"41024c7baa0e431fbd64d4a181db4284");
        //初始化控件
        setView();
        setDatabases();
        /** 科大讯飞 */
        initXFyun();
    }

    SqliteHandler sqlHandler ;
    /** 初始化数据库 */
    private void setDatabases() {
        isFirstStart = CacheUtils.getBoolean(this, KEY_FIRST_START, true);
        sqlHandler = SqliteHandler.getInstance(mContext);
        sqlHandler.createTable();
        if(isFirstStart) { //第一次打开
            /** 填充数据 */
            setData();
            CacheUtils.setBoolean(this, KEY_FIRST_START, false);

        }
    }

    /** 填充菜品数据 */
    private void setData() {
        sqlHandler.saveVariety(new Variety( true, 15000, "鸡翅尖"));
        sqlHandler.saveVariety(new Variety( true, 20000, "饺子"));
        sqlHandler.saveVariety(new Variety( true, 25000, "羊肉"));
        sqlHandler.saveVariety(new Variety( true, 30000, "大虾"));
        sqlHandler.saveVariety(new Variety( true, 35000, "鸡肉"));
        sqlHandler.saveVariety(new Variety( true, 45000, "排骨"));
        sqlHandler.saveVariety(new Variety( true, 60000, "香肠"));
        sqlHandler.saveVariety(new Variety( true, 65000, "鸡中翅"));

    }

    private void initXFyun() {
        // 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID +"=58e5bc6f");
    }


    private void setView() {
        QEcodeIV = (ImageView) findViewById(R.id.QRiv);
        settingBtn = (Button) findViewById(R.id.settingBtn);
        ctrlViewBtn = (Button) findViewById(R.id.controlPageBtn);


        ctrlViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IndControlActivity.class);
                startActivity(intent);
            }
        });


        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.master_icon);
        Bitmap qrBitmap = QRcode.createQRcode("火锅线测试_1号桌", QRcodeWidth, QRcodeHeight);
        Bitmap bitmap = QRcode.addLogo(qrBitmap, logoBitmap);
        QEcodeIV.setImageBitmap(bitmap);


    }


}
