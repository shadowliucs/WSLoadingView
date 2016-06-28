package com.ws.loadingview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 6/28 0028.
 */
public class WSLineProgress extends View {

    private Paint mPaint;
    private Context mContext;

    private int centerX, centerY;

    private int mRadius;

    private int mValue;

    private String text;

    private float fontWidth, fontHeight;

    private final static float RADIUS_RATIO = 2 / 3f;

    public WSLineProgress(Context context) {
        this(context, null);
    }

    public WSLineProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WSLineProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //处理 wrap_content问题
        int defaultDimension = dip2px(100);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultDimension, defaultDimension);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultDimension, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, defaultDimension);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        centerX = w / 2;
        centerY = h / 2;

        //处理padding情况
        mRadius = (int) (Math.min(centerX - getPaddingLeft(), centerX - getPaddingRight()) * RADIUS_RATIO);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(dip2px(2));
        mPaint.setTextSize(dip2px(20));

        text = mValue + "%";

        fontWidth = mPaint.measureText(text);
        fontHeight = getFontHeight(mPaint, text);

        if (mValue == 0) {
            canvas.drawText(text, centerX - mRadius, centerY + fontHeight / 2, mPaint);
            canvas.drawLine(centerX - mRadius + fontWidth, centerY, centerX + mRadius, centerY, mPaint);
        } else if (mValue >= 100) {
            canvas.drawText(text, centerX + mRadius - fontWidth, centerY + fontHeight / 2, mPaint);
            canvas.drawLine(centerX - mRadius, centerY, centerX + mRadius - fontWidth, centerY, mPaint);

        } else {

            float lineWidth = 2 * mRadius - fontWidth;
            //左边直线
            canvas.drawLine(centerX - mRadius, centerY, centerX - mRadius + (float) mValue / 100 * lineWidth, centerY, mPaint);
            //右边直线
            canvas.drawLine(centerX - mRadius + (float) mValue / 100 * lineWidth + fontWidth, centerY, centerX + mRadius, centerY, mPaint);
            //绘制文本
            canvas.drawText(text, centerX - mRadius + (float) mValue / 100 * lineWidth, centerY + fontHeight / 2, mPaint);

        }

    }

    //开始动画
    public void startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofInt(0, 101);
                animator.setDuration(5000);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mValue = (int) animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
                animator.start();
            }
        });
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    public float getFontHeight(Paint paint, String str) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.height();

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
