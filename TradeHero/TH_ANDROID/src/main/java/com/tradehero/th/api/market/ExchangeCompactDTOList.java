package com.tradehero.th.api.market;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

public class ExchangeCompactDTOList extends ArrayList<ExchangeCompactDTO> implements DTO
{
    //<editor-fold desc="Constructors">
    public ExchangeCompactDTOList()
    {
        super();
    }

    public ExchangeCompactDTOList(int capacity)
    {
        super(capacity);
    }

    public ExchangeCompactDTOList(Collection<? extends ExchangeCompactDTO> collection)
    {
        super(collection);
    }
    //</editor-fold>

}
