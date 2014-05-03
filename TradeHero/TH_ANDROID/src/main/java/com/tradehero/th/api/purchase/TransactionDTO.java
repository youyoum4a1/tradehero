package com.tradehero.th.api.purchase;


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
