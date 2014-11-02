package com.tradehero.th.fragments.settings;

import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class AlipaySettingViewHolder extends OneSettingViewHolder
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserProfileCacheRx userProfileCache;
    @Nullable Subscription userProfileCacheSubscription;
    @Nullable private UserProfileDTO userProfileDTO;

    //<editor-fold desc="Constructors">
    @Inject public AlipaySettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCacheRx userProfileCache)
    {
        super();
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        fetchUserProfile();
    }

    @Override public void destroyViews()
    {
        detachUserProfileCache();
        super.destroyViews();
    }

    protected void detachUserProfileCache()
    {
        Subscription copy = userProfileCacheSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        userProfileCacheSubscription = null;
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCacheSubscription = userProfileCache.get(currentUserId.toUserBaseKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileCacheObserver());
    }

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_alipay;
    }

    @Override protected void handlePrefClicked()
    {
        DashboardPreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            preferenceFragmentCopy.getNavigator().pushFragment(SettingsAlipayFragment.class);
        }
    }

    private Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new Observer<Pair<UserBaseKey,UserProfileDTO>>()
        {
            @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
            {
                setUserProfile(pair.second);
            }

            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
                setUserProfile(null);
            }
        };
    }

    protected void setUserProfile(@Nullable UserProfileDTO userProfile)
    {
        this.userProfileDTO = userProfile;
        clickablePref.setSummary(getSubtitleText());
    }

    @Nullable protected String getSubtitleText()
    {
        if (userProfileDTO == null || userProfileDTO.alipayAccount == null)
        {
            return null;
        }
        return userProfileDTO.alipayAccount;
    }
}
