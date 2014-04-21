package com.tradehero.th.network.retrofit;

import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.base.THUser;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.persistence.prefs.SessionToken;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.VersionUtils;
import javax.inject.Inject;
import retrofit.RequestInterceptor;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:10 AM Copyright (c) TradeHero
 */
public class RequestHeaders implements RequestInterceptor
{
    private final StringPreference sessionToken;
    private final String version;
    private final String languageCode;

    @Inject public RequestHeaders(
            Context context,
            @SessionToken StringPreference sessionToken,
            @LanguageCode String languageCode)
    {
        this.sessionToken = sessionToken;
        this.version = VersionUtils.getVersionId(context);
        this.languageCode = languageCode;
    }

    @Override
    public void intercept(RequestFacade request)
    {
        if (sessionToken.isSet())
        {
            buildAuthorizationHeader(request);
        }
    }

    private void buildAuthorizationHeader(RequestInterceptor.RequestFacade request)
    {
        //TODO use api like login(@Header("Authorization") String authorization,...) may override these header

        request.addHeader(Constants.TH_CLIENT_VERSION, version);
        request.addHeader(Constants.AUTHORIZATION, THUser.getAuthHeader());
        request.addHeader(Constants.TH_LANGUAGE_CODE, languageCode);
        Timber.d("buildAuthorizationHeader AUTHORIZATION: %s",THUser.getAuthHeader());
    }
}