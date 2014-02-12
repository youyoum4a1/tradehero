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

/**
 * Created by xavier on 2/12/14.
 */
public class LeaderboardFilterValueSlider extends RelativeLayout
{
    public static final String TAG = LeaderboardFilterValueSlider.class.getSimpleName();

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
        this.currentValue = ((fromSeekBar * (maxValue - minValue)) / 100 + minValue);
        displayValue();
    }

    protected void displayValue()
    {
        if (valueText != null)
        {
            valueText.setText(String.format("%d", Math.round(currentValue)));
        }
        if (valueSlider != null)
        {
            valueSlider.setProgress((int) (100 * (currentValue - minValue) / (maxValue - minValue)));
        }
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
