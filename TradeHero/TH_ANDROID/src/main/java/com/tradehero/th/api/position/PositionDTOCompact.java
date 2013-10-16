package com.tradehero.th.api.position;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:27 PM To change this template use File | Settings | File Templates. */
public class PositionDTOCompact
{
    public int id;
    public Integer shares;
    public int portfolioId;

    // This price is always is USD
    public Double averagePriceRefCcy;

    public PositionCompactId getPositionCompactId()
    {
        return new PositionCompactId(id);
    }
}
