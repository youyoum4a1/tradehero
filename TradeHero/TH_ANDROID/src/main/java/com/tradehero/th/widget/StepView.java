package com.tradehero.th.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.tradehero.th.R;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 1:21 PM Copyright (c) TradeHero */
public class StepView extends FrameLayout
{
    private final LayoutInflater layoutInflater;

    private final List<View> views;
    private WeakReference<StepProvider> provider;
    private int currentStep = -1;
    private boolean autoStart = true;
    private boolean circular = true;
    private boolean animationInitiated;
    private int[] animation = new int[2];

    public StepView(Context context, LayoutInflater layoutInflater, View startingView)
    {
        this(context, layoutInflater);
        views.add(startingView);
    }

    public StepView(Context context, LayoutInflater layoutInflater)
    {
        super(context);
        this.layoutInflater = layoutInflater;
        views = new LinkedList<>();

        setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                step();
            }
        });

    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (autoStart)
        {
            step();
        }
    }

    public void setAnimation(int show, int hide)
    {
        this.animation[0] = show;
        this.animation[1] = hide;
        this.animationInitiated = true;
    }
    private int[] getSafeAnimation()
    {
        if (animationInitiated) return this.animation;
        setAnimation(R.anim.alpha_in, R.anim.alpha_out);
        return animation;
    }

    private void step()
    {
        if (views == null)
        {
            return;
        }

        View oldStep = null;

        if (currentStep >=0 && currentStep < views.size())
        {
            oldStep = views.get(currentStep);
        }
        ++currentStep;

        // if this is a new step or the view for current step is not set, ask provider
        if (currentStep >= views.size() || views.get(currentStep) == null)
        {
            StepProvider stepper = provider.get();
            if (stepper != null)
            {
                View nextView = stepper.provideView(currentStep);
                if (nextView != null)
                {
                    views.add(nextView);
                    nextView.setVisibility(View.INVISIBLE);
                    addView(nextView);
                }
            }
        }

        View newStep = null;
        // bring current step to front
        if (currentStep < views.size() && views.get(currentStep) != null)
        {
            newStep = views.get(currentStep);
        }
        else if (circular && !views.isEmpty())
        {
            // circular
            currentStep = 0;
            newStep = views.get(currentStep);
        }

        int[] animation = getSafeAnimation();
        if (oldStep != null)
        {
            oldStep.setVisibility(View.GONE);
            oldStep.startAnimation(AnimationUtils.loadAnimation(getContext(), animation[1]));
        }
        if (newStep != null)
        {
            newStep.startAnimation(AnimationUtils.loadAnimation(getContext(), animation[0]));
            newStep.setVisibility(View.VISIBLE);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        // unlink all views
        views.clear();
    }

    public void setStepProvider(StepProvider listener)
    {
        this.provider = new WeakReference<>(listener);
    }

    public static interface StepProvider
    {
        public View provideView(int step);
    }

    protected List<View> getViews()
    {
        return views;
    }

    protected View getCurrentStep()
    {
        if (views != null && currentStep < views.size() && currentStep >= 0)
        {
            return views.get(currentStep);
        }
        return null;
    }
}
