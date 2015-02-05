package com.tradehero.th.fragments.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
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
    @NonNull private final Activity activity;
    @NonNull private final AccountManager accountManager;

    //<editor-fold desc="Constructors">
    @Inject public AuthDataAccountAction(@NonNull Activity activity, @NonNull AccountManager accountManager)
    {
        this.activity = activity;
        this.accountManager = accountManager;
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
        finishAuthentication(pair);
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

    private void finishAuthentication(@NonNull Pair<AuthData, UserProfileDTO> pair)
    {
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, pair.second.email);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, pair.first.getTHToken());
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);

        activity.setResult(Activity.RESULT_OK, intent);
    }
}