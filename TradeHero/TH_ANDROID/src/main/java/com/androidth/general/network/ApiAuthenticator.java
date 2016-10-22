package com.androidth.general.network;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.androidth.general.utils.Constants;
import java.io.IOException;
import java.net.Proxy;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

import static com.androidth.general.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.androidth.general.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

@Singleton
public class ApiAuthenticator implements Authenticator
{
    private final AccountManager accountManager;

    @Inject ApiAuthenticator(Context context)
    {
        this.accountManager = AccountManager.get(context);
    }

    @Override public Request authenticate(Proxy proxy, Response response) throws IOException
    {
        Timber.d("Not authenticated, need re-authentication");

        Account[] accounts;
        try{
            accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        }catch (SecurityException e){
            e.printStackTrace();
            accounts = null;
        }

        if (accounts != null && accounts.length != 0) {
            Account account = accounts[0];
            String oldToken = accountManager.peekAuthToken(account, PARAM_AUTHTOKEN_TYPE);

            // TODO if there is an authentication token, try to extend the token

            // if token cannot be extended, invalidate the token
            if (oldToken != null)
            {
                accountManager.invalidateAuthToken(PARAM_ACCOUNT_TYPE, oldToken);
            }

            // after that, request credential from AccountManager, which activate the addAccount method and consequentially will halt the current
            try
            {
                String token = accountManager.blockingGetAuthToken(account, PARAM_AUTHTOKEN_TYPE, false);
                if (token == null)
                {
                    accountManager.removeAccount(account, null, null);
                }
                else
                {
                    return response.request().newBuilder()
                            .header(Constants.AUTHORIZATION, token)
                            .build();
                }
            }
            catch (OperationCanceledException e)
            {
                Timber.e(e, "Get authToken operation cancelled");
            }
            catch (AuthenticatorException e)
            {
                Timber.e(e, "Authenticator exception!");
            }
        }
        return null;
    }

    @Override public Request authenticateProxy(Proxy proxy, Response response) throws IOException
    {
        return null;
    }
}
