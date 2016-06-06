package com.androidth.general.fragments.leaderboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

public class ExpandingLayout extends LinearLayout
{
    private static final int EXPAND_COLLAPSE_MAX_DURATION = 200; //ms

    private OnExpandListener expandListener;

    private int mCurrentHeight = 0;
    private ValueAnimator animator;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public ExpandingLayout(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ExpandingLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ExpandingLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    private void notifyExpand(boolean expand)
    {
        OnExpandListener expandListenerCopy = expandListener;
        if (expandListenerCopy != null)
        {
            expandListenerCopy.onExpand(expand);
        }
    }

    public void expand(boolean expand)
    {
        if (getVisibility() == View.VISIBLE)
        {
            mCurrentHeight = getMeasuredHeight();
        }

        clearExpandAnimation();

        int measuredHeight = getManualMeasuredHeight();
        int distToTravel;
        if (expand)
        {
            distToTravel = measuredHeight - mCurrentHeight;
            animator = ValueAnimator.ofInt(mCurrentHeight, measuredHeight);
            animator.addListener(new AnimatorListenerAdapter()
            {
                @Override public void onAnimationStart(Animator animation)
                {
                    super.onAnimationStart(animation);
                    getLayoutParams().height = mCurrentHeight;
                    setVisibility(View.VISIBLE);
                }

                @Override public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    notifyExpand(true);
                }
            });
        }
        else
        {
            distToTravel = mCurrentHeight;
            animator = ValueAnimator.ofInt(mCurrentHeight, 0);
            animator.addListener(new AnimatorListenerAdapter()
            {
                @Override public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    setVisibility(View.GONE);
                    notifyExpand(false);
                }
            });
        }

        int duration = EXPAND_COLLAPSE_MAX_DURATION * (distToTravel / measuredHeight);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override public void onAnimationUpdate(ValueAnimator animation)
            {
                mCurrentHeight = (int) animation.getAnimatedValue();
                ExpandingLayout.this.getLayoutParams().height = mCurrentHeight;
                ExpandingLayout.this.requestLayout();
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        //This is a hack to prevent the view being recycled while we are doing expand/collapse animation
        //Another way to avoid this hack is by using View.setHasTransientState(true) but this API is only available
        //for post JellyBean devices
        animate().alpha(1).setDuration(duration).start();
    }

    public void expandWithNoAnimation(boolean expand)
    {
        if (expand)
        {
            setVisibility(View.VISIBLE);
            getLayoutParams().height = getManualMeasuredHeight();
            requestLayout();
        }
        else
        {
            setVisibility(View.GONE);
        }
    }

    private int getManualMeasuredHeight()
    {
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT, MeasureSpec.AT_MOST);
        measure(widthMeasureSpec, heightMeasureSpec);
        return getMeasuredHeight() + getPaddingBottom() + getPaddingTop();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        clearExpandAnimation();
    }

    private void clearExpandAnimation()
    {
        if (animator != null)
        {
            animator.removeAllListeners();
            animator.removeAllUpdateListeners();
            animator.cancel();
        }
        animator = null;
    }

    public void setOnExpandListener(OnExpandListener expandListener)
    {
        this.expandListener = expandListener;
    }

    public static interface OnExpandListener
    {
        void onExpand(boolean expand);
    }
}
