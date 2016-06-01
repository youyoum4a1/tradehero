package com.ayondo.academy.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.utils.StringUtils;

import static com.ayondo.academy.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.ayondo.academy.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

public class AuthDataUtil
{
    public static void saveAccountAndResult(@NonNull Activity activity, @NonNull AuthData authData, String email)
    {
        saveAccount(activity, authData, email);

        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authData.getTHToken());
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);
        activity.setResult(Activity.RESULT_OK, intent);
    }

    public static void saveAccount(@NonNull Context context, @NonNull AuthData authData, String email)
    {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getOrAddAccount(accountManager, authData, email);
        if (authData.socialNetworkEnum != SocialNetworkEnum.TH || !StringUtils.isNullOrEmpty(authData.password))
        {
            accountManager.setAuthToken(account, PARAM_AUTHTOKEN_TYPE, authData.getTHToken());
        }
    }

    private static Account getOrAddAccount(@NonNull AccountManager accountManager, @NonNull AuthData authData, String email)
    {
        Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        Account account = accounts.length != 0 ? accounts[0] :
                new Account(email, PARAM_ACCOUNT_TYPE);

        String password = authData.password;
        if (accounts.length == 0)
        {
            accountManager.addAccountExplicitly(account, password, null);
        }
        else if (!StringUtils.isNullOrEmpty(password))
        {
            accountManager.setPassword(accounts[0], password);
        }
        return account;
    }
}
