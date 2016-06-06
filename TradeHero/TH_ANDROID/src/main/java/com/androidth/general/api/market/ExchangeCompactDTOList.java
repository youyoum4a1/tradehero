package com.androidth.general.api.market;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExchangeCompactDTOList extends BaseExchangeCompactDTOList<ExchangeCompactDTO> implements DTO
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Needed for deserialisation
    public ExchangeCompactDTOList()
    {
        super();
    }

    public ExchangeCompactDTOList(Collection<? extends ExchangeCompactDTO> collection)
    {
        super(collection);
    }
    //</editor-fold>

    @NonNull public List<ExchangeIntegerId> getExchangeIds()
    {
        List<ExchangeIntegerId> list = new ArrayList<>();
        for (ExchangeCompactDTO compactDTO : this)
        {
            list.add(compactDTO.getExchangeIntegerId());
        }
        return list;
    }
}
