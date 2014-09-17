package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.user.UserProfileCache;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import javax.inject.Inject;

public class RegionalNewsHeadlineFragment extends NewsHeadlineFragment
{
    public static final String REGION_CHANGED = RegionalNewsHeadlineFragment.class + ".regionChanged";

    @Inject Locale locale;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private BroadcastReceiver regionChangeBroadcastReceiver;

    public RegionalNewsHeadlineFragment()
    {
        super(null);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        newsItemListKey = new NewsItemListRegionalKey(locale.getCountry(), locale.getLanguage(), null, null);
        userProfileListener = new FetchUserProfileListener();
        regionChangeBroadcastReceiver = new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                String countryCode = intent.getStringExtra(CountryLanguagePairDTO.BUNDLE_KEY_COUNTRY_CODE);
                String languageCode = intent.getStringExtra(CountryLanguagePairDTO.BUNDLE_KEY_LANGUAGE_CODE);
                fetchNewsForRegion(countryCode, languageCode);
            }
        };
    }

    private void fetchNewsForRegion(String countryCode, String languageCode)
    {
        newsItemListKey = new NewsItemListRegionalKey(countryCode, languageCode, null, null);
        super.refreshNews();
    }

    @Override public void onDestroyView()
    {
        detachFetchUserProfileTask();
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(regionChangeBroadcastReceiver, new IntentFilter(REGION_CHANGED));
    }

    @Override public void onPause()
    {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(regionChangeBroadcastReceiver);
        super.onPause();
    }

    @Override protected void refreshNews()
    {
        detachFetchUserProfileTask();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    private void detachFetchUserProfileTask()
    {
        if (userProfileListener != null)
        {
            userProfileCache.unregister(userProfileListener);
        }
    }

    private class FetchUserProfileListener implements DTOCacheNew.HurriedListener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onPreCachedDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            onDTOReceived(key, value);
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean display)
    {
        if (display)
        {
            fetchNewsForRegion(userProfileDTO.countryCode, locale.getLanguage());
        }
    }
}
