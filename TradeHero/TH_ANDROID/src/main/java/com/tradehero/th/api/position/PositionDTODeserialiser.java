package com.ayondo.academy.api.position;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ayondo.academy.api.UniqueFieldDTODeserialiser;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class PositionDTODeserialiser extends UniqueFieldDTODeserialiser<PositionDTO>
{
    //<editor-fold desc="Constructors">
    @Inject public PositionDTODeserialiser(@NonNull ObjectMapper objectMapper)
    {
        // We provide a new ObjectMapper to avoid infinite loop.
        super(objectMapper, createUniqueAttributes(), PositionDTO.class);
    }
    //</editor-fold>

    @NonNull private static Map<String, Class<? extends PositionDTO>> createUniqueAttributes()
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
