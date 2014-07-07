package com.tradehero.common.persistence;

import java.util.Calendar;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public class BaseHasExpiration implements HasExpiration
{
    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public BaseHasExpiration(int seconds)
    {
        super();
        setExpirationDateSecondsInFuture(seconds);
    }

    public BaseHasExpiration(@NotNull Date expirationDate)
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
