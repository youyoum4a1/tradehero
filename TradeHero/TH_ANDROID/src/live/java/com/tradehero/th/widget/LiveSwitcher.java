package com.tradehero.th.widget;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;
import com.tradehero.th.R;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LiveSwitcher extends ViewSwitcher implements View.OnClickListener
{
    private static final int FLIPPER_VIRTUAL_INDEX = 0;
    private static final int FLIPPER_LIVE_INDEX = 1;

    private boolean mIsLive;

    private PublishSubject<Boolean> mSwitchSubject;

    public LiveSwitcher(Context context)
    {
        super(context);
        init();
    }

    public LiveSwitcher(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        setAnimateFirstView(false);
        mSwitchSubject = PublishSubject.create();
        addView(LayoutInflater.from(getContext()).inflate(R.layout.live_switch_virtual_textview, this, false));
        addView(LayoutInflater.from(getContext()).inflate(R.layout.live_switch_live_imageview, this, false));

        setOnClickListener(this);
        setIsLive(mIsLive);
    }

    @Override public void onClick(View v)
    {
        mIsLive = !mIsLive;
        updateComponents();
    }

    public void setIsLive(boolean isLive)
    {
        this.mIsLive = isLive;
        updateComponents();
    }

    private void updateComponents()
    {
        setInAnimation(getContext(), mIsLive ? R.anim.push_up_in : R.anim.push_down_in);
        setOutAnimation(getContext(), mIsLive ? R.anim.push_up_out : R.anim.push_down_out);
        setDisplayedChild(mIsLive ? FLIPPER_LIVE_INDEX : FLIPPER_VIRTUAL_INDEX);
        mSwitchSubject.onNext(mIsLive);
    }

    public Observable<Boolean> getSwitchObservable()
    {
        return mSwitchSubject.distinctUntilChanged().asObservable();
    }
}
