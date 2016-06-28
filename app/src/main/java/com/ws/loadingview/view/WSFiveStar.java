package com.ws.loadingview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
public class WSFiveStar extends View {

    private Paint mPaint;
    private Path mPath;
    private Context mContext;

    private int circleCenterX, circleCenterY;

    private int circleRadius;

    private float lineStartX, lineStartY;

    private float lineEndX, lineEndY;

    private float mValue;

    //正几边形
    private int regularPolygon;

    //边与边之间的圆角
    private int polygonAngle;

    private int startAngle;

    private final static int EDGE_COUNT = 9;

    private final static float RADIUS_RATIO = 2 / 3f;

    public WSFiveStar(Context context) {
        this(context, null);
    }

    public WSFiveStar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WSFiveStar(Context context, AttributeSet attrs, int defStyleAttr) {
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

        mPath = new Path();
        regularPolygon = EDGE_COUNT;
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

        polygonAngle = 360 / regularPolygon;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(dip2px(2));

        lineStartX = (float) (circleCenterX + circleRadius * Math.cos(Math.toRadians(startAngle)));
        lineStartY = (float) (circleCenterY + circleRadius * Math.sin(Math.toRadians(startAngle)));

        mPath.moveTo(lineStartX, lineStartY);

        lineEndX = (float) (circleCenterX + circleRadius * Math.cos(Math.toRadians(startAngle + polygonAngle * 2)));
        lineEndY = (float) (circleCenterY + circleRadius * Math.sin(Math.toRadians(startAngle + polygonAngle * 2)));


        mPath.rLineTo((lineEndX - lineStartX) * mValue, (lineEndY - lineStartY) * mValue);

        canvas.drawPath(mPath, mPaint);

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
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mValue = (float) animation.getAnimatedValue();
                        if (mValue >= 0.9f) {
                            mValue = 1.0f;
                        }
                        postInvalidate();
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        super.onAnimationRepeat(animation);
                        startAngle += polygonAngle * 2;
                        if (startAngle > polygonAngle * 2 * regularPolygon) {
                            startAngle = 0;
                            mPath.reset();
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

    //设置正多边形
    public void setRegularPolygon(int regularPolygon) {
        this.regularPolygon = regularPolygon;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
