package com.tradehero.th.models.security;

import com.tradehero.th.api.security.WarrantDTO;
import java.util.Comparator;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * When in a TreeSet, places in order:
 * underlyingName A
 * underlyingName Z
 * underlyingName null
 * null
 *
 * Created by xavier on 1/23/14.
 */
@Singleton public class WarrantDTOUnderlyerComparator implements Comparator<WarrantDTO>
{
    public static final String TAG = WarrantDTOUnderlyerComparator.class.getSimpleName();

    @Inject public WarrantDTOUnderlyerComparator()
    {
    }

    @Override public int compare(WarrantDTO lhs, WarrantDTO rhs)
    {
        if (lhs == null)
        {
            return rhs == null ? 0 : 1;
        }
        if (rhs == null)
        {
            return -1;
        }
        if (lhs.underlyingName == null)
        {
            return rhs.underlyingName == null ? 0 : 1;
        }
        if (rhs.underlyingName == null)
        {
            return -1;
        }

        return lhs.underlyingName.compareTo(rhs.underlyingName);
    }
}
