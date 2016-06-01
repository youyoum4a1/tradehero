package com.ayondo.academy.network.retrofit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import com.ayondo.academy.persistence.prefs.LanguageCode;
import com.ayondo.academy.utils.Constants;
import com.ayondo.academy.utils.VersionUtils;
import javax.inject.Inject;
import retrofit.RequestInterceptor;

import static com.ayondo.academy.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.ayondo.academy.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

public class RequestHeaders implements RequestInterceptor
{
    private final AccountManager accountManager;
    private final String version;
    private final String languageCode;

    //<editor-fold desc="Constructors">
    @Inject public RequestHeaders(
            Context context,
            @LanguageCode String languageCode)
    {
        this.accountManager = AccountManager.get(context);
        this.version = VersionUtils.getVersionId(context);
        this.languageCode = languageCode;
    }
    //</editor-fold>

    @Override
    public void intercept(RequestFacade request)
    {
        buildAuthorizationHeader(request);
        request.addHeader(Constants.TH_CLIENT_VERSION, version);
        request.addHeader(Constants.TH_LANGUAGE_CODE, languageCode);
        // OkHttp will apparently add "Accept-Encoding: gzip" itself
        request.addHeader(Constants.TH_CLIENT_TYPE, String.valueOf(Constants.DEVICE_TYPE.getServerValue()));
    }

    private void buildAuthorizationHeader(RequestFacade request)
    {
        Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        if (accounts.length != 0)
        {
            String token = accountManager.peekAuthToken(accounts[0], PARAM_AUTHTOKEN_TYPE);
            if (token != null)
            {
                request.addHeader(Constants.AUTHORIZATION, token);
            }
        }
    }
}