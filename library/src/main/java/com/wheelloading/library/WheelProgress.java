package com.wheelloading.library;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
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
    private float itemWidth = 3;
    /**
     * 条目的圆角
     */
    private static float itemCornersRadius = 5f;
    /**
     * 画笔
     */
    Paint paint = null;
    /**
     * 控件的宽和高
     */
    private int WHEEL_WIDTH = 80;
    /**
     * 控件的宽和高
     */
    private int WHEEL_HEIGHT = 80;
    /**
     * 用于计算屏幕的密度
     */
    private static int DENSITY;
    /**
     * 控件的实际宽高
     */
    private int resultHeight;
    private int resultWidth;
    /**
     * 用于绘制条目
     */
    private static final RectF rectF = new RectF();
    /**
     * 用于控制透明度，默认为完全不透明
     */
    private static final int ALPHA = 255;
    /**
     * 条目数量
     */
    private static final int itemNumber = 12;
    /**
     * 圆心位置
     */
    private float centerX;
    /**
     * 圆心位置
     */
    private float centerY;
    /**
     * 速度因子
     */
    private int speed = 100;
    /**
     * 是否旋转
     */
    private boolean isRotate = true;
    /**
     * 控制画笔旋转度数
     */
    private int controller;
    /**
     * 用于控制自身是否旋转,默认为不旋转
     */
    private boolean oneSeftRotate = false;
    /**
     * 旋转动画
     */
    private ObjectAnimator objectAnimator;

    public static final String TAG = WheelProgress.class.getSimpleName();

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
        itemColor = Color.WHITE;
        DENSITY = (int) context.getResources().getDisplayMetrics().density; //获取屏幕的密度
        WHEEL_HEIGHT *= DENSITY;
        WHEEL_WIDTH *= DENSITY;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WheelProgress, defStyleAttr, 0);
        itemWidth *= DENSITY;
        int index = ta.getIndex(0);
        itemColor = ta.getColor(R.styleable.WheelProgress_itemColor, Color.parseColor("#f1f2f3"));
        itemWidth = ta.getDimension(R.styleable.WheelProgress_itemWidth, itemWidth);
        itemCornersRadius = ta.getFloat(R.styleable.WheelProgress_itemCornersRadius, 5f);
        oneSeftRotate = ta.getBoolean(R.styleable.WheelProgress_oneSeftRotate, false);
        ta.recycle();
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (objectAnimator != null && objectAnimator.isStarted()) {
            objectAnimator.cancel();
        }
    }

    /**
     * 初始化要绘制条目 RectF 属性
     */
    public void initRectFProperty() {
        int radius = resultWidth / 2;
        //记录圆心的位置， 用于旋转canvas
        centerX = radius;
        centerY = radius;
        float startPosition = ((radius) - itemWidth / 2);
        float lineLength = ((radius) / 2.4F);
        rectF.left = startPosition;
        rectF.top = 0 + radius / 3.5F;
        rectF.right = startPosition + itemWidth;
        rectF.bottom = rectF.top + lineLength;
    }

    /**
     * 测量控件
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        width = height = Math.min(width, height);
        this.setMeasuredDimension(widthMode == MeasureSpec.AT_MOST ? WHEEL_WIDTH : width, heightMode == MeasureSpec.AT_MOST ? WHEEL_HEIGHT : height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 旋转画布
         */
        canvas.rotate(controller * 30f, centerX, centerY);
        controller++;
        if (controller == 12) {
            controller = 0;
        }
        for (int i = 0; i < itemNumber; i++) {
            /**
             * 设置透明度
             */
            canvas.drawRoundRect(rectF, itemCornersRadius, itemCornersRadius, paint);
            int alphaValue = ALPHA - ((ALPHA / itemNumber) * i);
            if (alphaValue <= 80) {
                alphaValue = 80;
            }
            paint.setAlpha(alphaValue);
            canvas.rotate(-30f, centerX, centerY);
        }
        if (isRotate) {
            postInvalidateDelayed(speed);
        }
    }


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
        if (objectAnimator != null && objectAnimator.isStarted()) {
            objectAnimator.cancel();
        }
    }

}
