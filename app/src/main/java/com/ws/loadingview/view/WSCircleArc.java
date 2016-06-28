package com.ws.loadingview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 6/28 0028.
 */
public class WSCircleArc extends View {

    private Paint mPaint;
    private Context mContext;

    private int circleCenterX, circleCenterY;

    private int circleRadius;

    private Path mPath;

    private float startAngle, sweepAngle;

    private float mValueAnimator;

    private String text;

    private final static float RADIUS_RATIO = 2 / 3f;

    private final static float BAR_RADIUS_RATIO = 1 / 8f;

    public WSCircleArc(Context context) {
        this(context, null);
    }

    public WSCircleArc(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WSCircleArc(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
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

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(circleRadius * BAR_RADIUS_RATIO);
        mPaint.setColor(Color.WHITE);
        //画背景
        canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, mPaint);

        //画bar
        RectF rect = new RectF(circleCenterX - circleRadius, circleCenterY - circleRadius,
                circleCenterX + circleRadius, circleCenterY + circleRadius);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPaint.getStrokeWidth() * 2);
        canvas.drawArc(rect, startAngle, sweepAngle, false, mPaint);

        //画文字
        mPaint.setStyle(Paint.Style.FILL);
        text = (int) (mValueAnimator * 100) + "%";
        mPaint.setTextSize(mPaint.getStrokeWidth() * 2);
        canvas.drawText(text, circleCenterX - mPaint.measureText(text) / 2, circleCenterY + getFontHeight(mPaint, text) / 2, mPaint);

    }

    public float getFontHeight(Paint paint, String str) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.height();

    }

    //开始动画
    public void startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f);
                animator.setDuration(5000);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mValueAnimator = (float) valueAnimator.getAnimatedValue();
                        sweepAngle = 360 * mValueAnimator;
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
