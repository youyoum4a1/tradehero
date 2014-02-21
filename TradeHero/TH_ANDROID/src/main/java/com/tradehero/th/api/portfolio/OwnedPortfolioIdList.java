package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:46 PM To change this template use File | Settings | File Templates. */
public class OwnedPortfolioIdList extends DTOKeyIdList<OwnedPortfolioId>
{
    public static final String TAG = OwnedPortfolioIdList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public OwnedPortfolioIdList()
    {
        super();
    }

    public OwnedPortfolioIdList(int capacity)
    {
        super(capacity);
    }

    public OwnedPortfolioIdList(Collection<? extends OwnedPortfolioId> collection)
    {
        super(collection);
    }
    //</editor-fold>

}
