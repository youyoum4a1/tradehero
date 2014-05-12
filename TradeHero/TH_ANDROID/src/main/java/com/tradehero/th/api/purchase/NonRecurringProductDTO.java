package com.tradehero.th.api.purchase;


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
