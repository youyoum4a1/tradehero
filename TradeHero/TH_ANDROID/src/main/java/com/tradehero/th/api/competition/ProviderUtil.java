package com.tradehero.th.api.competition;

import android.net.Uri;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.THUser;
import com.tradehero.th.network.CompetitionUrl;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ProviderUtil
{
    public static final String LANDING = "landing/";
    public static final String QUERY_KEY_PROVIDER_ID = "providerId";
    public static final String QUERY_KEY_USER_ID = "userId";
    public static final String QUERY_KEY_AUTHORISATION = "authorization";
    public static final String QUERY_KEY_SHOW_NEXT_BUTTON = "showNextButton";

    public static final String RULES = "rules/";

    public static final String TERMS = "terms/";

    public static final String WIZARD = "wizard/";
    public static final String QUERY_KEY_FULL_SCREEN = "fullScreen";

    private final CurrentUserId currentUserId;
    private final String competitionUrl;

    @Inject ProviderUtil(CurrentUserId currentUserId, @CompetitionUrl String competitionUrl)
    {
        this.currentUserId = currentUserId;
        this.competitionUrl = competitionUrl;
    }

    public String getLandingPage(ProviderId providerId, UserBaseKey userBaseKey)
    {
        String url = competitionUrl + LANDING;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&', userBaseKey);
        url = appendShowNextButton(url, '&');
        return appendAuthorization(url, '&');
    }

    public String getRulesPage(ProviderId providerId)
    {
        return getRulesPage(providerId, false);
    }

    public String getRulesPage(ProviderId providerId, boolean showNextButton)
    {
        String url = competitionUrl + RULES;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&', currentUserId.toUserBaseKey());
        if (showNextButton)
        {
            url = appendShowNextButton(url, '&');
        }
        return appendAuthorization(url, '&');
    }

    public String getTermsPage(ProviderId providerId)
    {
        String url = competitionUrl + TERMS;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&', currentUserId.toUserBaseKey());
        return appendAuthorization(url, '&');
    }

    public String getWizardPage(ProviderId providerId)
    {
        String url = competitionUrl + WIZARD;
        url = appendProviderId(url, '?', providerId);
        url = appendFullScreen(url, '&');
        return appendAuthorization(url, '&');
    }

    public String appendProviderId(String url, char separator, ProviderId providerId)
    {
        return appendToUrl(url, separator + QUERY_KEY_PROVIDER_ID + "=" + providerId.key);
    }

    public String appendUserId(String url, char separator, UserBaseKey userBaseKey)
    {
        return appendToUrl(url, separator + QUERY_KEY_USER_ID + "=" + userBaseKey.key);
    }

    public String appendToUrl(String url, String forAppend)
    {
        if (url.contains("?") || forAppend.startsWith("?"))
        {
            return url + forAppend;
        }
        else
        {
            return url + "?" + forAppend;
        }
    }

    public String appendShowNextButton(String url, char separator)
    {
        return url + separator + QUERY_KEY_SHOW_NEXT_BUTTON + "=1";
    }

    public String appendAuthorization(String url, char separator)
    {
        return url + separator + QUERY_KEY_AUTHORISATION + "=" + Uri.encode(THUser.getAuthHeader());
    }

    public String appendFullScreen(String url, char separator)
    {
        return url + separator + QUERY_KEY_FULL_SCREEN + "=1";
    }
}
