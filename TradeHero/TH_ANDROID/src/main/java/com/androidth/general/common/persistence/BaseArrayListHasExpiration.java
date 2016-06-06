package com.androidth.general.common.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.api.BaseArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class BaseArrayListHasExpiration<T> extends BaseArrayList<T>
    implements HasExpiration
{
    @NonNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public BaseArrayListHasExpiration(int seconds)
    {
        super();
        setExpirationDateSecondsInFuture(seconds);
    }

    public BaseArrayListHasExpiration(Collection<? extends T> c, int seconds)
    {
        super(c);
        setExpirationDateSecondsInFuture(seconds);
    }

    public BaseArrayListHasExpiration(@NonNull Date expirationDate)
    {
        super();
        this.expirationDate = expirationDate;
    }

    @SuppressWarnings("UnusedDeclaration")
    public BaseArrayListHasExpiration(Collection<? extends T> c, @NonNull Date expirationDate)
    {
        super(c);
        this.expirationDate = expirationDate;
    }
    //</editor-fold>

    @Override public boolean equals(@Nullable Object o)
    {
        return super.equals(o)
                && o instanceof BaseArrayListHasExpiration
                && expirationDate.equals(((BaseArrayListHasExpiration<?>) o).expirationDate);
    }

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
