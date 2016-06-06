package com.androidth.general.fragments.portfolio.header;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import com.androidth.general.api.portfolio.MarginCloseOutState;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOUtil;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.number.THSignedPercentage;

public class MarginCloseOutStatusTextView extends TextView
{
    private static final long PULSATING_DURATION_MS = 750;
    private static final int RELEVANT_DIGIT_MARGIN = 3;

    @Nullable private ObjectAnimator currentAnimator;
    @NonNull private Pair<MarginCloseOutState, MarginCloseOutState> stateTransition;
    protected PortfolioCompactDTO portfolioCompactDTO;

    //<editor-fold desc="Constructors">
    public MarginCloseOutStatusTextView(Context context)
    {
        super(context);
        HierarchyInjector.inject(this);
        stateTransition = Pair.create(null, null);
    }

    public MarginCloseOutStatusTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        stateTransition = Pair.create(null, null);
    }

    public MarginCloseOutStatusTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        HierarchyInjector.inject(this);
        stateTransition = Pair.create(null, null);
    }
    //</editor-fold>

    public void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        stateTransition = Pair.create(
                getState(),
                stateTransition.first);
        display();
    }

    @Nullable protected MarginCloseOutState getState()
    {
        if (portfolioCompactDTO == null || portfolioCompactDTO.marginCloseOutPercent == null)
        {
            return null;
        }
        return PortfolioCompactDTOUtil.getMarginCloseOutState(
                getResources(),
                portfolioCompactDTO.marginCloseOutPercent);
    }

    public void display()
    {
        if (portfolioCompactDTO != null
                && portfolioCompactDTO.marginCloseOutPercent != null)
        {
            setVisibility(VISIBLE);

            MarginCloseOutState closeOutState = PortfolioCompactDTOUtil.getMarginCloseOutState(
                    getResources(),
                    portfolioCompactDTO.marginCloseOutPercent);

            double margin = portfolioCompactDTO.marginCloseOutPercent;
            setText(getResources().getString(
                    closeOutState.labelResId,
                    THSignedPercentage.builder(margin * 100)
                            .relevantDigitCount(RELEVANT_DIGIT_MARGIN)
                            .build().toString()));

            if (stateTransition.first != stateTransition.second && stateTransition.first != null)
            {
                stateTransition = Pair.create(stateTransition.first, stateTransition.first);
                if (currentAnimator != null)
                {
                    currentAnimator.cancel();
                }
                currentAnimator = getOrCreateMarginCloseOutPulsator(this, closeOutState);
                currentAnimator.start();
            }
        }
        else
        {
            setVisibility(GONE);
        }
    }

    @NonNull private ObjectAnimator getOrCreateMarginCloseOutPulsator(
            @NonNull View view,
            @NonNull MarginCloseOutState state)
    {
        ObjectAnimator animator = ObjectAnimator.ofInt(
                view,
                "backgroundColor",
                getResources().getColor(state.colorResIdPulseStart),
                getResources().getColor(state.colorResIdPulseEnd));
        animator.setDuration(PULSATING_DURATION_MS);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        return animator;
    }
}
