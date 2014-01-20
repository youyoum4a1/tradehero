package com.tradehero.th.api.competition;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.Constants;

/**
 * Created by xavier on 1/20/14.
 */
public class ProviderConstants
{
    public static final String TAG = ProviderConstants.class.getSimpleName();

    public static final String PAGES_URL = Constants.BASE_API_URL + "competitionpages/";
    public static final String LANDING_URL = PAGES_URL + "landing/";

    public static String getLandingPage(ProviderId providerId, UserBaseKey userBaseKey)
    {
        return LANDING_URL + "?providerId=" + providerId.key + "&userId=" + userBaseKey.key;
    }

    public static String getLandingPage(ProviderId providerId, UserBaseKey userBaseKey, String previous)
    {
        return null;
    }
}
