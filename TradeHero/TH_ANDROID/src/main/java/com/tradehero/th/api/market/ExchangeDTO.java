package com.tradehero.th.api.market;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 5:54 PM To change this template use File | Settings | File Templates. */
public class ExchangeDTO
{
    public int id;
    public String name;

    public double sumMarketCap;
    public List<SectorDTO> sectors;

    public String desc;
    public boolean isInternal;
    public boolean isIncludedInTrending;
}
