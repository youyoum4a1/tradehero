package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by palmer on 14-11-6.
 */
public class TradeHeroProgressBar extends View {

    private boolean startAnimation = false;

    public TradeHeroProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private AlphaAnimation alphaAnimationA = null;
    private AlphaAnimation alphaAnimationB = null;

    private void initAnimationSet(){
        alphaAnimationA = new AlphaAnimation(1, 0.3f);
        alphaAnimationB = new AlphaAnimation(0.3f, 1);
        alphaAnimationA.setDuration(800);
        alphaAnimationB.setDuration(800);
        alphaAnimationA.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(startAnimation){
                    TradeHeroProgressBar.this.startAnimation(alphaAnimationB);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        alphaAnimationB.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(startAnimation){
                    TradeHeroProgressBar.this.startAnimation(alphaAnimationA);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public void startLoading(){
        startAnimation = true;
        if(alphaAnimationA == null || alphaAnimationB == null){
            initAnimationSet();
        }
        this.startAnimation(alphaAnimationA);
    }

    public void stopLoading(){
        startAnimation = false;
    }
}
