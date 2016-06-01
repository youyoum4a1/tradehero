package com.ayondo.academy.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ayondo.academy.activities.AuthenticationActivity;
import com.ayondo.academy.utils.Constants;

public class THAccountAuthenticator extends AbstractAccountAuthenticator
{
    private final Context mContext;

    public THAccountAuthenticator(Context context)
    {
        super(context);
        mContext = context;
    }

    @Override public Bundle editProperties(AccountAuthenticatorResponse response, String accountType)
    {
        return null;
    }

    @Override public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException
    {
        Intent authIntent = new Intent(mContext, AuthenticationActivity.class);
        authIntent.putExtra(Constants.Auth.PARAM_AUTHTOKEN_TYPE, authTokenType);
        authIntent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, authIntent);
        return bundle;
    }

    @Override public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle)
            throws NetworkErrorException
    {
        return null;
    }

    @Override public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String authTokenType,
            Bundle options) throws NetworkErrorException
    {
        String authToken = AccountManager.get(mContext).peekAuthToken(account, authTokenType);

        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
        return bundle;
    }

    @Override public String getAuthTokenLabel(String authTokenType)
    {
        if (Constants.Auth.PARAM_AUTHTOKEN_TYPE.equals(authTokenType)) {
            return authTokenType;
        }
        return null;
    }

    @Override public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String authTokenType,
            Bundle options) throws NetworkErrorException
    {
        return null;
    }

    @Override public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings)
            throws NetworkErrorException
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return bundle;
    }
}
