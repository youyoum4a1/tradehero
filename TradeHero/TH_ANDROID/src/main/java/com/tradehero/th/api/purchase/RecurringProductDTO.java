package com.tradehero.th.api.purchase;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:31 PM To change this template use File | Settings | File Templates. */
public class RecurringProductDTO
{
    public static final String TAG = RecurringProductDTO.class.getSimpleName();

    public String appleProductId;

    public int thcc_perMonthValue;        // the # of Content Credits that this product buys the user per month
    // 1 CC == 1 follow of lowest unit-cost trade feed

    public int billingPeriodMonths = 1;   // all 1M recurring products for now -- could use 0 here to indicate one-off (non-recurring) purchases?
    public double usd_customerPrice;     // just USD for now -- table holds all apple ccy's; can return more later (to match user locale/def reporting ccy)
    public double perc_discountFromBase;  // client rounds down to nearest 1% and displays the discount level

    public RecurringProductDTO()
    {
        super();
    }
}
