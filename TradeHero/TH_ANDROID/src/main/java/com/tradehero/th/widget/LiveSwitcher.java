package com.tradehero.th.widget;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextSwitcher;
import com.tradehero.th.R;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LiveSwitcher extends TextSwitcher implements View.OnClickListener
{
    private String mLiveText;
    private String mVirtualText;
    private boolean mIsLive;
    @ColorRes private static int LIVE_BG_COLOR_RES_ID = R.color.light_green_normal;
    @ColorRes private static int VIRTUAL_BG_COLOR_RES_ID = R.color.grey_transparent;

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
        mSwitchSubject = PublishSubject.create();
        mLiveText = getContext().getString(R.string.live);
        mVirtualText = getContext().getString(R.string.virtual);

        setFactory(new ViewFactory()
        {
            @Override public View makeView()
            {
                return LayoutInflater.from(getContext()).inflate(R.layout.live_switch_textview, LiveSwitcher.this, false);
            }
        });

        setOnClickListener(this);
        setInAnimation(getContext(), R.anim.push_up_in);
        setOutAnimation(getContext(), R.anim.push_up_out);
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
        setText(mIsLive ? mLiveText : mVirtualText);
        mSwitchSubject.onNext(mIsLive);
        getCurrentView().setBackgroundColor(getResources().getColor(mIsLive ? LIVE_BG_COLOR_RES_ID : VIRTUAL_BG_COLOR_RES_ID));
    }

    public Observable<Boolean> getSwitchObservable()
    {
        return mSwitchSubject.asObservable();
    }
}
