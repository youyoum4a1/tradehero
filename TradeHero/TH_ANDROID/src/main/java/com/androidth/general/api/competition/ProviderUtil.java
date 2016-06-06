package com.androidth.general.api.competition;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.network.CompetitionUrl;
import com.androidth.general.persistence.prefs.AuthHeader;
import javax.inject.Inject;

public class ProviderUtil
{
    public static final String LANDING = "landing/";
    public static final String QUERY_KEY_PROVIDER_ID = "providerId";
    public static final String QUERY_KEY_USER_ID = "userId";
    public static final String QUERY_KEY_AUTHORISATION = "authorization";
    public static final String QUERY_KEY_SHOW_NEXT_BUTTON = "showNextButton";

    public static final String RULES = "rules";

    public static final String TERMS = "terms";

    public static final String WIZARD = "wizard/";
    public static final String QUERY_KEY_FULL_SCREEN = "fullScreen";

    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final String competitionUrl;
    @NonNull private final String authenticationHeader;

    //<editor-fold desc="Constructors">
    @Inject ProviderUtil(
            @NonNull CurrentUserId currentUserId,
            @CompetitionUrl @NonNull String competitionUrl,
            @AuthHeader @NonNull String authenticationHeader)
    {
        this.currentUserId = currentUserId;
        this.competitionUrl = competitionUrl;
        this.authenticationHeader = authenticationHeader;
    }
    //</editor-fold>

    @NonNull public String getLandingPage(@NonNull ProviderId providerId)
    {
        String url = competitionUrl + LANDING;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&');
        url = appendShowNextButton(url, '&');
        return appendAuthorization(url, '&');
    }

    @NonNull public String getRulesPage(@NonNull ProviderId providerId)
    {
        return getRulesPage(providerId, false);
    }

    @NonNull public String getRulesPage(@NonNull ProviderId providerId, boolean showNextButton)
    {
        String url = competitionUrl + RULES;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&');
        if (showNextButton)
        {
            url = appendShowNextButton(url, '&');
        }
        return appendAuthorization(url, '&');
    }

    @NonNull public String getTermsPage(@NonNull ProviderId providerId)
    {
        String url = competitionUrl + TERMS;
        url = appendProviderId(url, '?', providerId);
        url = appendUserId(url, '&');
        return appendAuthorization(url, '&');
    }

    @NonNull public String getWizardPage(@NonNull ProviderId providerId)
    {
        String url = competitionUrl + WIZARD;
        url = appendProviderId(url, '?', providerId);
        url = appendFullScreen(url, '&');
        return appendAuthorization(url, '&');
    }

    @NonNull public String appendProviderId(@NonNull String url, char separator, @NonNull ProviderId providerId)
    {
        return appendToUrl(url, separator + QUERY_KEY_PROVIDER_ID + "=" + providerId.key);
    }

    @NonNull public String appendUserId(@NonNull String url, char separator)
    {
        return appendToUrl(url, separator + QUERY_KEY_USER_ID + "=" + currentUserId.get());
    }

    @NonNull public String appendToUrl(@NonNull String url, @NonNull String forAppend)
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

    @NonNull public String appendShowNextButton(@NonNull String url, char separator)
    {
        return url + separator + QUERY_KEY_SHOW_NEXT_BUTTON + "=1";
    }

    @NonNull public String appendAuthorization(@NonNull String url, char separator)
    {
        return url + separator + QUERY_KEY_AUTHORISATION + "=" + Uri.encode(authenticationHeader);
    }

    @NonNull public String appendFullScreen(@NonNull String url, char separator)
    {
        return url + separator + QUERY_KEY_FULL_SCREEN + "=1";
    }
}
