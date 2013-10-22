package com.tradehero.th.api.purchase;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:32 PM To change this template use File | Settings | File Templates. */
public class NonRecurringProductDTO
{
    public static final String TAG = NonRecurringProductDTO.class.getSimpleName();

    public String appleProductId;
    public int thcc_Value;
    public double usd_customerPrice;     // just USD for now -- table holds all apple ccy's; can return more later (to match user locale/def reporting ccy)

    public NonRecurringProductDTO()
    {
        super();
    }
}
