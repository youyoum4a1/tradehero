package com.tradehero.th.fragments.settings;

import android.preference.Preference;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.location.LocationListFragment;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class LocationCountrySettingsViewHolder
{
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    protected DashboardPreferenceFragment preferenceFragment;
    protected Preference locationPreference;

    public void initViews(DashboardPreferenceFragment preferenceFragment)
    {
        DaggerUtils.inject(this);
        this.preferenceFragment = preferenceFragment;

        locationPreference =
                preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_location));
        if (locationPreference != null)
        {
            locationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleLocationClicked();
                    return true;
                }
            });
        }

        userProfileCacheListener = new UserProfileCacheListener();
        fetchUserProfile();
    }

    public void destroyViews()
    {
        detachUserProfileCache();

        locationPreference = null;
        preferenceFragment = null;
        userProfileCacheListener = null;
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(currentUserId.toUserBaseKey(), userProfileCacheListener);
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    public void updateLocation()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null && userProfileDTO.countryCode != null)
        {
            locationPreference.setSummary(userProfileDTO.countryCode);
            if (Country.valueOf(userProfileDTO.countryCode) != null)
            {
                locationPreference.setIcon(Country.valueOf(userProfileDTO.countryCode).logoId);
            }
        }
    }

    protected void handleLocationClicked()
    {
        preferenceFragment.getNavigator().pushFragment(LocationListFragment.class);
    }

    protected class UserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull final UserBaseKey key, @NotNull final UserProfileDTO value)
        {
            updateLocation();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Error fetching the user profile %s", key, error);
        }
    }
}
