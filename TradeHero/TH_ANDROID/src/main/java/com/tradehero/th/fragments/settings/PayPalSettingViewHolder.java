package com.tradehero.th.fragments.settings;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PayPalSettingViewHolder extends OneSettingViewHolder
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserProfileCache userProfileCache;
    @Nullable private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Nullable private UserProfileDTO userProfileDTO;

    //<editor-fold desc="Constructors">
    @Inject public PayPalSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache)
    {
        super();
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.userProfileCacheListener = createUserProfileCacheListener();
        fetchUserProfile();
    }
    //</editor-fold>

    @Override public void destroyViews()
    {
        userProfileCache.unregister(userProfileCacheListener);
        userProfileCacheListener = null;
        super.destroyViews();
    }

    protected void fetchUserProfile()
    {
        this.userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        this.userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_paypal;
    }

    @Override protected void handlePrefClicked()
    {
        preferenceFragment.getNavigator().pushFragment(SettingsPayPalFragment.class);
    }

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override
            public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
            {
                setUserProfile(value);
            }

            @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
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
        if (userProfileDTO == null || userProfileDTO.paypalEmailAddress == null)
        {
            return null;
        }
        return userProfileDTO.paypalEmailAddress;
    }
}
