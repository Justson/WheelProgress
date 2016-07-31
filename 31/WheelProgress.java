package com.ucmap.just_upatch;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2016/7/31.
 * 作者:Justson 岑晓中
 */
public class WheelProgress extends View {

    /**
     * 条目的颜色
     */
    private int itemColor;
    /**
     * 条目宽度
     */
    private int itemWidth = 10;
    /**
     * 条目的圆角
     */
    private static float itemCornersRadius = 5f;
    /**
     * 画笔
     */
    Paint paint = null;

    //控件的宽和高
    private int WHEELWIDTH = 80;
    private int WHEELHEIGHT = 80;
    //用于计算屏幕的密度
    private static int DENSITY;

    //控件的实际宽高
    private int resultHeight;
    private int resultWidth;
    //用于绘制条目
    private static final RectF rectF = new RectF();
    //用于控制透明度，默认为完全不透明
    private static final int ALPHA = 255;
    //条目数量
    private static final int itemNumber = 12;
    private float centerX;
    private float centerY;
    //速度因子
    private int speed = 100;
    /**
     * 是否旋转
     */
    private boolean isRotate = true;
    /**
     * 控制画笔旋转度数
     */
    private int controler;
    /**
     * 用于控制自身是否旋转,默认为不旋转
     */
    private boolean oneSeftRotate = false;
    /**
     * 旋转动画
     */
    private ObjectAnimator objectAnimator;

    public WheelProgress(Context context) {
        this(context, null);
    }

    public WheelProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        itemColor = Color.WHITE; //默认为白色

        DENSITY = (int) context.getResources().getDisplayMetrics().density; //获取屏幕的密度

        WHEELHEIGHT *= DENSITY;
        WHEELWIDTH *= DENSITY;


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WheelProgress, defStyleAttr, 0);
        int count = ta.getIndexCount();//获取自定义所有属性个数
        for (int i = 0; i < count; i++) {
            int index = ta.getIndex(0);
            switch (index) {
                //获取自定义颜色
                case R.styleable.WheelProgress_itemColor:
                    itemColor = ta.getColor(index, Color.parseColor("#f1f2f3"));
                    break;
                //获取自定义条目个数
                case R.styleable.WheelProgress_itemWidth:
                    itemWidth = ta.getInteger(index, 12);
                    break;
                case R.styleable.WheelProgress_itemCornersRadius:
                    itemCornersRadius = ta.getFloat(index, 5f);
                    break;
                //获取Boolean 属性
                case R.styleable.WheelProgress_oneSeftRotate:
                    oneSeftRotate = ta.getBoolean(index, false);
                    Log.i("Info", "soneSeftRotate:" + oneSeftRotate);
                    break;
            }

        }
        ta.recycle();
        //初始化画笔
        paint = new Paint();
        paint.setColor(itemColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAlpha(255);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                resultWidth = WheelProgress.this.getMeasuredWidth();
                resultHeight = WheelProgress.this.getMeasuredHeight();
                WheelProgress.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initRectFProperty();
            }
        });
    }

    @Override //依附窗口的时候就开启自身旋转动画
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startOneSelfAnimator();
    }

    public void startOneSelfAnimator() {
        if (oneSeftRotate) {
            objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 359).setDuration(speed * 30 * 12);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限旋转
            objectAnimator.start();
        }
    }

    @Override //取消属性动画
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (objectAnimator != null && objectAnimator.isStarted()) {
            objectAnimator.cancel();
        }
    }

    //初始化要绘制条目 RectF  属性
    public void initRectFProperty() {

        int radius = resultWidth / 2;
        //记录圆心的位置， 用于旋转canvas
        centerX = radius;
        centerY = radius;

        int startPosition = (radius) - itemWidth / 2;

        int lineLength = (radius) / 2 - 5;

        rectF.left = startPosition;

        rectF.top = 0 + 15f;

        rectF.right = startPosition + itemWidth;

        rectF.bottom = rectF.top + lineLength;
    }

    @Override  //测量该控件
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        width = height = Math.min(width, height);     //两者之间取最小值
        this.setMeasuredDimension(widthMode == MeasureSpec.AT_MOST ? WHEELWIDTH : width, heightMode == MeasureSpec.AT_MOST ? WHEELHEIGHT : height);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        canvas.rotate(controler * 30f, centerX, centerY); //旋转画布
        controler++;
        if (controler == 12) {
            controler = 0;
        }
        for (int i = 0; i < itemNumber; i++) {

            canvas.drawRoundRect(rectF, itemCornersRadius, itemCornersRadius, paint);

            int tem = ALPHA - ((ALPHA / itemNumber) * i); //设置透明度

            if (tem <= 80) {
                tem = 80;
            }

            paint.setAlpha(tem);
            canvas.rotate(-30f, centerX, centerY);
        }
        if (isRotate) {
            postInvalidateDelayed(speed);
        }
    }


    /***************************************************
     * setter+getter
     *************************************************/
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isRotate() {
        return isRotate;
    }

    public void setRotate(boolean rotate) {
        oneSeftRotate = rotate;
        if (rotate) {
            startOneSelfAnimator();
        } else {
            cancelOneSelfAnimator();
        }
    }

    public void cancelOneSelfAnimator() {

        if (objectAnimator != null && objectAnimator.isStarted())
            objectAnimator.cancel();
    }

}
