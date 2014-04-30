package com.tradehero.th.models.security;

import com.tradehero.th.api.security.WarrantDTO;
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
@Singleton public class WarrantDTOUnderlyerTypeComparator implements Comparator<WarrantDTO>
{
    private final WarrantDTOUnderlyerComparator underlyerComparator;
    private final WarrantDTOTypeComparator typeComparator;

    @Inject public WarrantDTOUnderlyerTypeComparator(WarrantDTOUnderlyerComparator underlyerComparator, WarrantDTOTypeComparator typeComparator)
    {
        this.underlyerComparator = underlyerComparator;
        this.typeComparator = typeComparator;
    }

    @Override public int compare(WarrantDTO lhs, WarrantDTO rhs)
    {
        int underlyerComparison = underlyerComparator.compare(lhs, rhs);

        return underlyerComparison != 0 ? underlyerComparison : typeComparator.compare(lhs, rhs);
    }
}
