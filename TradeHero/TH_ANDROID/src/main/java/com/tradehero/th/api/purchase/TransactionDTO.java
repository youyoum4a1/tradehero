package com.tradehero.th.api.purchase;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:47 PM To change this template use File | Settings | File Templates. */
public class TransactionDTO
{
    public static final String TAG = TransactionDTO.class.getSimpleName();

    public int id;
    public String createdAtUtc;
    public double balance;
    public double value;
    public String comment;

    public TransactionDTO()
    {
        super();
    }
}
