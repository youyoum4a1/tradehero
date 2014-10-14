package com.tradehero.th.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.base.THApp;
import com.tradehero.th.inject.Injector;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class BaseActivity extends FragmentActivity
        implements OnAccountsUpdateListener, Injector
{
    private AccountManager accountManager;
    private Injector newInjector;

    @Inject protected LocalBroadcastManager localBroadcastManager;
    @Inject @ForUpgrade IntentFilter upgradeIntentFilter;
    BroadcastReceiver upgradeRequiredBroadcastListener;
    @Inject @ForSocialToken IntentFilter socialTokenIntentFilter;
    BroadcastReceiver socialTokenBroadcastListener;
    @Inject Lazy<MarketUtil> marketUtil;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        extendAndInject();

        super.onCreate(savedInstanceState);

        Timber.d("Activity created");

        accountManager = AccountManager.get(this);
        upgradeRequiredBroadcastListener = new UpgradeRequiredListener();
        socialTokenBroadcastListener = new SocialTokenListener();
    }

    private void extendAndInject()
    {
        THApp thApp = THApp.get(this);
        newInjector = thApp.plus(getModules().toArray());
        newInjector.inject(this);
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
        localBroadcastManager.registerReceiver(upgradeRequiredBroadcastListener, upgradeIntentFilter);
        localBroadcastManager.registerReceiver(socialTokenBroadcastListener, socialTokenIntentFilter);
    }

    @Override protected void onPause()
    {
        super.onPause();

        localBroadcastManager.unregisterReceiver(upgradeRequiredBroadcastListener);
        localBroadcastManager.unregisterReceiver(socialTokenBroadcastListener);
        if (requireLogin())
        {
            accountManager.removeOnAccountsUpdatedListener(this);
        }
    }

    @Override protected void onDestroy()
    {
        socialTokenBroadcastListener = null;
        upgradeRequiredBroadcastListener = null;
        super.onDestroy();
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

    protected class UpgradeRequiredListener extends BroadcastReceiver
    {
        @Override public void onReceive(Context context, Intent intent)
        {
            showUpgradeDialog();
        }
    }

    private void showUpgradeDialog()
    {
        alertDialogUtil.get().popWithOkCancelButton(
                this,
                R.string.upgrade_needed,
                R.string.please_update,
                R.string.update_now,
                R.string.later,
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        THToast.show(R.string.update_guide);
                        marketUtil.get().showAppOnMarket(BaseActivity.this);
                        finish();
                    }
                });
    }

    protected class SocialTokenListener extends BroadcastReceiver
    {
        @Override public void onReceive(Context context, Intent intent)
        {
            ActivityHelper.launchAuthentication(BaseActivity.this);
        }
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
