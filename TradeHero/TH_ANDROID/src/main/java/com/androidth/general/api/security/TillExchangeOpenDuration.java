package com.androidth.general.api.security;

public class TillExchangeOpenDuration
{
    public final long createdAtNanoTime;
    public final int days;
    public final int hours;
    public final int minutes;
    public final int seconds;

    //<editor-fold desc="Constructors">
    public TillExchangeOpenDuration(long createdAtNanoTime, int days, int hours, int minutes, int seconds)
    {
        this.createdAtNanoTime = createdAtNanoTime;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }
    //</editor-fold>
}
