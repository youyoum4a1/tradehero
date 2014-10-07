package com.tradehero.th.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.tradehero.th.UIModule;
import com.tradehero.th.base.THApp;
import com.tradehero.th.inject.Injector;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.List;

public class BaseActivity extends FragmentActivity
        implements OnAccountsUpdateListener, Injector
{
    private AccountManager accountManager;
    private Injector newInjector;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        THApp thApp = THApp.get(this);
        newInjector = thApp.plus(getModules().toArray());
        newInjector.inject(this);

        accountManager = AccountManager.get(this);
    }

    protected List<Object> getModules()
    {
        return Arrays.<Object>asList(new BaseActivityModule());
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
        for (Account account : accounts)
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

    @Override public void inject(Object o)
    {
        if (newInjector != null)
        {
            newInjector.inject(o);
        }
    }

    @Module(
            addsTo = AppModule.class,
            includes = UIModule.class,
            library = true,
            complete = false
    )
    public class BaseActivityModule
    {
        @Provides Activity provideActivity()
        {
            return BaseActivity.this;
        }
    }
}
