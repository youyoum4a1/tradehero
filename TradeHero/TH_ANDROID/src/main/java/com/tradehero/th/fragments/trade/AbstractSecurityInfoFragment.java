package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.utils.DaggerUtils;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 7:04 PM To change this template use File | Settings | File Templates. */
abstract public class AbstractSecurityInfoFragment<InfoType> extends SherlockFragment
        implements DTOCache.Listener<SecurityId, InfoType>
{
    public final static String TAG = AbstractSecurityInfoFragment.class.getSimpleName();

    protected SecurityId securityId;
    protected InfoType value;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new SecurityId(args), true);
        }
        else
        {
            display();
        }
    }

    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
    }

    @Override public void onDTOReceived(SecurityId key, InfoType value)
    {
        if (key.equals(securityId))
        {
            linkWith(value, true);
        }
    }

    @Override public void onErrorThrown(SecurityId key, Throwable error)
    {
        THToast.show(getString(R.string.error_fetch_security_info));
        THLog.e(TAG, "Error fetching the security " + key, error);
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
