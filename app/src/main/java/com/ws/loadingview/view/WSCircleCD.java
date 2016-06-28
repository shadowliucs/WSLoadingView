package com.ws.loadingview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by Administrator on 6/28 0028.
 */
public class WSCircleCD extends View {

    private Paint mPaint;
    private Context mContext;

    private int circleCenterX, circleCenterY;

    private int circleRadius;

    private final static float RADIUS_RATIO = 2 / 3f;

    public WSCircleCD(Context context) {
        this(context, null);
    }

    public WSCircleCD(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WSCircleCD(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
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

        circleCenterX = w / 2;
        circleCenterY = h / 2;

        //处理padding情况
        circleRadius = (int) (Math.min(Math.min(circleCenterY - getPaddingTop(), circleCenterY - getPaddingBottom()),
                Math.min(circleCenterX - getPaddingLeft(), circleCenterX - getPaddingRight())) * RADIUS_RATIO);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(dip2px(2));
        canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, mPaint);//画大圆

        canvas.drawCircle(circleCenterX, circleCenterY, dip2px(4), mPaint);//画中心小圆圆

        // 下面是画弧线
        RectF rectF = new RectF(circleCenterX - circleRadius * RADIUS_RATIO, circleCenterY - circleRadius * RADIUS_RATIO,
                circleCenterX + circleRadius * RADIUS_RATIO, circleCenterY + circleRadius * RADIUS_RATIO);

        canvas.drawArc(rectF, 0, 80, false, mPaint);
        canvas.drawArc(rectF, 180, 80, false, mPaint);


        rectF = new RectF(circleCenterX - circleRadius / 2, circleCenterY - circleRadius / 2,
                circleCenterX + circleRadius / 2, circleCenterY + circleRadius / 2);

        canvas.drawArc(rectF, 0, 80, false, mPaint);
        canvas.drawArc(rectF, 180, 80, false, mPaint);

    }

    //开始动画
    public void startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {
                RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(-1);
                animation.setDuration(1000);
                animation.setFillAfter(true);
                startAnimation(animation);
            }
        });
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
