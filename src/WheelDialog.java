package com.ucmap.just_upatch;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/7/31.
 * 作者 : Justson  岑晓中
 */
public class WheelDialog extends Dialog {

    private TextView message;
    private WheelProgress wheelProgress;

    public WheelDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public WheelDialog(Context context, int res, Builder builder) {
        super(context, res);
        init(context);
        if (builder.message != null) {
            message.setText(builder.message);
        }
        if (builder.isOnSelfRotate != null) {
            wheelProgress.setRotate(builder.isOnSelfRotate);
        }
        if (builder.speed != null) {
            wheelProgress.setSpeed(builder.speed);
        }
    }

    private void init(Context context) {

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);

        getWindow().setBackgroundDrawable(new BitmapDrawable());

        this.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View contentView = layoutInflater.inflate(R.layout.dialog_wheel, null);

        this.setContentView(contentView);

        wheelProgress = (WheelProgress) contentView.findViewById(R.id.wheel);
        wheelProgress.setSpeed(150);
        wheelProgress.setRotate(false);

        message = (TextView) contentView.findViewById(R.id.message);
        message.setText("内容");
        message.setTextColor(Color.WHITE);
        message.setTextSize(16);
    }

    public void onDismiss() {
        this.dismiss();
    }

    public WheelDialog onShow() {
        this.show();
        return this;
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setOneSelfRotate(boolean tag) {
        wheelProgress.setRotate(tag);
    }


    static class Builder {

        String message;
        Boolean isOnSelfRotate;

        Integer speed;
        Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setSpeed(int speed) {
            this.speed = speed;
            return this;
        }

        public Builder setOnSelfRotate(boolean onSelfRotate) {
            isOnSelfRotate = onSelfRotate;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public WheelDialog onShow() {
            return new WheelDialog(context, R.style.WheelDialog, this).onShow();
        }
    }
}
