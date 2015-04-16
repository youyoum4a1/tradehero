package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.internal.util.SubscriptionList;

public class SettingsResideMenuItemUpdateCenter extends LinearLayout
{
    @InjectView(R.id.unread_Count) TextView tvUnreadCount;

    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;

    protected SubscriptionList onStopSubscriptions;
    private int unReadCount = 0;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public SettingsResideMenuItemUpdateCenter(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public SettingsResideMenuItemUpdateCenter(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public SettingsResideMenuItemUpdateCenter(Context context, AttributeSet attrs, int defStyle)
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
        tvUnreadCount.setVisibility(hasUnVisitedSetting() ? View.VISIBLE : View.INVISIBLE);
        tvUnreadCount.setText(" "+unReadCount+" ");
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    public boolean hasUnVisitedSetting()
    {
        unReadCount = userProfileCache.getCachedValue(currentUserId.toUserBaseKey()).unreadNotificationsCount;
        return unReadCount > 0;
    }

    public void refresh() {
        tvUnreadCount.setVisibility(hasUnVisitedSetting() ? View.VISIBLE : View.INVISIBLE);
        tvUnreadCount.setText(" "+unReadCount+" ");
    }
}
