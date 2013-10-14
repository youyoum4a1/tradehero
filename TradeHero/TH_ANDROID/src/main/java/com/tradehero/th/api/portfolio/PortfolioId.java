package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 2:38 PM To change this template use File | Settings | File Templates. */
public class PortfolioId implements DTOKey<Integer>
{
    public final Integer id;

    public PortfolioId(Integer id)
    {
        super();
        this.id = id;
    }

    @Override public Integer makeKey()
    {
        return id;
    }
}
