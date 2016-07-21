package com.androidth.general.fragments.timeline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidth.general.R;

import butterknife.Unbinder;
import rx.Observable;
import rx.subjects.PublishSubject;

public class TimelineHeaderButtonView extends LinearLayout
{
    @BindView(R.id.btn_profile_portfolios) TextView btnPortfolioList;
    @BindView(R.id.btn_profile_timeline) TextView btnTimeline;

    @NonNull private final PublishSubject<TimelineFragment.TabType> tabTypeSubject;

    private Unbinder unbinder;
    //<editor-fold desc="Constructors">
    public TimelineHeaderButtonView(Context context)
    {
        super(context);
        tabTypeSubject = PublishSubject.create();
    }

    public TimelineHeaderButtonView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        tabTypeSubject = PublishSubject.create();
    }

    public TimelineHeaderButtonView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        tabTypeSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        unbinder = ButterKnife.bind(this);
        setDividerDrawable(null);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        unbinder = ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        unbinder.unbind();
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<TimelineFragment.TabType> getTabTypeObservable()
    {
        return tabTypeSubject.asObservable();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_profile_portfolios)
    protected void onPortfoliosClicked(View view)
    {
        tabTypeSubject.onNext(TimelineFragment.TabType.PORTFOLIO_LIST);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_profile_timeline)
    protected void onTimelineClicked(View view)
    {
        tabTypeSubject.onNext(TimelineFragment.TabType.TIMELINE);
    }

    public void setActive(@NonNull TimelineFragment.TabType activeType)
    {
        if (btnTimeline != null)
        {
            changeButtonLook(activeType, btnTimeline, TimelineFragment.TabType.TIMELINE);
        }
        if (btnPortfolioList != null)
        {
            changeButtonLook(activeType, btnPortfolioList, TimelineFragment.TabType.PORTFOLIO_LIST);
        }
    }

    protected void changeButtonLook(@NonNull TimelineFragment.TabType activeType, @NonNull TextView view, @NonNull TimelineFragment.TabType viewType)
    {
        view.setSelected(activeType == viewType);
    }
}
