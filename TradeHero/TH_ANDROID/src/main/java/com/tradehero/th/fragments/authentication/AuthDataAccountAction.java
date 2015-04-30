package com.tradehero.th.fragments.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.utils.StringUtils;
import javax.inject.Inject;
import rx.functions.Action1;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.tradehero.th.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

public class AuthDataAccountAction implements Action1<Pair<AuthData, UserProfileDTO>>
{
    @NonNull private final AccountManager accountManager;

    //<editor-fold desc="Constructors">
    @Inject public AuthDataAccountAction(@NonNull Context context)
    {
        this.accountManager = AccountManager.get(context);
    }
    //</editor-fold>

    @Override public void call(@NonNull Pair<AuthData, UserProfileDTO> pair)
    {
        Account account = getOrAddAccount(pair);
        AuthData authData = pair.first;
        if (authData.socialNetworkEnum != SocialNetworkEnum.TH || !StringUtils.isNullOrEmpty(authData.password))
        {
            accountManager.setAuthToken(account, PARAM_AUTHTOKEN_TYPE, pair.first.getTHToken());
        }
    }

    @NonNull private Account getOrAddAccount(@NonNull Pair<AuthData, UserProfileDTO> pair)
    {
        Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        Account account = accounts.length != 0 ? accounts[0] :
                new Account(pair.second.email, PARAM_ACCOUNT_TYPE);

        String password = pair.first.password;
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