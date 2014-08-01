package com.tradehero.common.persistence;

import com.tradehero.common.api.BaseArrayList;
import java.util.Collection;

public class DTOKeyIdList<ListedDTOKeyType extends DTOKey>
        extends BaseArrayList<ListedDTOKeyType>
        implements DTO
{
    //<editor-fold desc="Constructors">
    public DTOKeyIdList()
    {
        super();
    }

    public DTOKeyIdList(int capacity)
    {
        super(capacity);
    }

    public DTOKeyIdList(Collection<? extends ListedDTOKeyType> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
