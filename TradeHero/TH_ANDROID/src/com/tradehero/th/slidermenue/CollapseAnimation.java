/* Created by Srikanth gr.
 */

package com.tradehero.th.slidermenue;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

public class CollapseAnimation extends TranslateAnimation implements
        TranslateAnimation.AnimationListener
{

    private RelativeLayout mainLayout;
    int panelWidth;

    public CollapseAnimation(RelativeLayout slidingPanel, int width, int fromXType,
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

        // Clear left and right margins
        LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
        params.rightMargin = 0;
        params.leftMargin = 0;
        mainLayout.setLayoutParams(params);
        mainLayout.requestLayout();
        mainLayout.startAnimation(this);
    }

    public void onAnimationEnd(Animation animation)
    {
        // not implemented
    }

    public void onAnimationRepeat(Animation animation)
    {
        // not implemented
    }

    public void onAnimationStart(Animation animation)
    {
        // not implemented
    }
}
