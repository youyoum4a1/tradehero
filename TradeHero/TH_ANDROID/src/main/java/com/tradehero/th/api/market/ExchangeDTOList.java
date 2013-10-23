package com.tradehero.th.api.market;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

/**
 *  Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:44 PM To change this template use File | Settings | File Templates.
 *
 *  This one is an exception to the DTOKeyIdList rule. It appears that this Exchange DTO is used only in a single list.
 *  */
public class ExchangeDTOList extends ArrayList<ExchangeDTO> implements DTO
{
    public static final String TAG = ExchangeDTOList.class.getSimpleName();

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
