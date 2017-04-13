package com.gmri.master.hottable.utils;

/**
 * Created by xiaoQ on 2017/4/6.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gmri.master.hottable.entity.Variety;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/** 数据库操作类 */
public class SqliteHandler {
    static Context mContext;
    public SqliteHandler() {};

    SQLiteDatabase db;
    private static  SqliteHandler sqlHandler;

    public static  synchronized SqliteHandler getInstance(Context ctx) {
        if(ctx != null) {
            mContext = ctx;
        }

        if(sqlHandler == null) {
            synchronized (SqliteHandler.class) {

                sqlHandler = new SqliteHandler();

            }
        }

        return sqlHandler;
    }

    private final static String DATABASE = "HotTable.db";   //数据库名称
    /** 创建表 */
    public void createTable() {
        db = mContext.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null);
        /** 菜品表 */
        String varietyName = "create table if not exists varietyName (name varchar(50), countDown Integer, time Integer)";   // countDown = 0 没有设置额倒计时 countDown = 1 设置了倒计时
        try{
            db.execSQL(varietyName);
            Log.i("databases", "数据库创建成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase(db);
        }

    }


    /** 获取所有菜品信息 */
    public List<Variety> getAllVariety() {
        List<Variety> varietyList = new ArrayList<>();
        Cursor mCursor = null;
        try{

            db = mContext.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null);
            mCursor = db.rawQuery("select * from varietyName", null);
            while(mCursor.moveToNext()) {
                Variety variety = new Variety();
                if(mCursor.getInt(mCursor.getColumnIndex("countDown")) == 0) {
                    variety.setCountDown(false);
                } else {
                    variety.setCountDown(true);
                }
                variety.setVarietyName(mCursor.getString(mCursor.getColumnIndex("name")));
                variety.setTime(mCursor.getInt(mCursor.getColumnIndex("time")));
                varietyList.add(variety);
            }
            return varietyList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(mCursor);
            closeDatabase(db);
        }
        return null;
    }

    /** 存储菜品信息 */
    public void saveVariety(Variety variety) {
        try{
            db = mContext.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null);
            String countDownStr;
            if(variety.getCountDown()) {
                countDownStr = "1";
            } else {
                countDownStr = "0";
            }
            db.execSQL("insert into varietyName (name , countDown , time) values" +
                    "(?, ?, ?)", new String[]{ variety.getVarietyName(), countDownStr ,  variety.getTime() + ""});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase(db);
        }
    }

    /** 修改菜品信息 */
    public void updateVariety(Variety variety) {
        ContentValues cv = new ContentValues();
        try{
            db = mContext.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null);
            int countDown;
            if(variety.getCountDown()) {
                countDown = 1;
            } else {
                countDown = 0;
            }
            cv.put("countDown", countDown);
            cv.put("time", variety.getTime());
            db.update("varietyName", cv, "name = " + variety.getVarietyName(), null);
        }  catch (Exception e) {
            Log.i("databases", "更新数据出错！");
        } finally {
            closeDatabase(db);
        }
    }


    /** 删除菜品信息 */
    public boolean deleteVariety(Variety variety) {
        try{
            db = mContext.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null);
            db.delete("varietyName", "name = " + variety.getVarietyName(), null );
            return  true;
        } catch (Exception e) {
            Log.i("", "删除菜品出错！");
            e.printStackTrace();
        } finally {
            closeDatabase(db);
        }
        return false;
    }

       /** 关闭Cursor */
    public void closeCursor(Cursor mCursor) {
        try{
            if(mCursor != null) {
                mCursor.close();
            }
        } catch (Exception e) {
            Log.i("database", "关闭Cursor出错！");
            e.printStackTrace();
        }

    }


    /** 关闭DataBase */
    public void closeDatabase(SQLiteDatabase db) {
        try{
            if(db != null) {
                db.close();
            }
        } catch(Exception e) {
            Log.i("database", "关闭数据库出错！");
            e.printStackTrace();
        }
    }

    /** 关闭流 */
    public void closeStream(Closeable mCloseable) {
        try{
            if(mCloseable != null) {
                mCloseable.close();
            }
        } catch (Exception e){
            Log.i("database", "流关闭异常!");
            e.printStackTrace();
        }
    }


}





















