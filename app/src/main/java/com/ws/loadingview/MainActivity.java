package com.ws.loadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ws.loadingview.view.WSCircleArc;
import com.ws.loadingview.view.WSCircleBar;
import com.ws.loadingview.view.WSCircleCD;
import com.ws.loadingview.view.WSCircleFace;
import com.ws.loadingview.view.WSCircleJump;
import com.ws.loadingview.view.WSCircleRing;
import com.ws.loadingview.view.WSCircleRise;
import com.ws.loadingview.view.WSCircleSun;
import com.ws.loadingview.view.WSEatBeans;
import com.ws.loadingview.view.WSFiveStar;
import com.ws.loadingview.view.WSGears;
import com.ws.loadingview.view.WSJump;
import com.ws.loadingview.view.WSLineProgress;

public class MainActivity extends AppCompatActivity {

    private WSCircleCD mWSCircleCD;
    private WSCircleSun mWSCircleSun;
    private WSCircleRing mWSCircleRing;
    private WSCircleFace mWSCircleFace;
    private WSCircleJump  mWSCircleJump;
    private WSGears  mWSGears;
    private WSJump mWSJump;
    private WSLineProgress mWSLineProgress;
    private WSEatBeans mWSEatBeans;
    private WSFiveStar mWSFiveStar;
    private WSFiveStar mWSFiveStarView;
    private WSCircleRise mWSCircleRise;
    private WSCircleBar mWSCircleBar;
    private WSCircleArc mWSCircleArc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWSCircleCD = (WSCircleCD) findViewById(R.id.load_cd);
        mWSCircleCD.startAnimator();


        mWSCircleSun = (WSCircleSun) findViewById(R.id.load_sun);
        mWSCircleSun.startAnimator();

        mWSCircleRing= (WSCircleRing) findViewById(R.id.load_ring);
        mWSCircleRing.startAnimator();

        mWSCircleFace= (WSCircleFace) findViewById(R.id.load_face);
        mWSCircleFace.startAnimator();

        mWSCircleJump= (WSCircleJump) findViewById(R.id.load_jump);
        mWSCircleJump.startAnimator();

        mWSGears= (WSGears) findViewById(R.id.load_gear);
        mWSGears.startAnimator();

        mWSJump= (WSJump) findViewById(R.id.load_mjump);
        mWSJump.startAnimator();

        mWSLineProgress= (WSLineProgress) findViewById(R.id.load_line_progress);
        mWSLineProgress.startAnimator();

        mWSEatBeans= (WSEatBeans) findViewById(R.id.load_eat);
        mWSEatBeans.startAnimator();

        mWSFiveStar= (WSFiveStar) findViewById(R.id.load_five);
        mWSFiveStar.startAnimator();

        mWSFiveStarView= (WSFiveStar) findViewById(R.id.load_mfive);
        mWSFiveStarView.setRegularPolygon(5);
        mWSFiveStarView.startAnimator();

        mWSCircleRise= (WSCircleRise) findViewById(R.id.load_rise);
        mWSCircleRise.startAnimator();

        mWSCircleBar= (WSCircleBar) findViewById(R.id.load_bar);
        mWSCircleBar.startAnimator();

        mWSCircleArc= (WSCircleArc) findViewById(R.id.load_arc);
        mWSCircleArc.startAnimator();
    }
}
