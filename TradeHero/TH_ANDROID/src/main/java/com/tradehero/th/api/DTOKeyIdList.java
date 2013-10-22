package com.tradehero.th.api;

import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderListKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 6:55 PM To change this template use File | Settings | File Templates. */
public class DTOKeyIdList<CacheDTOKeyType extends DTOKey, ListedDTOKeyType extends DTOKey>
        extends ArrayList<ListedDTOKeyType>
        implements DTO
{
    public static final String TAG = DTOKeyIdList.class.getSimpleName();

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
