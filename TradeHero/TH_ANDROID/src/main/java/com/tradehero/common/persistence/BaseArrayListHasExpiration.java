package com.tradehero.common.persistence;

import android.support.annotation.NonNull;
import com.tradehero.common.api.BaseArrayList;
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

    public BaseArrayListHasExpiration(Collection<? extends T> c, @NonNull Date expirationDate)
    {
        super(c);
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
