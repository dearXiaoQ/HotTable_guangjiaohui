package com.gmri.master.hottable.entity;

/**
 * Created by mars on 2017/3/1.
 */

public class SettingBean {

    /** 门店名 */
    private String storeName;

    /** 台号 */
    private String tableNo;

    public SettingBean(){
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getTableNo() {
        return tableNo;

    }
}
