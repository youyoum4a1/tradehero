package com.tradehero.th.fragments.portfolio.header;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.tradehero.th.api.portfolio.MarginCloseOutState;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.inject.HierarchyInjector;

import java.util.HashMap;
import javax.inject.Inject;

public class MarginCloseOutStatusTextView extends TextView
{
    private static final long PULSATING_DURATION_MS = 750;

    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @NonNull private final HashMap<MarginCloseOutState, ObjectAnimator> stateAnimators = new HashMap<>();
    protected PortfolioCompactDTO portfolioCompactDTO;

    //<editor-fold desc="Constructors">
    public MarginCloseOutStatusTextView(Context context)
    {
        super(context);
        HierarchyInjector.inject(this);
    }

    public MarginCloseOutStatusTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    public MarginCloseOutStatusTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        stateAnimators.clear();
        super.onDetachedFromWindow();
    }

    public void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        display();
    }

    public void display()
    {
        if (portfolioCompactDTO != null
                && portfolioCompactDTO.marginCloseOutPercent != null)
        {
            setVisibility(VISIBLE);

            MarginCloseOutState closeOutState = portfolioCompactDTOUtil.getMarginCloseOutState(
                    getResources(),
                    portfolioCompactDTO.marginCloseOutPercent);

            double margin = keepTwoDecimals(portfolioCompactDTO.marginCloseOutPercent);
            setText(getResources().getString(
                    closeOutState.labelResId, margin
            ));

            ObjectAnimator animator = getOrCreateMarginCloseOutPulsator(this, closeOutState);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                startOrResumeAnimator(animator);
            }
            else
            {
                startAnimator(animator);
            }
        }
        else
        {
            setVisibility(GONE);
        }
    }

    private double keepTwoDecimals(Double number){
        if(number==null){
            return 0;
        }
        return Math.floor(number*100d)/100;
    }


    @NonNull private ObjectAnimator getOrCreateMarginCloseOutPulsator(
            @NonNull View view,
            @NonNull MarginCloseOutState state)
    {
        ObjectAnimator animator = stateAnimators.get(state);
        if (animator == null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                pauseAllAnimators();
            }
            else
            {
                stopAllAnimators();
            }

            animator = ObjectAnimator.ofInt(
                    view,
                    "backgroundColor",
                    getResources().getColor(state.colorResIdPulseStart),
                    getResources().getColor(state.colorResIdPulseEnd));
            animator.setDuration(PULSATING_DURATION_MS);
            animator.setEvaluator(new ArgbEvaluator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            stateAnimators.put(state, animator);
        }
        return animator;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void startOrResumeAnimator(@NonNull ObjectAnimator animator)
    {
        if (animator.isPaused())
        {
            animator.resume();
        }
        else
        {
            startAnimator(animator);
        }
    }

    private void startAnimator(@NonNull ObjectAnimator animator)
    {
        if (!animator.isStarted())
        {
            animator.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void pauseAllAnimators()
    {
        for (ObjectAnimator animator : stateAnimators.values())
        {
            if (!animator.isPaused())
            {
                animator.pause();
            }
        }
    }

    private void stopAllAnimators()
    {
        for (ObjectAnimator animator : stateAnimators.values())
        {
            if (animator.isStarted())
            {
                animator.end();
            }
        }
    }


}
