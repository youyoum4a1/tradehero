package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.authentication.SocialNetworkButtonListLinear;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class LeaderboardFriendCallToActionItemView extends LinearLayout
{
    @InjectView(R.id.social_network_button_list) SocialNetworkButtonListLinear socialNetworkButtonList;

    @NonNull private final PublishSubject<SocialNetworkEnum> socialNetworkEnumSubject;
    private Subscription buttonSubscription;

    //<editor-fold desc="Constructors">
    public LeaderboardFriendCallToActionItemView(Context context)
    {
        super(context);
        socialNetworkEnumSubject = PublishSubject.create();
    }

    public LeaderboardFriendCallToActionItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        socialNetworkEnumSubject = PublishSubject.create();
    }

    public LeaderboardFriendCallToActionItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        socialNetworkEnumSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<SocialNetworkEnum> getSocialNetworkEnumObservable()
    {
        return socialNetworkEnumSubject.asObservable();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        buttonSubscription = socialNetworkButtonList.getSocialNetworkEnumSubject()
                .subscribe(socialNetworkEnumSubject);
    }

    @Override protected void onDetachedFromWindow()
    {
        buttonSubscription.unsubscribe();
        super.onDetachedFromWindow();
    }
}
