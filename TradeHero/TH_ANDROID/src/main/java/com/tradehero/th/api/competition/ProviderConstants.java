package com.tradehero.th.api.competition;

import android.net.Uri;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.THUser;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;

/**
 * Created by xavier on 1/20/14.
 */
public class ProviderConstants
{
    public static final String TAG = ProviderConstants.class.getSimpleName();

    public static final String PAGES_URL = Constants.BASE_API_URL + "competitionpages/";

    public static final String LANDING = "landing/";
    public static final String LANDING_URL = PAGES_URL + LANDING;
    public static final String QUERY_KEY_PROVIDER_ID = "providerId";
    public static final String QUERY_KEY_USER_ID = "userId";
    public static final String QUERY_KEY_AUTHORISATION = "authorization";
    public static final String QUERY_KEY_SHOW_NEXT_BUTTON = "showNextButton";

    public static final String RULES = "rules/";
    public static final String RULES_URL = PAGES_URL + RULES;

    public static final String TERMS = "terms/";
    public static final String TERMS_URL = PAGES_URL + TERMS;

    public static final String WIZARD = "wizard/";
    public static final String WIZARD_URL = PAGES_URL + WIZARD;
    public static final String QUERY_KEY_FULL_SCREEN = "fullScreen";

    @Inject static CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    public static String getLandingPage(ProviderId providerId, UserBaseKey userBaseKey)
    {
        String url = LANDING_URL;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&', userBaseKey);
        url = appendShowNextButton(url, '&');
        return appendAuthorization(url, '&');
    }

    public static String getRulesPage(ProviderId providerId)
    {
        return getRulesPage(providerId, false);
    }

    public static String getRulesPage(ProviderId providerId, boolean showNextButton)
    {
        String url = RULES_URL;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&', currentUserBaseKeyHolder.getCurrentUserBaseKey());
        if (showNextButton)
        {
            url = appendShowNextButton(url, '&');
        }
        return appendAuthorization(url, '&');
    }

    public static String getTermsPage(ProviderId providerId)
    {
        String url = TERMS_URL;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&', currentUserBaseKeyHolder.getCurrentUserBaseKey());
        return appendAuthorization(url, '&');
    }

    public static String getWizardPage(ProviderId providerId)
    {
        String url = WIZARD_URL;
        url = appendProviderId(url, '?', providerId);
        url = appendFullScreen(url, '&');
        return appendAuthorization(url, '&');
    }

    public static String appendProviderId(String url, char separator, ProviderId providerId)
    {
        return url + separator + QUERY_KEY_PROVIDER_ID + "=" + providerId.key;
    }

    public static String appendUserId(String url, char separator, UserBaseKey userBaseKey)
    {
        return url + separator + QUERY_KEY_USER_ID + "=" + userBaseKey.key;
    }

    public static String appendShowNextButton(String url, char separator)
    {
        return url + separator + QUERY_KEY_SHOW_NEXT_BUTTON + "=1";
    }

    public static String appendAuthorization(String url, char separator)
    {
        return url + separator + QUERY_KEY_AUTHORISATION + "=" + Uri.encode(THUser.getAuthHeader());
    }

    public static String appendFullScreen(String url, char separator)
    {
        return url + separator + QUERY_KEY_FULL_SCREEN + "=1";
    }
}
