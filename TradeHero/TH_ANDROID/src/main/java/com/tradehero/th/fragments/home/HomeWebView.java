package com.tradehero.th.fragments.home;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.tradehero.common.widget.NotifyingWebView;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.utils.Constants;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.tradehero.th.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

public final class HomeWebView extends NotifyingWebView
{
    @Inject CurrentUserId currentUserId;
    @NonNull final AccountManager accountManager;

    //region Constructors
    public HomeWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        accountManager = AccountManager.get(context);
    }
    //endregion

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        reload();
    }

    @Override public void reload()
    {
        String appHomeLink = String.format("%s/%d", Constants.APP_HOME, currentUserId.get());

        Map<String, String> headers = new HashMap<>();
        Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        if (accounts.length != 0)
        {
            String token = accountManager.peekAuthToken(accounts[0], PARAM_AUTHTOKEN_TYPE);
            if (token != null)
            {
                headers.put(Constants.AUTHORIZATION, token);
            }
        }
        loadUrl(appHomeLink, headers);
    }
}
