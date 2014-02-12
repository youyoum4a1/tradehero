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

    protected int minValue = 0;
    protected int maxValue = 100;

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
        minValue = a.getInteger(R.styleable.LeaderboardFilterValueSlider_valueMin, minValue);
        maxValue = a.getInteger(R.styleable.LeaderboardFilterValueSlider_valueMax, maxValue);
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

    protected void displayValue(int fromSeekBar)
    {
        if (valueText != null)
        {
            valueText.setText("" + ((fromSeekBar * (maxValue - minValue)) / 100 + minValue));
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
            displayValue(i);
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        @Override public void onStopTrackingTouch(SeekBar seekBar)
        {
        }
    }
}
