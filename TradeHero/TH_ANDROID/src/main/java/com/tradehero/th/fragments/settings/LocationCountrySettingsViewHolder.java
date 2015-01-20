package com.tradehero.th.fragments.settings;

import android.support.annotation.NonNull;
import android.support.v4.preference.PreferenceFragment;
import android.util.Pair;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.location.LocationListFragment;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class LocationCountrySettingsViewHolder extends OneSettingViewHolder
{
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final UserProfileCacheRx userProfileCache;
    protected Subscription userProfileCacheSubscription;

    //<editor-fold desc="Constructors">
    @Inject public LocationCountrySettingsViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        fetchUserProfile();
    }

    @Override public void destroyViews()
    {
        detachUserProfileCache();
        super.destroyViews();

    }

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_location;
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
        .subscribe(new UserProfileCacheObserver());
    }

    public void updateLocation()
    {
        UserProfileDTO userProfileDTO = userProfileCache.getCachedValue(currentUserId.toUserBaseKey());
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
                PreferenceFragment preferenceFragmentCopy = preferenceFragment;
                if (preferenceFragmentCopy != null)
                {
                    String summary = preferenceFragmentCopy.getString(
                            R.string.location_summary,
                            userProfileDTO.countryCode,
                            preferenceFragmentCopy.getString(currentCountry.locationName));
                    clickablePref.setSummary(summary);
                }
                // TODO superimpose flag?
            }
        }
    }

    protected void handlePrefClicked()
    {
        DashboardPreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            preferenceFragmentCopy.getNavigator().pushFragment(LocationListFragment.class);
        }
    }

    protected class UserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> userBaseKeyUserProfileDTOPair)
        {
            updateLocation();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Error fetching the user profile", e);
        }
    }
}
