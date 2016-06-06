package com.androidth.general.common.persistence;

import android.support.annotation.NonNull;
import java.util.Calendar;
import java.util.Date;

public class BaseHasExpiration implements HasExpiration
{
    @NonNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public BaseHasExpiration(int seconds)
    {
        super();
        setExpirationDateSecondsInFuture(seconds);
    }

    public BaseHasExpiration(@NonNull Date expirationDate)
    {
        this.expirationDate = expirationDate;
    }
    //</editor-fold>

    protected void setExpirationDateSecondsInFuture(int seconds)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        this.expirationDate = calendar.getTime();
    }

    @Override public long getExpiresInSeconds()
    {
        return Math.max(
                0,
                expirationDate.getTime() - Calendar.getInstance().getTime().getTime());
    }
}
