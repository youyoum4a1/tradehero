package com.tradehero.th.api.social;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:27 PM To change this template use File | Settings | File Templates. */
public class FollowerTransactionDTO
{
    public static final String TAG = FollowerTransactionDTO.class.getSimpleName();

    public String paidAt;
    public double value;
    public double freeCreditValue;
    public double revenue;

    public FollowerTransactionDTO()
    {
        super();
    }
}
