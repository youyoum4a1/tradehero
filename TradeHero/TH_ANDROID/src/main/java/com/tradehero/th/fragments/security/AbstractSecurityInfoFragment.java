package com.tradehero.th.fragments.security;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import org.jetbrains.annotations.NotNull;

abstract public class AbstractSecurityInfoFragment<InfoType extends DTO>
        extends SherlockFragment
        implements DTOCacheNew.Listener<SecurityId, InfoType>
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

    abstract protected DTOCacheNew<SecurityId, InfoType> getInfoCache();

    /**
     * Called in onResume.
     * @param securityId
     * @param andDisplay
     */
    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
    }

    @Override public void onDTOReceived(@NotNull SecurityId key, @NotNull InfoType value)
    {
        if (key.equals(securityId))
        {
            linkWith(value, !isDetached());
        }
    }

    @Override public void onErrorThrown(@NotNull SecurityId key, @NotNull Throwable error)
    {
        THToast.show(R.string.error_fetch_security_info);
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
