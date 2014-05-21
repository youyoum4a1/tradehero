package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

public class OwnedPortfolioIdList extends DTOKeyIdList<OwnedPortfolioId>
{
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

    //@Override public String toString()
    //{
    //    StringBuilder builder = new StringBuilder();
    //    builder.append("[");
    //    String separator = "";
    //    for (OwnedPortfolioId ownedPortfolioId : this)
    //    {
    //        builder.append(separator);
    //        builder.append(ownedPortfolioId);
    //        separator = ", ";
    //    }
    //    builder.append("]");
    //    return builder.toString();
    //}
}
