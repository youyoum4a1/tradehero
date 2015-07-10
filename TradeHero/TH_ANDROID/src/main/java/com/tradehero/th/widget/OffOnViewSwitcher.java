package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.tradehero.th.R;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class OffOnViewSwitcher extends ViewSwitcher implements View.OnClickListener
{
    private static final int ANIM_DELAY = 300;

    private boolean mIsOn;
    private PublishSubject<OffOnViewSwitcherEvent> mSwitchSubject;

    public OffOnViewSwitcher(Context context)
    {
        this(context, null);
    }

    public OffOnViewSwitcher(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs)
    {
        setAnimateFirstView(false);
        mSwitchSubject = PublishSubject.create();
        setOnClickListener(this);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.OffOnViewSwitcher, 0, 0);

        @LayoutRes int offLayoutId = a.getResourceId(R.styleable.OffOnViewSwitcher_offLayout, R.layout.off_on_switcher_textview_off);
        @LayoutRes int onLayoutId = a.getResourceId(R.styleable.OffOnViewSwitcher_onLayout, R.layout.off_on_switcher_textview_on);

        View offView = LayoutInflater.from(getContext()).inflate(offLayoutId, this, false);
        if (offView instanceof TextView && TextUtils.isEmpty(((TextView) offView).getText()))
        {
            ((TextView) offView).setText(a.getString(R.styleable.OffOnViewSwitcher_offText));
        }

        View onView = LayoutInflater.from(getContext()).inflate(onLayoutId, this, false);
        if (onView instanceof TextView && TextUtils.isEmpty(((TextView) onView).getText()))
        {
            ((TextView) onView).setText(a.getString(R.styleable.OffOnViewSwitcher_onText));
        }

        addView(offView);
        addView(onView);

        a.recycle();
    }

    @Override public void onClick(View v)
    {
        setIsOn(!mIsOn, true);
    }

    public void setIsOn(boolean isOn, boolean isFromUser)
    {
        boolean changed = this.mIsOn ^ isOn;
        this.mIsOn = isOn;
        setupAnimationToBeUsed(true);
        setDisplayedChild(mIsOn ? 1 : 0);
        if (changed)
        {
            mSwitchSubject.onNext(new OffOnViewSwitcherEvent(isFromUser, mIsOn));
        }
    }

    protected void setupAnimationToBeUsed(boolean animate)
    {
        setInAnimation(animate ? AnimationUtils.loadAnimation(getContext(), mIsOn ? R.anim.push_up_in : R.anim.push_down_in) : null);
        setOutAnimation(animate ? AnimationUtils.loadAnimation(getContext(), mIsOn ? R.anim.push_up_out : R.anim.push_down_out) : null);
    }

    public Observable<OffOnViewSwitcherEvent> getSwitchObservable()
    {
        return mSwitchSubject.delay(ANIM_DELAY, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).distinctUntilChanged(
                new Func1<OffOnViewSwitcherEvent, Boolean>()
                {
                    @Override public Boolean call(OffOnViewSwitcherEvent offOnViewSwitcherEvent)
                    {
                        return offOnViewSwitcherEvent.isOn;
                    }
                });
    }
}
