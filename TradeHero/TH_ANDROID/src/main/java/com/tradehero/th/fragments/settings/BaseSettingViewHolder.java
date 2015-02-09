package com.tradehero.th.fragments.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.preference.PreferenceFragment;
import rx.Subscription;
import rx.internal.util.SubscriptionList;

abstract public class BaseSettingViewHolder implements SettingViewHolder
{
    @Nullable protected DashboardPreferenceFragment preferenceFragment;
    @NonNull protected SubscriptionList subscriptions;

    //<editor-fold desc="Constructors">
    protected BaseSettingViewHolder()
    {
        this.subscriptions = new SubscriptionList();
    }
    //</editor-fold>

    @Override public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        this.preferenceFragment = preferenceFragment;
    }

    @Override public void destroyViews()
    {
        subscriptions.unsubscribe();
        subscriptions = new SubscriptionList();
        this.preferenceFragment = null;
    }

    @Nullable protected String getString(@StringRes int stringResId)
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            return preferenceFragmentCopy.getString(stringResId);
        }
        return null;
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }
}
