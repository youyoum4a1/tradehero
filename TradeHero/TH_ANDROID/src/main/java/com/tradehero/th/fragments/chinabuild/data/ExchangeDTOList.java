package com.tradehero.th.fragments.chinabuild.data;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.io.Serializable;

public class ExchangeDTOList extends BaseArrayList<ExchangeDTO>
    implements DTO , Serializable
{
    private static final long serialVersionUID = 1L;
    //<editor-fold desc="Constructors">
    public ExchangeDTOList()
    {
        super();
    }
    //</editor-fold>
}
