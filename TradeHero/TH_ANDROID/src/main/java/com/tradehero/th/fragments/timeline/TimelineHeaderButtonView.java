package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.thm.R;

public class TimelineHeaderButtonView extends LinearLayout
{
    @InjectView(R.id.btn_profile_timeline) Button btnTimeline;
    @InjectView(R.id.btn_profile_portfolios) Button btnPortfolioList;
    @InjectView(R.id.btn_profile_stats) Button btnStats;

    private TimelineProfileClickListener clickListener;

    //<editor-fold desc="Constructors">
    public TimelineHeaderButtonView(Context context)
    {
        super(context);
    }

    public TimelineHeaderButtonView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TimelineHeaderButtonView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        btnTimeline.setOnClickListener(createBtnClickListener(TimelineFragment.TabType.TIMELINE));
        btnPortfolioList.setOnClickListener(createBtnClickListener(TimelineFragment.TabType.PORTFOLIO_LIST));
        btnStats.setOnClickListener(createBtnClickListener(TimelineFragment.TabType.STATS));
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        btnTimeline.setOnClickListener(null);
        btnPortfolioList.setOnClickListener(null);
        btnStats.setOnClickListener(null);
        super.onDetachedFromWindow();
    }

    public void setTimelineProfileClickListener(TimelineProfileClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    protected OnTouchListener createBtnTouchListener(final TimelineFragment.TabType tabType)
    {
        return new OnTouchListener()
        {
            @Override public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    changeButtonLook(tabType);
                    notifyClicked(tabType);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        };
    }

    protected OnClickListener createBtnClickListener(final TimelineFragment.TabType tabType)
    {
        return new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                //changeButtonLook(tabType);
                notifyClicked(tabType);
            }
        };
    }

    protected void notifyClicked(TimelineFragment.TabType tabType)
    {
        if (clickListener != null)
        {
            clickListener.onBtnClicked(tabType);
        }
    }

    public void changeButtonLook(TimelineFragment.TabType activeType)
    {
        changeButtonLook(activeType, btnTimeline, TimelineFragment.TabType.TIMELINE);
        changeButtonLook(activeType, btnPortfolioList, TimelineFragment.TabType.PORTFOLIO_LIST);
        changeButtonLook(activeType, btnStats, TimelineFragment.TabType.STATS);
    }

    protected void changeButtonLook(TimelineFragment.TabType activeType, TextView view, TimelineFragment.TabType viewType)
    {
        view.setSelected(activeType == viewType);
    }
}
