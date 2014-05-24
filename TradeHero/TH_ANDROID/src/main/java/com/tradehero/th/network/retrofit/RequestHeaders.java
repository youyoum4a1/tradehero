package com.tradehero.th.network.retrofit;

import android.content.Context;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.VersionUtils;
import javax.inject.Inject;
import retrofit.RequestInterceptor;

public class RequestHeaders implements RequestInterceptor
{
    private final MainCredentialsPreference mainCredentialsPreference;
    private final String version;
    private final String languageCode;

    @Inject public RequestHeaders(
            Context context,
            MainCredentialsPreference mainCredentialsPreference,
            @LanguageCode String languageCode)
    {
        this.mainCredentialsPreference = mainCredentialsPreference;
        this.version = VersionUtils.getVersionId(context);
        this.languageCode = languageCode;
    }

    @Override
    public void intercept(RequestFacade request)
    {
        if (mainCredentialsPreference.getCredentials() != null)
        {
            buildAuthorizationHeader(request);
        }
        request.addHeader(Constants.TH_LANGUAGE_CODE, languageCode);
        request.addHeader(Constants.TH_CLIENT_TYPE, String.valueOf(DeviceTokenHelper.getDeviceType().getServerValue()));
    }

    private void buildAuthorizationHeader(RequestFacade request)
    {
        request.addHeader(Constants.TH_CLIENT_VERSION, version);
        request.addHeader(Constants.AUTHORIZATION, mainCredentialsPreference.getCredentials().getAuthHeaderParameter());
        //request.addHeader(Constants.TH_LANGUAGE_CODE, languageCode);
        //Timber.d("buildAuthorizationHeader AUTHORIZATION: %s",THUser.getAuthHeader());
    }
}