package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class MessageItemViewWrapper extends FrameLayout implements DTOView<MessageHeaderDTO>
{
    @InjectView(R.id.swipelist_frontview) MessageItemView messageItemView;
    @InjectView(R.id.swipelist_backview) View messageItemBackView;

    @NonNull private BehaviorSubject<MessageItemView.UserAction> userActionBehavior;
    private Subscription itemViewSubscription;

    //<editor-fold desc="Constructors">
    public MessageItemViewWrapper(Context context)
    {
        super(context);
        userActionBehavior = BehaviorSubject.create();
    }

    public MessageItemViewWrapper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userActionBehavior = BehaviorSubject.create();
    }

    public MessageItemViewWrapper(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        userActionBehavior = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        itemViewSubscription = messageItemView.getUserActionObservable().subscribe(userActionBehavior);
    }

    @Override protected void onDetachedFromWindow()
    {
        itemViewSubscription.unsubscribe();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<MessageItemView.UserAction> getUserActionObservable()
    {
        return userActionBehavior.asObservable();
    }

    @Override public void display(MessageHeaderDTO dto)
    {
        if (messageItemView != null)
        {
            messageItemView.display(dto);
        }
    }
}
