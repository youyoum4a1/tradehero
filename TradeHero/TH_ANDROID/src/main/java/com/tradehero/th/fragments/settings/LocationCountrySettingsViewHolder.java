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
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class LocationCountrySettingsViewHolder implements SettingViewHolder
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserProfileCache userProfileCache;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    protected DashboardPreferenceFragment preferenceFragment;
    protected Preference locationPreference;

    //<editor-fold desc="Constructors">
    @Inject public LocationCountrySettingsViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache)
    {
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
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

    @Override public void destroyViews()
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
            Country currentCountry = null;
            try
            {
                currentCountry = Country.valueOf(userProfileDTO.countryCode);
            }
            catch (IllegalArgumentException e)
            {
                Timber.e(e, "Unhandled countryCode %s", userProfileDTO.countryCode);
            }
            if (currentCountry != null)
            {
                String summary = preferenceFragment.getString(
                        R.string.location_summary,
                        userProfileDTO.countryCode,
                        preferenceFragment.getString(currentCountry.locationName));
                locationPreference.setSummary(summary);
                locationPreference.setIcon(currentCountry.logoId);
            }
        }
    }

    protected void handleLocationClicked()
    {
        preferenceFragment.getNavigator().pushFragment(LocationListFragment.class);
    }

    protected class UserProfileCacheListener implements DTOCacheNew.HurriedListener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onPreCachedDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            updateLocation();
        }

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
