package com.tradehero.th.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.tradehero.th.utils.Constants;

public class BaseActivity extends FragmentActivity
        implements OnAccountsUpdateListener
{
    private AccountManager accountManager;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        accountManager = AccountManager.get(this);
    }

    @Override protected void onResume()
    {
        super.onResume();

        if (requireLogin())
        {
            accountManager.addOnAccountsUpdatedListener(this, null, true);
        }
    }

    @Override protected void onPause()
    {
        super.onPause();

        if (requireLogin())
        {
            accountManager.removeOnAccountsUpdatedListener(this);
        }
    }

    protected boolean requireLogin()
    {
        return true;
    }

    @Override public void onAccountsUpdated(Account[] accounts)
    {
        for (Account account: accounts)
        {
            if (Constants.Auth.PARAM_ACCOUNT_TYPE.equals(account.type))
            {
                return;
            }
        }

        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }
}
