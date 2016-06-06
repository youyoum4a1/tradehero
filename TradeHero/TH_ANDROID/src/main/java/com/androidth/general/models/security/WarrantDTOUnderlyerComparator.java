package com.androidth.general.models.security;

import com.androidth.general.api.security.compact.WarrantDTO;
import java.io.Serializable;
import java.util.Comparator;
import javax.inject.Singleton;

/**
 * When in a TreeSet, places in order:
 * underlyingName A
 * underlyingName Z
 * underlyingName null
 * null
 */
@Singleton public class WarrantDTOUnderlyerComparator implements Comparator<WarrantDTO>, Serializable
{
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
