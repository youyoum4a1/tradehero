package com.tradehero.th.fragments.security;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 7:04 PM To change this template use File | Settings | File Templates. */
abstract public class AbstractSecurityInfoFragment<InfoType extends DTO>
        extends SherlockFragment
        implements DTOCache.Listener<SecurityId, InfoType>
{
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = AbstractSecurityInfoFragment.class.getName() + ".securityId";

    protected SecurityId securityId;
    protected InfoType value;

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new SecurityId(getArguments().getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE)), true);
        }
    }

    abstract DTOCache<SecurityId, InfoType> getInfoCache();

    /**
     * Called in onResume.
     * @param securityId
     * @param andDisplay
     */
    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
    }

    @Override public void onDTOReceived(SecurityId key, InfoType value, boolean fromCache)
    {
        if (key.equals(securityId))
        {
            linkWith(value, !isDetached());
        }
    }

    @Override public void onErrorThrown(SecurityId key, Throwable error)
    {
        THToast.show(getString(R.string.error_fetch_security_info));
    }

    public void linkWith(InfoType value, boolean andDisplay)
    {
        this.value = value;
        if (andDisplay)
        {
            display();
        }
    }

    abstract void display();
}
