package com.ayondo.academy.models.security;

import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.compact.WarrantDTO;
import java.util.Comparator;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * When in a TreeSet, places in order:
 * underlyingName A type C
 * underlyingName A type P
 * underlyingName A type null
 * underlyingName Z type C
 * underlyingName Z type P
 * underlyingName Z type null
 * underlyingName null type C
 * underlyingName null type P
 * underlyingName null type null
 * null
 */
@Singleton public class WarrantDTOUnderlyerTypeComparator implements Comparator<SecurityCompactDTO>
{
    private final WarrantDTOUnderlyerComparator underlyerComparator;
    private final WarrantDTOTypeComparator typeComparator;

    @Inject public WarrantDTOUnderlyerTypeComparator(WarrantDTOUnderlyerComparator underlyerComparator, WarrantDTOTypeComparator typeComparator)
    {
        this.underlyerComparator = underlyerComparator;
        this.typeComparator = typeComparator;
    }

    @Override public int compare(SecurityCompactDTO lhs, SecurityCompactDTO rhs)
    {
        if (!(lhs instanceof WarrantDTO) && rhs instanceof WarrantDTO)
        {
            return 1;
        }
        if (!(rhs instanceof WarrantDTO) && lhs instanceof WarrantDTO)
        {
            return -1;
        }
        if (!(rhs instanceof WarrantDTO))
        {
            return 0;
        }
        int underlyerComparison = underlyerComparator.compare((WarrantDTO) lhs, (WarrantDTO) rhs);

        return underlyerComparison != 0 ? underlyerComparison : typeComparator.compare((WarrantDTO) lhs, (WarrantDTO) rhs);
    }
}
