package com.tradehero.th.fragments.leaderboard.filter;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

public class LeaderboardFilterValueSlider extends RelativeLayout
{
    @InjectView(R.id.leaderboard_filter_value) protected TextView valueText;
    @InjectView(R.id.leaderboard_filter_value_slider) protected SeekBar valueSlider;

    protected float minValue = 0;
    protected float maxValue = 100;
    protected float currentValue = 0;

    //<editor-fold desc="Constructors">
    public LeaderboardFilterValueSlider(Context context)
    {
        super(context);
    }

    public LeaderboardFilterValueSlider(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public LeaderboardFilterValueSlider(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    //</editor-fold>

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LeaderboardFilterValueSlider);
        minValue = a.getFloat(R.styleable.LeaderboardFilterValueSlider_valueMin, minValue);
        maxValue = a.getFloat(R.styleable.LeaderboardFilterValueSlider_valueMax, maxValue);
        currentValue = a.getFloat(R.styleable.LeaderboardFilterValueSlider_valueDefault, currentValue);
        a.recycle();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    protected void initViews()
    {
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (valueSlider != null)
        {
            valueSlider.setOnSeekBarChangeListener(new LeaderboardFilterValueSliderSeekBarListener());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (valueSlider != null)
        {
            valueSlider.setOnSeekBarChangeListener(null);
        }
        super.onDetachedFromWindow();
    }

    public float getCurrentValue()
    {
        return currentValue;
    }

    public void setCurrentValue(float currentValue)
    {
        this.currentValue = currentValue;
        displayValue();
    }

    public void setDefaultCurrentValue()
    {
        setCurrentValue(minValue);
    }

    protected void setValueFromSeekBar(int fromSeekBar)
    {
        this.currentValue = ((((float) fromSeekBar) * (maxValue - minValue)) / 100f + minValue);
        displayValue();
    }

    protected void displayValue()
    {
        if (valueText != null)
        {
            valueText.setText(getCurrentValueText());
        }
        if (valueSlider != null)
        {
            valueSlider.setProgress((int) (100f * (currentValue - minValue) / (maxValue - minValue)));
        }
    }

    protected String getCurrentValueText()
    {
        return String.format("%d", Math.round(currentValue));
    }

    protected class LeaderboardFilterValueSliderSeekBarListener implements SeekBar.OnSeekBarChangeListener
    {
        public LeaderboardFilterValueSliderSeekBarListener()
        {
            super();
        }

        @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b)
        {
            setValueFromSeekBar(i);
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        @Override public void onStopTrackingTouch(SeekBar seekBar)
        {
        }
    }
}
