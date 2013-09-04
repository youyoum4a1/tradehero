

package com.tradehero.th.slidermenue;

import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

public class ExpandAnimation extends TranslateAnimation implements
        Animation.AnimationListener
{

    private RelativeLayout mainLayout;
    int panelWidth;

    public ExpandAnimation(RelativeLayout slidingPanel, int width, int fromXType,
            float fromXValue, int toXType, float toXValue, int fromYType,
            float fromYValue, int toYType, float toYValue)
    {

        super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue,
                toYType, toYValue);

        // Initialize
        mainLayout = slidingPanel;
        panelWidth = width;
        setDuration(400);
        setFillAfter(false);
        setInterpolator(new AccelerateDecelerateInterpolator());
        setAnimationListener(this);
        mainLayout.startAnimation(this);
    }

    public void onAnimationEnd(Animation arg0)
    {

        // Create margin and align left
        LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
        params.leftMargin = panelWidth;
        params.gravity = Gravity.LEFT;
        mainLayout.clearAnimation();
        mainLayout.setLayoutParams(params);
        mainLayout.requestLayout();
    }

    public void onAnimationRepeat(Animation arg0)
    {

    }

    public void onAnimationStart(Animation arg0)
    {

    }
}
