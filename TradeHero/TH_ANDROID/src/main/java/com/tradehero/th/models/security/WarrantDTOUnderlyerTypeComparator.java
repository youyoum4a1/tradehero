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
 *
 * Created by xavier on 1/23/14.
 */
@Singleton public class WarrantDTOUnderlyerTypeComparator implements Comparator<WarrantDTO>
{
    public static final String TAG = WarrantDTOUnderlyerTypeComparator.class.getSimpleName();

    @Inject WarrantDTOUnderlyerComparator underlyerComparator;
    @Inject WarrantDTOTypeComparator typeComparator;

    @Inject public WarrantDTOUnderlyerTypeComparator()
    {
    }

    @Override public int compare(WarrantDTO lhs, WarrantDTO rhs)
    {
        int underlyerComparison = underlyerComparator.compare(lhs, rhs);

        return underlyerComparison != 0 ? underlyerComparison : typeComparator.compare(lhs, rhs);
    }
}
