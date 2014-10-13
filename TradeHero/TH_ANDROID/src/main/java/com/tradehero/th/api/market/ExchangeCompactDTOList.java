package com.tradehero.th.api.market;

import com.tradehero.common.persistence.DTO;
import java.util.Collection;

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
}
