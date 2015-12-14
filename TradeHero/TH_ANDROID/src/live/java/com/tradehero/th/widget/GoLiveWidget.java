package com.tradehero.th.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import rx.android.view.OnClickEvent;
import rx.Observable;
import rx.android.view.ViewObservable;

// Live Call to action Banner - pre 3.4
public class GoLiveWidget extends FrameLayout
{
    @Bind(R.id.go_live_button) ImageButton goLiveButton;
    @Bind(R.id.dismiss_live_widget) Button dismissLiveWidgetButton;

    public GoLiveWidget(Context context)
    {
        super(context);
        //init();
    }

    public GoLiveWidget(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        //init();
    }

    public GoLiveWidget(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        //init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public GoLiveWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        //init();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateButtonImage(int resourceId)
    {
        goLiveButton.setImageDrawable(ContextCompat.getDrawable(getContext(), resourceId));
    }

    public Observable<OnClickEvent> getGoLiveButtonClickedObservable()
    {
        return ViewObservable.clicks(goLiveButton);
    }

    public Observable<OnClickEvent> getDismissLiveWidgetButtonClickedObservable()
    {
        return ViewObservable.clicks(dismissLiveWidgetButton);
    }
}
