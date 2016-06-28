package com.ws.loadingview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 6/28 0028.
 */
public class WSJump extends View {

    private Paint mPaint;
    private Context mContext;

    private int centerX, centerY;

    private int mRadius;

    private float mJumpY;

    private float quadMoveY;

    private final static float RADIUS_RATIO = 2 / 3f;


    public WSJump(Context context) {
        this(context, null);
    }

    public WSJump(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WSJump(Context context, AttributeSet attrs, int defStyleAttr) {
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

        centerX = w / 2;
        centerY = h / 2;

        //处理padding情况
        mRadius = (int) (Math.min(Math.min(centerY - getPaddingTop(), centerY - getPaddingBottom()),
                Math.min(centerX - getPaddingLeft(), centerX - getPaddingRight())) * RADIUS_RATIO);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dip2px(2));
        //绘制贝塞尔曲线
        Path mPath = new Path();
        mPath.moveTo(centerX - mRadius, centerY);
        mPath.quadTo(centerX, centerY + quadMoveY, centerX + mRadius, centerY);
        canvas.drawPath(mPath, mPaint);

        //绘制2边小球   dip2px(4)为小球半径  分别加上和减去半径
        canvas.drawCircle(centerX - mRadius - dip2px(4), centerY, dip2px(4), mPaint);
        canvas.drawCircle(centerX + mRadius + dip2px(4), centerY, dip2px(4), mPaint);

        //绘制中间小球    dip2px(4+3)  两边小球的半径 加上自己的半径
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY - dip2px(4 + 3) - mJumpY, dip2px(6), mPaint);


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
                        float value = (float) valueAnimator.getAnimatedValue();
                        if (value > 0.75f) {  // 0.75  =0.25*3
                            quadMoveY = mRadius * (1 - value) * 3;
                        } else {
                            quadMoveY = value * mRadius;
                        }
                        if (value > 0.35f) {// 0.7  =0.35*2
                            mJumpY = (1 - value) * mRadius;
                        } else {
                            mJumpY = value * mRadius * 2;
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
