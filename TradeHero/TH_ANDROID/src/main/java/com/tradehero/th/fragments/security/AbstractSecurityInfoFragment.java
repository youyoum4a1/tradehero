package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import rx.internal.util.SubscriptionList;

abstract public class AbstractSecurityInfoFragment
        extends Fragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = AbstractSecurityInfoFragment.class.getName() + "securityId";

    protected SecurityId securityId;
    @Nullable protected SecurityCompactDTO securityCompactDTO;
    protected SubscriptionList onDestroyViewSubscriptions;

    //<editor-fold desc="Arguments Passing">
    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    @NonNull private static SecurityId getSecurityId(@NonNull Bundle args)
    {
        return new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID));
    }
    //</editor-fold>

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.securityId = getSecurityId(getArguments());
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        onDestroyViewSubscriptions = new SubscriptionList();
    }

    @Override public void onDestroyView()
    {
        onDestroyViewSubscriptions.unsubscribe();
        super.onDestroyView();
    }
}
