package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.api.security.SecurityId;
import rx.Subscription;

abstract public class AbstractSecurityInfoFragment<InfoType extends DTO>
        extends Fragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = AbstractSecurityInfoFragment.class.getName() + ".securityId";

    protected SecurityId securityId;
    protected InfoType value;

    public static void putSecurityId(Bundle args, SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    public static SecurityId getSecurityId(Bundle args)
    {
        SecurityId extracted = null;
        if (args != null)
        {
            extracted = new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID));
        }
        return extracted;
    }

    @Override public void onResume()
    {
        super.onResume();
        linkWith(getSecurityId(getArguments()), true);
    }

    abstract protected DTOCacheRx<SecurityId, InfoType> getInfoCache();

    protected void detachSubscription(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    /**
     * Called in onResume.
     * @param securityId
     * @param andDisplay
     */
    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
    }

    public void linkWith(InfoType value, boolean andDisplay)
    {
        this.value = value;
        if (andDisplay)
        {
            display();
        }
    }

    abstract public void display();
}
