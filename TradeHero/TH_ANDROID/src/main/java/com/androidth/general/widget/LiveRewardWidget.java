package com.androidth.general.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.androidth.general.R;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LiveRewardWidget extends LinearLayout
{

    private Unbinder unbinder;
    @BindViews({R.id.live_reward_20,
            R.id.live_reward_40,
            R.id.live_reward_60,
            R.id.live_reward_80,
            R.id.live_reward_100}) View[] views;

    public LiveRewardWidget(Context context)
    {
        super(context);
        init(null);
    }

    public LiveRewardWidget(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public LiveRewardWidget(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public LiveRewardWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs)
    {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.live_reward_steps_merged, this, true);
        unbinder = ButterKnife.bind(this);
        if (attrs != null)
        {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.LiveRewardWidget, 0, 0);
            int rewardStepCount = a.getInteger(R.styleable.LiveRewardWidget_rewardStep, 0);
            setRewardStep(rewardStepCount);
            a.recycle();
        }
    }

    public void setRewardStep(int rewardStepCount)
    {
        for (int i = 0; i < views.length; i++)
        {
            if (i < rewardStepCount)
            {
                views[i].setAlpha(1.0f);
            }
            else
            {
                views[i].setAlpha(0.3f);
            }
        }
    }

    public void setDistributeEvenly()
    {
        //TODO
    }
}
