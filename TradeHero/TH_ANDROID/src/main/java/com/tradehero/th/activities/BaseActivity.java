package com.tradehero.th.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.base.THApp;
import com.tradehero.th.inject.Injector;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import rx.functions.Action1;
import timber.log.Timber;

public class BaseActivity extends ActionBarActivity
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

    private WeakReference<Toolbar> toolbarRef;

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

    @NonNull protected List<Object> getModules()
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

    @Override public void setSupportActionBar(@Nullable Toolbar toolbar)
    {
        toolbarRef = new WeakReference<Toolbar>(toolbar);
        super.setSupportActionBar(toolbar);
    }

    public @Nullable Toolbar getToolbar()
    {
        Toolbar toolbar = toolbarRef.get();
        return toolbar;
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

    protected void showUpgradeDialog()
    {
        AlertDialogRxUtil.popUpgradeRequired(this)
                .subscribe(
                        new Action1<OnDialogClickEvent>()
                        {
                            @Override public void call(OnDialogClickEvent event)
                            {
                                if (event.isPositive())
                                {
                                    THToast.show(R.string.update_guide);
                                    marketUtil.get().showAppOnMarket(BaseActivity.this);
                                    finish();
                                }
                            }
                        },
                        new EmptyAction1<Throwable>());
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
