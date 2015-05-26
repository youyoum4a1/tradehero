package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;

public class UpdateCenterDrawerMenuItem extends LinearLayout
{
    @InjectView(R.id.unread_count) TextView tvUnreadCount;

    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;

    protected SubscriptionList onDetachedSubscriptions;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public UpdateCenterDrawerMenuItem(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public UpdateCenterDrawerMenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public UpdateCenterDrawerMenuItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        if (!isInEditMode())
        {
            ButterKnife.inject(this);
            HierarchyInjector.inject(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        onDetachedSubscriptions = new SubscriptionList();
        onDetachedSubscriptions.add(userProfileCache.get(currentUserId.toUserBaseKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<UserBaseKey, UserProfileDTO> userBaseKeyUserProfileDTOPair)
                            {
                                linkWith(userBaseKeyUserProfileDTOPair.second);
                            }
                        },
                        new TimberOnErrorAction("Failed to get userProfile in LeftMenu")));
    }

    @Override protected void onDetachedFromWindow()
    {
        onDetachedSubscriptions.unsubscribe();
        super.onDetachedFromWindow();
    }

    public void linkWith(@NonNull UserProfileDTO currentUserProfile)
    {
        int unReadCount = currentUserProfile.unreadNotificationsCount;
        tvUnreadCount.setVisibility(unReadCount > 0 ? View.VISIBLE : View.INVISIBLE);
        tvUnreadCount.setText(" " + unReadCount + " ");
    }
}
