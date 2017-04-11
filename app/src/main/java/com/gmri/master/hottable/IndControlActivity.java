package com.gmri.master.hottable;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class IndControlActivity extends Activity {

    //使用缓存的设备列表刷新UI
    List<GizWifiDevice> devices = GizWifiSDK.sharedInstance().getDeviceList();

    //接收设备列表变化上报，刷新UI
    GizWifiSDKListener mListener = new GizWifiSDKListener() {
        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
            //提示错误原因
            if (result != GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Log.d("GizDeviceError", "result：" + result.name());
            }
            //显示变化后的设备列表
            Log.i("GizDeviceInfo", "Discovered deviceList：" + deviceList);
            devices = deviceList;
        }

    };


    /**
     * 机智云设备监听
     */
    private GizWifiDeviceListener mDeviceListener = new GizWifiDeviceListener() {
        @Override
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int sn) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 如果App不使用sn，此处不需要判断sn
                if (sn == INDCOOKER_SN) {
                    String mataMapStr = dataMap.toString();
                    Log.i("电磁炉控制指令反馈", "dataMap = " + mataMapStr);
                } else {
                    Log.i("dataMap", "dataMap = " + dataMap.toString());
                    if (dataMap.get("data") != null) {
                        ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
                        // 普通数据点，打印对应的key和value
                        StringBuilder sb = new StringBuilder();
                        for (String key : map.keySet()) {
                            if (key.equals(temperatureStr)) {
                                temperture = Float.valueOf(map.get(key).toString());
                            }
                            if (key.equals(statusStr)) {
                                status = (int) map.get(key);
                            }
                            sb.append(key + "  :" + map.get(key) + "\r\n");
                        }
                        Toast.makeText(IndControlActivity.this,
                                sb.toString(), Toast.LENGTH_SHORT).show();
                        Log.i("电磁炉控制指令反馈data ", "data= " + sb.toString());

                        freshViewData(status, temperture);

                    }
                }
            }
        }


    };

    /**
     * 刷新控制页面的数据
     */
    private void freshViewData(int statusOrginn, float temperture) {
        Log.i("status", "status = " + status);
        status = statusOrginn ;
        if(statusOrginn > 0) {
            stickySwitch.setDirection(StickySwitch.Direction.RIGHT);
            mSwitch.setChecked(true);
        } else {
            stickySwitch.setDirection(StickySwitch.Direction.LEFT);
            mSwitch.setChecked(false);
        }

        controlView.GizOnCallRefreshData(statusOrginn);
        if(statusOrginn == 1) {
            statusOrginn = 0;
        }

        if(statusOrginn  > 1) {
            statusOrginn = statusOrginn - 1;
        }

        controlView.setCurrentCount(statusOrginn);
        handlerReciverData(temperture);


    }

    /** 倒计时 */
    private void handlerReciverData(float temperature) {
        Log.i("Thread", " threadAlreadyStart = " + threadAlreadyStart + "\n OPEN_COUNT_DOWN = " +  OPEN_COUNT_DOWN + "\n countDownList.size()" + countDownList.size());
        if(status > 0 && countDownList.size() > 0) { //temperature >= bollingTemperature && status > 0 && countDownList.size() > 0
            if( !(threadAlreadyStart) && OPEN_COUNT_DOWN) {
                try {
                    OPEN_COUNT_DOWN = false;
                    new TimerThread().start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {

        }
    }


    /**
     * 机智云电磁炉produckkey
     */
    static final String INDUCTION_COONER_PRODUCT_KEY = "3b78444a05214648b54156c2b85db900";
    /**
     * 电磁炉指令sn
     */
    public static final int INDCOOKER_SN = 10;

    static List<Variety> varietyList;

    static List<Variety> countDownList = new ArrayList<>();

    private Context mContext;

    private ProgressView controlView;
    private Button testBtn;
    private int value;
    private Random random = new Random(System.currentTimeMillis());

    /** 沸腾温度 */
    public static int bollingTemperature = 89;

    SqliteHandler sqlHandler;

    private Switch mSwitch;

    private ImageView backIv;

    private Button refreshBtn;

    private boolean threadAlreadyStart = false;

    private ImageButton addBtn;
    private ImageButton subBtn;

    private MultiSelectSpinner spinner;

    private StickySwitch stickySwitch;

    private Handler mHandler;

    private static final int SHOW_COUNTIME_MESSAGE = 1;

    private ArrayAdapter<String> spinnerAdapter;
    /** 下拉菜单数据源 */
    List<String> spinnerData = new ArrayList<>();


    public static final String deviceTipInfo = "没找到电磁炉！";
    /** 智能电磁炉返回的数据 */
    /**
     * dataMap = {data={temperature=25.5, power_level=0, power=true, increase=true, decrease=true, status=0}}
     */
    public static final String temperatureStr = "temperature";
    public static final String statusStr = "status";
    public static final String powerStr = "power";
    private float temperture = 0;
    private boolean power = false;
    private int status = 0;

    static final String RIGHT = "RIGHT";
    static final String LEFT = "LEFT";

    /** 倒计时设定时间 */
    public static long COUNT_DOWN = 5000;

    /** 是否退出倒计时标志位 */
    boolean CANCEL_TIMER = false;
    /** 是否启用 */
    boolean OPEN_COUNT_DOWN = true;

    private TimerThread mTimerThread = new TimerThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ind_control);
        mContext = this;
        setHandler();
        GizWifiSDK.sharedInstance().setListener(mListener);
        initData();
        setView();
    }

    /** 初始化数据 */
    private void initData() {
        sqlHandler = SqliteHandler.getInstance(mContext);
        varietyList = sqlHandler.getAllVariety();
    }


    @Override
    protected void onResume() {
        super.onResume();
        OPEN_COUNT_DOWN = true;
    }


    /**  获取电磁炉数据 */
    private void setInitThread() {
        new Thread(){
            @Override
            public void run() {
                boolean isOk = false;
                while (!isOk) {
                    Log.i("Thread", "已经进入线程！");
                    try{
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /** 直到有设备绑定成功才退出初始化线程 */
                    if (!(devices.size() == 0)) {
                        Log.i("Thread", "绑定设备成功！");
                        GizWifiDevice dev = devices.get(0);
                        dev.setSubscribe(true);
                        controlIndCook(dev, "increase", true, INDCOOKER_SN);
                        isOk = true;
                    }

                    Log.i("Thread", "已经退出了初始化线程！");
                }
            }
        }.start();
    }

    /** setHandler */
    private void setHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_COUNTIME_MESSAGE:
                        Toast.makeText(mContext, (String)msg.obj + "煮熟了！", Toast.LENGTH_SHORT).show();
                        //科大语音合成
                        setSound((String)msg.obj);
                        break;

                    case 2:
                        spinner.setEmpty();
                        break;

                }
            }
        };
    }


    /** 科大讯飞合成语音提醒 */
    private  void  setSound(String info) {
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(mContext, null);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        mTts.setParameter(SpeechConstant.SPEED, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "80");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);//设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        mTts.startSpeaking(info + "熟了，请慢用！", mSynListener);
    }

    private void setView() {
        backIv = (ImageView) findViewById(R.id.backIv);
        controlView = (ProgressView) findViewById(R.id.controlProgressView);
        addBtn = (ImageButton) findViewById(R.id.addBtn);
        subBtn = (ImageButton) findViewById(R.id.subBtn);
        refreshBtn = (Button) findViewById(R.id.refreshBtn);

        spinner = (MultiSelectSpinner) findViewById(R.id.spinner);
        mSwitch = (Switch) findViewById(R.id.mSwitch);

        testBtn = (Button) findViewById(R.id.testBtn);
        controlView.setMaxCount(9.0f);
        value = random.nextInt(10) ;
        //  controlView.setCurrentCount(value);
        controlView.GizOnCallRefreshData(status);

        stickySwitch = (StickySwitch) findViewById(R.id.stickySwitch);
        initStickySwitch();
        testBtn.setText("off");

        /** 控制动作监听 */
        setControlLinstener();
        /** spinnerAdapter */
        setSpinnerAdapter();
        /** 初始化控制设备 */
        setInitThread();
    }

    /** 电磁炉控制按钮监听 */
    private void setControlLinstener() {

        /** 返回按钮 */
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndControlActivity.this.finish();
            }
        });

        /** 开关按钮 */
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (devices.size() == 0) {
                    Toast.makeText(IndControlActivity.this, deviceTipInfo, Toast.LENGTH_SHORT).show();
                    return;
                }
                GizWifiDevice dev = devices.get(0);
                dev.setSubscribe(true);
                controlIndCook(dev, "power", true, INDCOOKER_SN);
                //    private void contorlIndCook(GizWifiDevice gizDev, String instructions, Object controlSwitch, int sn){
            }
        });


        /**增加功率 */
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (devices.size() == 0) {
                    Toast.makeText(IndControlActivity.this, deviceTipInfo, Toast.LENGTH_SHORT).show();
                    return;
                }
                GizWifiDevice dev = devices.get(0);
                /** 绑定设备 */
                dev.setSubscribe(true);
                if (value < 90) {
                    value = value + 10;
                }
                controlIndCook(dev, "increase", true, INDCOOKER_SN);

            }
        });


        /**减少功率 */
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (devices.size() == 0) {
                    Toast.makeText(IndControlActivity.this, deviceTipInfo, Toast.LENGTH_SHORT).show();
                    return;
                }
                GizWifiDevice dev = devices.get(0);
                /** 绑定设备 */
                dev.setSubscribe(true);
                if (value >= 10) {
                    value = value - 10;
                }
                controlIndCook(dev, "decrease", true, INDCOOKER_SN);

            }
        });

        /** 主动刷新设备 */
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> pks = new ArrayList<String>();
                pks.add(INDUCTION_COONER_PRODUCT_KEY);
                GizWifiSDK.sharedInstance().getBoundDevices("", "", pks);
            }
        });

    }

    /** 初始化stickySwitch */
    private void initStickySwitch() {
        stickySwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String text) {
                Log.i("Switch", "Now selected：" + direction.name() + "，current Text：" + text);
                if(direction.name() == RIGHT) {
                    stickySwitch.setSwitchColor(IndControlActivity.this.getResources().getColor(R.color.pinkSwitchColor));
                } else {
                    stickySwitch.setSwitchColor(IndControlActivity.this.getResources().getColor(R.color.graySwitchColor));
                }
            }
        });

        stickySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("isChecked", "mSwitch = "  + mSwitch.isChecked());
                if (devices.size() == 0) {
                    Toast.makeText(IndControlActivity.this, deviceTipInfo, Toast.LENGTH_SHORT).show();
                    return;
                }
                GizWifiDevice dev = devices.get(0);
                dev.setSubscribe(true);
                controlIndCook(dev, "power", true, INDCOOKER_SN);
            }
        });

    }


    private void setSpinnerAdapter() {
        Log.i("Thread", "初始化数据！");
        int size = varietyList.size();
        if(varietyList != null) {
            for(int i = 0 ; i < size; i ++) {
                Variety variety = varietyList.get(i);
                spinnerData.add(variety.getVarietyName());
            }
        }
        if(spinnerData.size() > 0) {
           spinner.setItems(spinnerData);
            List<String> selected = spinner.getSelectedStrings();
        }


    }

    /**
     * 电磁炉控制方法
     */
    private void controlIndCook(GizWifiDevice gizDev, String instructions, Object controlSwitch, int sn) {

        // gziDev是设备实体对象
        gizDev.setListener(mDeviceListener);
        ConcurrentHashMap<String, Object> command = new ConcurrentHashMap<>();
        command.put(instructions, controlSwitch);
        gizDev.write(command, sn);
        Log.i("control instruction", "instructions = " + instructions + "  switch = " + controlSwitch + "  sn = " + INDCOOKER_SN);
    }



    /** 定时关火线程实现 */
    class TimerThread extends  Thread {
        @Override
        public void run() {
            threadAlreadyStart = true;
            Log.i("Thread", "进入倒计时方法！");
            //initVarietyData();
            long startTime = System.currentTimeMillis();
            boolean interrupt = false;
            while ((!interrupt) && !CANCEL_TIMER) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long currentTime = System.currentTimeMillis();
                long sub = currentTime - startTime;

                int size = countDownList.size();
                for(int i = 0; i < size; i++) {

                    Variety variety = countDownList.get(i);
                    int countTime = variety.getTime();
                    if(sub > countTime) { /** 定时时间已到 */
                        Message message = mHandler.obtainMessage();
                        message.obj = variety.getVarietyName();
                        message.what = SHOW_COUNTIME_MESSAGE;
                        mHandler.sendMessage(message);
                        countDownList.remove(variety);
                        i = 0;
                        size = size - 1;
                    }

                }

                if(size == 0) {
                    mHandler.sendEmptyMessage(2);
                    if (devices.size() == 0) {
                        Toast.makeText(IndControlActivity.this, deviceTipInfo, Toast.LENGTH_SHORT).show();
                    } else {
                        GizWifiDevice dev = devices.get(0);

                        dev.setSubscribe(true);
                        Log.i("Thread", "发送关机指令！ status = " + status);
                        if(status > 0) {
                            controlIndCook(dev, "power", true, INDCOOKER_SN);
                        }
                    }
                    OPEN_COUNT_DOWN = true;
                    interrupt = true;
                }


            }
            threadAlreadyStart = false;
            Log.i("Thread", "退出线程！");
        }



 /*       private void initVarietyData() {
            int size = varietyList.size();
            for(int i = 0; i < size; i ++) {
                Variety variety = varietyList.get(i);
                if(variety.getCountDown()) {
                    countDownList.add(variety);
                }
            }
        }*/
    }


    @Override
    protected void onPause() {
        super.onPause();
        OPEN_COUNT_DOWN = false;
    }

    /** 科大讯飞监听器 */
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

}





















