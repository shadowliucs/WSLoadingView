package com.ws.loadingview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Administrator on 6/28 0028.
 */
public class WSGears extends View {

    private Paint mPaint;
    private Context mContext;

    private int centerX, centerY;

    private int outCircleRadius;  //外圆半径

    private int inCircleRadius;  //内圆半径

    private int moveAngle;   //移动角度

    float mAnimatedValue;

    private final static float RADIUS_RATIO = 2 / 3f;

    //内圆占外圆比
    private final static float IN_CIRCLE_RATIO = 1 / 2f;

    private final static float GEAR_RADIUS_RATIO = 1 / 16f;

    public WSGears(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public WSGears(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WSGears(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
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
        outCircleRadius = (int) (Math.min(Math.min(centerY - getPaddingTop(), centerY - getPaddingBottom()),
                Math.min(centerX - getPaddingLeft(), centerX - getPaddingRight())) * RADIUS_RATIO);

        inCircleRadius = (int) (outCircleRadius * IN_CIRCLE_RATIO);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(outCircleRadius * GEAR_RADIUS_RATIO);

        //60 是偏移量    绘制直线
        for (int i = 0; i < 360 / 120; i++) {
            canvas.drawLine(centerX, centerY, (float) (centerX + outCircleRadius * Math.sin(Math.toRadians(i * 120 + 60))),
                    (float) (centerY + outCircleRadius * Math.cos(Math.toRadians(i * 120 + 60))), mPaint);
        }

        //绘制外圆齿轮
        for (int i = 0; i < 360 / 8; i++) {
            canvas.drawLine((float) (centerX + outCircleRadius * Math.sin(Math.toRadians(i * 8 + moveAngle))),
                    (float) (centerY + outCircleRadius * Math.cos(Math.toRadians(i * 8 + moveAngle))),
                    (float) (centerX + (outCircleRadius + dip2px(4)) * Math.sin(Math.toRadians(i * 8 + moveAngle))),
                    (float) (centerY + (outCircleRadius + dip2px(4)) * Math.cos(Math.toRadians(i * 8 + moveAngle))), mPaint);
        }

        //绘制内圆齿轮
        mPaint.setStrokeWidth(inCircleRadius * GEAR_RADIUS_RATIO);
        for (int i = 0; i < 360 / 8; i++) {
            canvas.drawLine((float) (centerX + inCircleRadius * Math.sin(Math.toRadians(i * 8 - moveAngle))),
                    (float) (centerY + inCircleRadius * Math.cos(Math.toRadians(i * 8 - moveAngle))),
                    (float) (centerX + (inCircleRadius + dip2px(4)) * Math.sin(Math.toRadians(i * 8 - moveAngle))),
                    (float) (centerY + (inCircleRadius + dip2px(4)) * Math.cos(Math.toRadians(i * 8 - moveAngle))), mPaint);

        }

        mPaint.setStrokeWidth(mPaint.getStrokeWidth() * 2);
        canvas.drawCircle(centerX, centerY, outCircleRadius, mPaint);

        canvas.drawCircle(centerX, centerY, inCircleRadius, mPaint);


    }

    //开始动画
    public void startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {

                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f);
                animator.setDuration(5000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mAnimatedValue = (float) valueAnimator.getAnimatedValue();
                        moveAngle = (int) (mAnimatedValue * 360);

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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
