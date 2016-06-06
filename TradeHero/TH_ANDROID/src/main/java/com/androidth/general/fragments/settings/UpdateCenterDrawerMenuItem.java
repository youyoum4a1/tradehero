package com.androidth.general.fragments.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.TimberOnErrorAction1;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;

public class UpdateCenterDrawerMenuItem extends LinearLayout
{
    @Bind(R.id.unread_count) TextView tvUnreadCount;

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
            ButterKnife.bind(this);
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
                        new TimberOnErrorAction1("Failed to get userProfile in LeftMenu")));
    }

    @Override protected void onDetachedFromWindow()
    {
        onDetachedSubscriptions.unsubscribe();
        super.onDetachedFromWindow();
    }

    public void linkWith(@NonNull UserProfileDTO currentUserProfile)
    {
        int unReadCount = currentUserProfile.unreadMessageThreadsCount;
        tvUnreadCount.setVisibility(unReadCount > 0 ? View.VISIBLE : View.INVISIBLE);
        tvUnreadCount.setText(" " + unReadCount + " ");
    }
}
