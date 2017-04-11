package com.gmri.master.hottable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by xiaoQ on 2017/3/13.
 */
/** 圆环控制 */
public class ProgressView extends View {

    //分段颜色
    private static final int[] SECTION_COLORS = {
            0xff0000ff, 0xffff0000, 0xffcd2626
    };
    /** 电磁炉档位 */
    private static final String[] IndCooker_LEVEL = {
            "off", "on", "400W", "600W", "800W", "1000W", "1200W", "1400W", "1600W", "1800W", "2100W"
    };

    private float maxCount;
    private float currentCont;
    private int score;
    private String currentLevel;
    private Paint mPaint;
    private Paint mBgPaint;
    private Paint mTextPaint;
    private int mWidth, mHeight;

    public ProgressView(Context context, AttributeSet attrs, int defStlyleAttr) {
        super(context, attrs, defStlyleAttr);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context) {this(context, null);}

    /** 初始化参数 */
    private void init() {
        mPaint = new Paint();
        mTextPaint = new Paint();
        mBgPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        RectF rectBlackBg = new RectF(20, 20, mWidth - 20, mHeight -20);
        canvas.drawArc(rectBlackBg, 0, 360, false, mPaint);
        mPaint.setColor(Color.BLACK);
       // canvas.drawText(score + "W", mWidth / 2, mHeight / 2 + 50, mTextPaint);

        //文字
        mTextPaint.setTextSize(120);
        if(currentLevel != null) {
            canvas.drawText(currentLevel, mWidth / 2, mHeight / 2 + 50, mTextPaint);
        }
        float section = currentCont / maxCount;
        Log.i("currentCont", "currentCont = " + currentCont + "\nmaxCount = " + maxCount) ;
        if(section <= 1.0f / 3.0f){
            if(section != 0.0f) {
                mPaint.setColor(SECTION_COLORS[0]);
            } else {
                mPaint.setColor(Color.TRANSPARENT);
            }
        }else {
            int count = (section <= 1.0f /3.0f * 2) ? 2 : 3;
            int[] colors = new int[count];
            System.arraycopy(SECTION_COLORS, 0, colors, 0, count);
            float[] positions = new float[count];
            if(count == 2) {
                positions[0] = 0.0f;
                positions[1] = 1.0f - positions[0];
            } else {
                positions[0] = 0.0f;
                positions[1] = (maxCount / 3) / currentCont;
                positions[2] = 1.0f - positions[0] * 2;
            }
            positions[positions.length - 1] = 1.0f;
            LinearGradient shader = new LinearGradient(3, 3, (mWidth - 3)
                    * section, mHeight - 3, colors, null,
                    Shader.TileMode.MIRROR);
            mPaint.setShader(shader);
        }
        //圆环底色
        mBgPaint.setColor(Color.GRAY);
        canvas.drawArc(rectBlackBg, 140, 260, false, mBgPaint);
        canvas.drawArc(rectBlackBg, 140, section * 260, false, mPaint);
    }

    private void initPaint() {
        mPaint.setAntiAlias(true); //抗锯齿
        mPaint.setStrokeWidth((float)40.0); //设置空心线宽
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.TRANSPARENT);

        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeWidth((float)40.0);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setColor(Color.TRANSPARENT);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth((float)3.0);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(50);
        mTextPaint.setColor(getResources().getColor(R.color.pinkSwitchColor));

    }


    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >=0 ? 1 : -1));
    }


    public int getScore() {return score;}

    public String getCurrentLevel() {
        return  currentLevel;
    }


    public void setCurrentLevel(String currentLevel) {this.currentLevel = currentLevel;}

    public float getMaxCount() {return maxCount;}

    public float getCurrentCount() {return currentCont;}


    public void setScore(int score) {
        this.score = score;
        int index;
        if(score > 80) {
            index = 10;
        } else if(score > 70 && score <= 80) {
            index = 9;
        } else if(score > 60 && score <= 70) {
            index = 8;
        } else if(score > 50 && score <= 60) {
            index = 7;
        } else if(score > 40 && score <= 50) {
            index = 6;
        } else if(score > 30 && score <= 40) {
            index = 5;
        } else if(score > 20 && score <= 30) {
            index = 4;
        } else if(score > 10 && score <= 20) {
            index = 3;
        } else if(score > 0 && score <= 10) {
            index = 2;
        } else {
            index = 0;
        }
        Log.i("index", "index = " + index + "\n score = " + score);
        this.currentLevel =  IndCooker_LEVEL[index];
        invalidate();
    }

    /** 机智云回调刷新函数 */
    public void GizOnCallRefreshData(int status) {

        this.currentLevel =  IndCooker_LEVEL[status];
        this.score = status;
        invalidate();
    }

    /**
     *  设置最大的进度值
     *
     * @param maxCount
     */
    public void setMaxCount(float maxCount){this.maxCount = maxCount;}


    public void setCurrentCount(float currentCount) {
        this.currentCont = currentCount > maxCount ? maxCount : currentCount;
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY
                || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST
                || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(15);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(mWidth, mHeight);

    }
}


























