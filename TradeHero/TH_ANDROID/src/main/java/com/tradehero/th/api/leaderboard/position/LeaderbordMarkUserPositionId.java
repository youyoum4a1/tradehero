package com.tradehero.th.api.leaderboard.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/**
 * Created by julien on 1/11/13
 */
public class LeaderbordMarkUserPositionId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = LeaderbordMarkUserPositionId.class.getName() + ".key";

    public LeaderbordMarkUserPositionId(Integer key)
    {
        super(key);
    }

    public LeaderbordMarkUserPositionId(Bundle args)
    {
        super(args);
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof LeaderbordMarkUserPositionId) && equals((LeaderbordMarkUserPositionId) other);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
