package com.ws.loadingview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 6/28 0028.
 */
public class WSCircleJump extends View {

    private Paint mPaint;
    private Context mContext;

    private int centerX, centerY;

    private int mRadius;

    private int currentBallPosition;

    private float mAnimatedValue;

    private float ballJumpY;

    private final static float RADIUS_RATIO = 2 / 3f;

    private final static float JUMP_BALL_RATIO = 1 / 12f;

    private final static int BALL_COUNT = 4;

    public WSCircleJump(Context context) {
        this(context, null);
    }

    public WSCircleJump(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WSCircleJump(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mRadius = (int) (Math.min(Math.min(centerY - getPaddingTop(), centerY - getPaddingBottom()),
                Math.min(centerX - getPaddingLeft(), centerX - getPaddingRight())) * RADIUS_RATIO);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(mRadius * JUMP_BALL_RATIO);

        //画小球
        for (int i = 0; i < BALL_COUNT; i++) {
            if (i == currentBallPosition) {
                canvas.drawCircle((centerX - mRadius) + 2 * mRadius / (BALL_COUNT - 1) * i,
                        centerY - ballJumpY, mPaint.getStrokeWidth(), mPaint);
            } else {
                canvas.drawCircle((centerX - mRadius) + 2 * mRadius / (BALL_COUNT - 1) * i,
                        centerY, mPaint.getStrokeWidth(), mPaint);
            }
        }

    }

    //开始动画
    public void startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f);
                animator.setDuration(500);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setRepeatCount(ValueAnimator.INFINITE);

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mAnimatedValue = (float) valueAnimator.getAnimatedValue();
                        if (mAnimatedValue < 0.5) {
                            ballJumpY = mAnimatedValue * mRadius;
                        } else {
                            ballJumpY = (1 - mAnimatedValue) * mRadius;
                        }
                        postInvalidate();
                    }
                });

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        super.onAnimationRepeat(animation);
                        currentBallPosition++;
                        if (currentBallPosition >= BALL_COUNT) {
                            currentBallPosition = 0;
                        }
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
