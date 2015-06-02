package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;
import rx.Observable;
import rx.subjects.PublishSubject;

public class FollowerListCallToActionItemView extends LinearLayout
{
    @NonNull private final PublishSubject<FollowerListItemAdapter.UserAction> tradeClickedSubject;

    //<editor-fold desc="Constructors">
    public FollowerListCallToActionItemView(Context context)
    {
        super(context);
        tradeClickedSubject = PublishSubject.create();
    }

    public FollowerListCallToActionItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        tradeClickedSubject = PublishSubject.create();
    }

    public FollowerListCallToActionItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        tradeClickedSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<FollowerListItemAdapter.UserAction> getTradeClickedObservable()
    {
        return tradeClickedSubject.asObservable();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_trade_now)
    void onButtonTradeClicked(View view)
    {
        tradeClickedSubject.onNext(new TradeNowUserAction());
    }

    public static class TradeNowUserAction implements FollowerListItemAdapter.UserAction
    {
    }
}
