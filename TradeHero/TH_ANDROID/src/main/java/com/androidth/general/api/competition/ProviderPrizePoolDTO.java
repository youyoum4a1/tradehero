package com.androidth.general.api.competition;

public class ProviderPrizePoolDTO
{
    public String current;
    public String extra;
    public int newPlayerNeeded;
    public String background;

    @Override
    public String toString()
    {
        return "ProviderPrizePoolDTO{" +
                "current='" + current + '\'' +
                ", extra='" + extra + '\'' +
                ", newPlayerNeeded='" + newPlayerNeeded + '\'' +
                ", background='" + background + '\'' +
                '}';
    }
}
