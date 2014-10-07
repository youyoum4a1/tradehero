package com.tradehero.th.fragments.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.AuthData;
import javax.inject.Inject;
import rx.functions.Action1;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.tradehero.th.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

public class AuthDataAction implements Action1<Pair<AuthData, UserLoginDTO>>
{
    private final Activity activity;
    private final AccountManager accountManager;

    @Inject public AuthDataAction(Activity activity, AccountManager accountManager)
    {
        this.activity = activity;
        this.accountManager = accountManager;
    }

    @Override public void call(Pair<AuthData, UserLoginDTO> authDataUserLoginDTOPair)
    {
        Account account = getOrAddAccount(authDataUserLoginDTOPair);
        accountManager.setAuthToken(account, PARAM_AUTHTOKEN_TYPE, authDataUserLoginDTOPair.first.getTHToken());
        finishAuthentication(authDataUserLoginDTOPair);
    }

    private Account getOrAddAccount(Pair<AuthData, UserLoginDTO> authDataUserLoginDTOPair)
    {
        Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        Account account = accounts.length != 0 ? accounts[0] :
                new Account(authDataUserLoginDTOPair.second.profileDTO.email, PARAM_ACCOUNT_TYPE);

        String password = authDataUserLoginDTOPair.first.password;
        if (accounts.length == 0)
        {
            accountManager.addAccountExplicitly(account, password, null);
        }
        else
        {
            accountManager.setPassword(accounts[0], password);
        }
        return account;
    }

    private void finishAuthentication(Pair<AuthData, UserLoginDTO> authDataUserLoginDTOPair)
    {
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, authDataUserLoginDTOPair.second.profileDTO.email);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authDataUserLoginDTOPair.first.getTHToken());
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);

        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
        activity.startActivity(new Intent(activity, DashboardActivity.class));
    }
}