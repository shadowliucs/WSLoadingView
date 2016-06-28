# WSLoadingView

　　那一刻 我升起风马 不为乞福 只为守候你的到来
　　那一天 闭目在经殿香雾中 蓦然听见 你颂经中的真言
　　那一日 垒起玛尼堆 不为修德 只为投下心湖的石子
　　那一夜 我听了一宿梵唱 不为参悟 只为寻你的一丝气息

##概述

有时候我会为了实现某个动画的算法而绞尽脑汁，苦苦思考。往往会在草稿上面绘制动画轨迹，真的很多时候几天就在纠结一个动画的实现，不过最后成功后真的很满足喜悦。

我陆续出过一些`LodingView`的文章，但都比较杂乱。近来我在网上看到一张图，并通过代码实现了出来。在这边非常感谢`@ldoublem`，我会不断更新`LodingView`把好看及简洁的动画集成进来，希望能够帮助到你。


先出图说话：

![load](http://img.blog.csdn.net/20160628224646411)


##WSCircleCD

```
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
```

我主要讲解下红字注释的地方，为什么自定义一个View在布局时将其大小设为wrap_content但其实际却是match_parent的效果？请查阅[谷歌的小弟一自定义View系列教程02--onMeasure源码详尽分析](http://blog.csdn.net/lfdfhl/article/details/51347818)；处理`padding`情况，不然布局`xml`文件设置`padding`值将无效；绘制过程比较简单，我这里就不再详细讲解，有不懂请给我留言。

##WSCircleSun

WSCircleSun主要是控制小圆的旋转，每次移动角度就增加`moveAngle`。

```

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    mPaint.setStrokeWidth(circleRadius * SMALL_RADIUS_RATIO);
    //绘制小圆
    for (int i = 0; i < SMALL_CIRCLE_COUNT; i++) {
        canvas.drawCircle((float) (circleCenterX + circleRadius * Math.sin(Math.toRadians(i * 360 / SMALL_CIRCLE_COUNT + moveAngle))),
                (float) (circleCenterY + circleRadius * Math.cos(Math.toRadians(i * 360 / SMALL_CIRCLE_COUNT + moveAngle))),
                circleRadius * SMALL_RADIUS_RATIO,
                mPaint);
    }
    //绘制大圆
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius * LARGE_RADIUS_RATIO, mPaint);
}

```

```

//开始动画
public void startAnimator() {
    post(new Runnable() {
        @Override
        public void run() {
            ValueAnimator animator = ValueAnimator.ofInt(0, 361);
            animator.setDuration(3000);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    moveAngle = (int) valueAnimator.getAnimatedValue();
                    postInvalidate();
                }
            });
            animator.start();
        }
    });
}

```

##WSCircleRing

`WSCircleRing`主要是控制弧度的`startAngle`。

```

@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    mPaint.setColor(Color.WHITE);
    mPaint.setStrokeWidth(circleRadius * RING_RADIUS_RATIO);
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, mPaint);//画背景
    mPaint.setColor(Color.parseColor("#FF4081"));
    RectF rectF = new RectF(circleCenterX - circleRadius, circleCenterY - circleRadius,
            circleCenterX + circleRadius, circleCenterY + circleRadius);
    canvas.drawArc(rectF, 0 + moveAngle, 80, false, mPaint);
}
//开始动画
public void startAnimator() {
    post(new Runnable() {
        @Override
        public void run() {
            ValueAnimator animator = ValueAnimator.ofInt(0, 361);
            animator.setDuration(1000);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    moveAngle = (int) valueAnimator.getAnimatedValue();
                    postInvalidate();
                }
            });
            animator.start();
        }
    });
}

```

##WSCircleFace

WSCircleFace主要是控制脸的出现和消失，动画出现一半后脸出现，反之消失。


```

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

```

##WSCircleJump

WSCircleJump主要是控制小圆的弹跳，上一个小圆弹跳结束后下一个小圆开始。

```

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


```

##WSGears

WSGears主要是绘制大小齿轮。

```

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


```

##WSJump

WSJump利用贝塞尔曲线实现弹动效果。

```

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

```

##WSFiveStar

WSFiveStar主要控制线条的绘制，一条直线从起点绘制到终点，终点作为下一条直线的起点，依次绘制。利用`mPath.rLineTo`增量的方式绘制直线，如果你敢兴趣可以通过设置`setRegularPolygon(int regularPolygon)`方法设置需要绘制的是正多边形，我绘制出过正二十四边形。

```

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

```

##WSLineProgress

WSLineProgress主要是控制两段直线的绘制。

```

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

```

##WSEatBeans

WSEatBeans主要是控制嘴的张合，豆数量的变化。

```

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(dip2px(2));
        RectF rectF = new RectF(centerX - mRadius + moveX, centerY - eatRadius, centerX - mRadius + eatRadius * 2
                + moveX, centerY + eatRadius);
        canvas.drawArc(rectF, eatStartAngle, eatSweepAngle, true, mPaint);

        //画眼睛
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(centerX - mRadius + eatRadius + moveX, centerY - eatRadius / 2, dip2px(2), mPaint);

        //绘制豆
        mPaint.setColor(Color.WHITE);
        //豆的个数是间隔数减去1
        int count = (2 * (mRadius - eatRadius)) / eatRadius - 1;
        for (int i = eatBeans; i < count; i++) {
            canvas.drawCircle(centerX - mRadius + eatRadius * 2 +
                    eatRadius * (i + 1), centerY, dip2px(2), mPaint);
        }


    }

    //开始动画
    public void startAnimator() {
        post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f);
                animator.setDuration(3000);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        moveX = (int) ((float) animation.getAnimatedValue() * 2 * (mRadius - eatRadius));
                        eatBeans = moveX / eatRadius;

                        if (eatState == 0) {
                            eatStartAngle -= 2;
                            if (eatStartAngle <= 0) {
                                eatState = 1;
                            }
                        } else if (eatState == 1) {
                            eatStartAngle += 2;
                            if (eatStartAngle >= 30) {
                                eatState = 0;
                            }
                        }
                        eatSweepAngle = 360 - eatStartAngle * 2;
                        postInvalidate();
                    }
                });
                animator.start();
            }
        });
    }


```

##WSCircleBar

WSCircleBar，WSCircleArc，WSCircleRise类似。

```

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

```

在此非常感谢[ldoublem ](http://www.jianshu.com/p/2bdc30a6bbd2)的效果图并通过代码实现了出来，我们实现过程有很多不同的地方。

`LoadingView`我会不断更新，敬请你的期待。关注我的博客http://blog.csdn.net/u012551350/article/details/51779358




