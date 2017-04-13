package com.gmri.master.hottable;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gmri.master.hottable.entity.SettingBean;

import java.util.HashMap;
import java.util.Map;

/**
 *  参数设置界面
 *  author  xiaoQ
 */
public class SettingActivity extends Activity {


    private Resources mResource;

    private SettingBean settingBean;

    private ImageView backIv;

    Map<String, String> settingMap = new HashMap<>();

    private EditText tableNumEt, boilingTemEt;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = this;

        setView();

        setData();
    }

    /** 初始化数据 */
    private void setData() {
        settingBean = new SettingBean();
        settingMap.put("店铺名称", "");
        settingMap.put("桌子编号", "");
    }

    /**  初始化界面 */
    private void setView() {
        backIv = (ImageView) findViewById(R.id.backIv);
     //   mListView = (ListView) findViewById(R.id.settingLv);
        tableNumEt = (EditText) findViewById(R.id.tableNumEt);
        boilingTemEt = (EditText) findViewById(R.id.temperatureEt);
        boilingTemEt.setText(IndControlActivity.bollingTemperature + "");
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        boilingTemEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int bolliing;
                try{
                     bolliing = Integer.valueOf(boilingTemEt.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "请输入数字", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(bolliing > 100) {
                    Toast.makeText(mContext, "不能设置超过100℃", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(bolliing < 60) {
                    Toast.makeText(mContext, "不能设置低于60℃", Toast.LENGTH_SHORT).show();
                    return ;
                }

                IndControlActivity.bollingTemperature = bolliing;

            }
        });

    }



}
