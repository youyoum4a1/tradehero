package com.androidth.general.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.androidth.general.R;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class OffOnViewSwitcher extends LinearLayout
        implements View.OnClickListener
{
    private static final int ANIM_DELAY = 300;

    @Bind(R.id.off_on_view_switcher) ViewSwitcher offOnViewSwitcher;
    private boolean mIsOn;
    private PublishSubject<OffOnViewSwitcherEvent> mSwitchSubject;

    public OffOnViewSwitcher(Context context)
    {
        this(context, null);
    }

    public OffOnViewSwitcher(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public OffOnViewSwitcher(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OffOnViewSwitcher(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.off_on_switcher_merged, this);
        ButterKnife.bind(this);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        offOnViewSwitcher.setForegroundGravity(Gravity.CENTER_VERTICAL);
        offOnViewSwitcher.setAnimateFirstView(false);
        mSwitchSubject = PublishSubject.create();

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.OffOnViewSwitcher, 0, 0);

        @LayoutRes int offLayoutId = a.getResourceId(R.styleable.OffOnViewSwitcher_offLayout, R.layout.off_on_switcher_default_textview);
        @LayoutRes int onLayoutId = a.getResourceId(R.styleable.OffOnViewSwitcher_onLayout, R.layout.off_on_switcher_default_textview);

        View offView = LayoutInflater.from(getContext()).inflate(offLayoutId, offOnViewSwitcher, false);
        if (offView instanceof TextView && TextUtils.isEmpty(((TextView) offView).getText()))
        {
            ((TextView) offView).setText(a.getString(R.styleable.OffOnViewSwitcher_offText));
        }

        View onView = LayoutInflater.from(getContext()).inflate(onLayoutId, offOnViewSwitcher, false);
        if (onView instanceof TextView && TextUtils.isEmpty(((TextView) onView).getText()))
        {
            ((TextView) onView).setText(a.getString(R.styleable.OffOnViewSwitcher_onText));
        }

        offOnViewSwitcher.addView(offView);
        offOnViewSwitcher.addView(onView);

        a.recycle();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        setOnClickListener(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        setOnClickListener(null);
        super.onDetachedFromWindow();
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
        offOnViewSwitcher.setDisplayedChild(mIsOn ? 1 : 0);
        if (changed)
        {
            mSwitchSubject.onNext(new OffOnViewSwitcherEvent(isFromUser, mIsOn));
        }
    }

    protected void setupAnimationToBeUsed(boolean animate)
    {
        offOnViewSwitcher.setInAnimation(
                animate ? AnimationUtils.loadAnimation(getContext(), mIsOn ? R.anim.push_up_in : R.anim.push_down_in) : null);
        offOnViewSwitcher.setOutAnimation(
                animate ? AnimationUtils.loadAnimation(getContext(), mIsOn ? R.anim.push_up_out : R.anim.push_down_out) : null);
    }

    @NonNull public Observable<OffOnViewSwitcherEvent> getSwitchObservable()
    {
        return mSwitchSubject.delay(ANIM_DELAY, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .distinctUntilChanged(
                        new Func1<OffOnViewSwitcherEvent, Boolean>()
                        {
                            @Override public Boolean call(OffOnViewSwitcherEvent offOnViewSwitcherEvent)
                            {
                                return offOnViewSwitcherEvent.isOn;
                            }
                        });
    }
}
