package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

/**
 * Created by xavier on 3/6/14.
 */
public class TimelineHeaderButtonView extends LinearLayout
{
    public static final String TAG = TimelineHeaderButtonView.class.getSimpleName();

    @InjectView(R.id.btn_profile_timeline) TextView btnTimeline;
    @InjectView(R.id.btn_profile_portfolios) TextView btnPortfolioList;
    @InjectView(R.id.btn_profile_stats) TextView btnStats;

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

    protected OnClickListener createBtnClickListener(final TimelineFragment.TabType tabType)
    {
        return new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                notifyClicked(tabType);
                changeButtonLook(tabType);
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
        view.setTextAppearance(getContext(),
                activeType == viewType ? R.style.UserProfile_Button_Selected : R.style.UserProfile_Button_Default);
    }
}
