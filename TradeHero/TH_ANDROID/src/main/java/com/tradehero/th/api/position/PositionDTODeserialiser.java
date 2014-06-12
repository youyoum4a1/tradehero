package com.tradehero.th.api.position;

import com.tradehero.th.api.UniqueFieldDTODeserialiser;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class PositionDTODeserialiser extends UniqueFieldDTODeserialiser<PositionDTO>
{
    //<editor-fold desc="Constructors">
    @Inject public PositionDTODeserialiser()
    {
        super(createUniqueAttributes(), PositionDTO.class);

    }
    //</editor-fold>

    private static Map<String, Class<? extends PositionDTO>> createUniqueAttributes()
    {
        Map<String, Class<? extends PositionDTO>> uniqueAttributes = new HashMap<>();
        uniqueAttributes.put(PositionInPeriodDTO.TOTAL_PL_IN_PERIOD_REF_CCY, PositionInPeriodDTO.class);
        uniqueAttributes.put(WatchlistPositionDTO.WATCHLIST_PRICE_FIELD, WatchlistPositionDTO.class);
        return uniqueAttributes;
    }

    @Override protected Class<? extends PositionDTO> getDefaultClass()
    {
        return PositionDTO.class;
    }
}
