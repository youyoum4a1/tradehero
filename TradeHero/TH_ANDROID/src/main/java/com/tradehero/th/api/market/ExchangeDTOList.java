package com.tradehero.th.api.market;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

public class ExchangeDTOList extends ArrayList<ExchangeDTO> implements DTO
{
    //<editor-fold desc="Constructors">
    public ExchangeDTOList()
    {
        super();
    }

    public ExchangeDTOList(int capacity)
    {
        super(capacity);
    }

    public ExchangeDTOList(Collection<? extends ExchangeDTO> collection)
    {
        super(collection);
    }
    //</editor-fold>


}
