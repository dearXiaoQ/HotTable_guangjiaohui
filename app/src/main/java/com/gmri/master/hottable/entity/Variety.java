package com.gmri.master.hottable.entity;

/**
 * Created by xiaoQ on 2017/4/6.
 */

/** 菜品实体类 */
public class Variety {

    public Variety(boolean countDown, int time, String varietyName) {
        this.countDown = countDown;
        this.time = time;
        this.varietyName = varietyName;
    }
    public Variety() {}

    /** 倒计时功能 */
    private boolean countDown;
    /** 倒计时时间 */
    private int time;
    /** 菜品名称 */
    private String varietyName;


    public void setCountDown(boolean countDown) {
        this.countDown = countDown;
    }

    public boolean getCountDown() {
        return countDown;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setVarietyName(String name) {
        this.varietyName = name;
    }

    public String getVarietyName() {
        return varietyName;
    }

}
