package com.androidth.general.network.retrofit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.androidth.general.persistence.prefs.LanguageCode;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.VersionUtils;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.RequestInterceptor;

import static com.androidth.general.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.androidth.general.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

public class RequestHeaders implements Interceptor
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
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();

        //add authorization header
        try{
            Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
            if (accounts.length != 0)
            {
                String token = accountManager.peekAuthToken(accounts[0], PARAM_AUTHTOKEN_TYPE);
                if (token != null)
                {
                    request.addHeader(Constants.AUTHORIZATION, token);
                }
            }
        }catch (SecurityException e){
            //no user account access
            e.printStackTrace();
        }

        request.addHeader(Constants.TH_CLIENT_VERSION, version);
        request.addHeader(Constants.TH_LANGUAGE_CODE, languageCode);
        // OkHttp will apparently add "Accept-Encoding: gzip" itself
        request.addHeader(Constants.TH_CLIENT_TYPE, String.valueOf(Constants.DEVICE_TYPE.getServerValue()));

        return chain.proceed(request.build());
    }

//    @Override
//    public void intercept(RequestInterceptor.RequestFacade request)
//    {
//        buildAuthorizationHeader(request);
//        request.addHeader(Constants.TH_CLIENT_VERSION, version);
//        request.addHeader(Constants.TH_LANGUAGE_CODE, languageCode);
//        // OkHttp will apparently add "Accept-Encoding: gzip" itself
//        request.addHeader(Constants.TH_CLIENT_TYPE, String.valueOf(Constants.DEVICE_TYPE.getServerValue()));
//    }

    private Request buildAuthorizationHeader(Request request)
    {
        try{
            Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
            if (accounts.length != 0)
            {
                String token = accountManager.peekAuthToken(accounts[0], PARAM_AUTHTOKEN_TYPE);
                if (token != null)
                {
                    request.newBuilder().addHeader(Constants.AUTHORIZATION, token);
                }
            }
            return request;
        }catch (SecurityException e){
            //no user account access
            e.printStackTrace();
            return null;
        }

    }

    public String  headerTokenLive()
    {
        try{
            Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
            if (accounts.length != 0)
            {
                String token = accountManager.peekAuthToken(accounts[0], PARAM_AUTHTOKEN_TYPE);
                if (token != null)
                {
                    return token;
                }
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }

        return null;
    }
}