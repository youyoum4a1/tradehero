package com.tradehero.th.api.yahoo;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.DTOKeyIdList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderListKey;
import com.tradehero.th.api.security.SecurityId;
import java.util.ArrayList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:27 PM To change this template use File | Settings | File Templates. */
public class NewsList extends ArrayList<News> implements DTO
{
    public static final String TAG = NewsList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public NewsList()
    {
        super();
    }

    public NewsList(int capacity)
    {
        super(capacity);
    }

    public NewsList(Collection<? extends News> collection)
    {
        super(collection);
    }
    //</editor-fold>

}
