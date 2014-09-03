package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.Locale;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class RegionalNewsHeadlineFragment extends NewsHeadlineFragment
{
    @Inject Locale locale;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileListener;

    public RegionalNewsHeadlineFragment()
    {
        super(null);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        newsItemListKey = new NewsItemListRegionalKey(locale.getCountry(), locale.getLanguage(), null, null);
        userProfileListener = new FetchUserProfileListener();
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
        Timber.d("Country code: %s", userProfileDTO.countryCode);
        newsItemListKey = new NewsItemListRegionalKey(userProfileDTO.countryCode, locale.getLanguage(), null, null);
        
        if (display)
        {
            super.refreshNews();
        }
    }
}
