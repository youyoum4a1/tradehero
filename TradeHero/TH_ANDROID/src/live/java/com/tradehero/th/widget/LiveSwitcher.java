package com.tradehero.th.widget;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
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

    private PublishSubject<Event> mSwitchSubject;

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
    }

    @Override public void onClick(View v)
    {
        boolean isUser = true;
        mIsLive = !mIsLive;
        updateComponents(isUser);
        mSwitchSubject.onNext(new Event(isUser, mIsLive));
    }

    public void setIsLive(boolean isLive, boolean animate)
    {
        boolean changed = this.mIsLive ^ isLive;
        this.mIsLive = isLive;
        updateComponents(animate);
        if (changed)
        {
            mSwitchSubject.onNext(new Event(false, mIsLive));
        }
    }

    private void updateComponents(boolean animate)
    {
        setInAnimation(animate ? AnimationUtils.loadAnimation(getContext(), mIsLive ? R.anim.push_up_in : R.anim.push_down_in) : null);
        setOutAnimation(animate ? AnimationUtils.loadAnimation(getContext(), mIsLive ? R.anim.push_up_out : R.anim.push_down_out) : null);
        setDisplayedChild(mIsLive ? FLIPPER_LIVE_INDEX : FLIPPER_VIRTUAL_INDEX);
    }

    public Observable<Event> getSwitchObservable()
    {
        return mSwitchSubject.distinctUntilChanged();
    }

    public static class Event
    {
        public boolean isFromUser;
        public boolean isLive;

        public Event(boolean isFromUser, boolean isLive)
        {
            this.isFromUser = isFromUser;
            this.isLive = isLive;
        }

        @Override public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Event)) return false;

            Event event = (Event) o;

            return isLive == event.isLive;
        }
    }
}
