package com.tradehero.th.fragments.chinabuild.cache;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.security.SecurityId;

public class PositionDTOKey extends AbstractIntegerDTOKey
{
    public final static String BUNDLE_KEY_KEY = PositionDTOKey.class.getName() + ".key";
    public final static String DEFAULT_KEY = "All";

    public int competitionId;
    public SecurityId securityId;

    public PositionDTOKey(Bundle args)
    {
        super(args);
    }

    public PositionDTOKey(Integer competitionId,SecurityId securityId)
    {
        super(competitionId);
        this.competitionId = competitionId;
        this.securityId = securityId;
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

}
