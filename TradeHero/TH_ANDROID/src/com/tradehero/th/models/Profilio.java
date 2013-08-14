package com.tradehero.th.models;

public class Profilio
{

    // portfolio
    private String totalExtraCashPurchased;
    private String roiQ2DAnnualized;
    private String roiY2D;
    private String id;
    private String title;
    private String roiSinceInception;
    private String initialCash;
    private String roiY2DAnnualized;
    private String description;
    private String plQ2D;
    private String Currency;
    private String plSinceInception;
    private String totalExtraCashGiven;
    private String countTrades;
    private String roiSinceInceptionAnnualized;
    private String markingAsOfUtc;
    private String countExchanges;
    private String roiM2DAnnualized;
    private String roiM2D;
    private String totalValue;
    private String plM2D;
    private String creationDate;
    private String plY2D;
    private String yahooSymbols;//class
    private int cashBalance;
    private String roiQ2D;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getCashBalance()
    {
        return cashBalance;
    }

    public void setCashBalance(int cashBalance)
    {
        this.cashBalance = cashBalance;
    }
}
