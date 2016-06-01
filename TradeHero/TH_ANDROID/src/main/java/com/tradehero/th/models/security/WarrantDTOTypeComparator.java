package com.ayondo.academy.models.security;

import com.ayondo.academy.api.security.compact.WarrantDTO;
import java.io.Serializable;
import java.util.Comparator;
import javax.inject.Singleton;

/**
 * When in a TreeSet, places in order:
 * type C
 * type P
 * null
 */
@Singleton public class WarrantDTOTypeComparator implements Comparator<WarrantDTO>, Serializable
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
        if (lhs.warrantType == null)
        {
            return rhs.warrantType == null ? 0 : 1;
        }
        if (rhs.warrantType == null)
        {
            return -1;
        }

        return lhs.warrantType.compareTo(rhs.warrantType);
    }
}
