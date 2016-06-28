package com.ws.loadingview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 6/28 0028.
 */
public class WSCircleFace extends View {

    private Paint mPaint;
    private Context mContext;

    private int circleCenterX, circleCenterY;

    private int circleRadius;

    private int startAngle;

    private boolean isFace;

    private final static int EYE_ROUND = 60;

    private final static float RADIUS_RATIO = 1 / 2f;

    private final static float FACE_RADIUS_RATIO = 1 / 8f;

    public WSCircleFace(Context context) {
        this(context, null);
    }

    public WSCircleFace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WSCircleFace(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
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

        circleCenterX = w / 2;
        circleCenterY = h / 2;

        //处理padding情况
        circleRadius = (int) (Math.min(Math.min(circleCenterY - getPaddingTop(), circleCenterY - getPaddingBottom()),
                Math.min(circleCenterX - getPaddingLeft(), circleCenterX - getPaddingRight())) * RADIUS_RATIO);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(circleRadius * FACE_RADIUS_RATIO);

        RectF rectF = new RectF(circleCenterX - circleRadius, circleCenterY - circleRadius,
                circleCenterX + circleRadius, circleCenterY + circleRadius);
        canvas.drawArc(rectF, startAngle, 180, false, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        if (isFace) {  //画脸
            canvas.drawCircle((float) (circleCenterX - circleRadius * Math.sin(Math.toRadians(EYE_ROUND))),
                    (float) (circleCenterY - circleRadius * Math.cos(Math.toRadians(EYE_ROUND))), mPaint.getStrokeWidth() * 3 / 2, mPaint);

            canvas.drawCircle((float) (circleCenterX + circleRadius * Math.sin(Math.toRadians(EYE_ROUND))),
                    (float) (circleCenterY - circleRadius * Math.cos(Math.toRadians(EYE_ROUND))), mPaint.getStrokeWidth() * 3 / 2, mPaint);
        }

    }

    //开始动画
    public void startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f);
                animator.setDuration(1500);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float mAnimatedValue = (float) valueAnimator.getAnimatedValue();
                        if (mAnimatedValue < 0.5) {
                            isFace = false;
                            startAngle = (int) (720 * mAnimatedValue);
                        } else {
                            startAngle = 720;
                            isFace = true;
                        }

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
