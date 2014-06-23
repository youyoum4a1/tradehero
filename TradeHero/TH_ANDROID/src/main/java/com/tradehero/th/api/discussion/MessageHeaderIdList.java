package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public class MessageHeaderIdList extends DTOKeyIdList<MessageHeaderId>
    implements HasExpiration
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 300;

    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public MessageHeaderIdList()
    {
        super();
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    public MessageHeaderIdList(int capacity)
    {
        super(capacity);
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    public MessageHeaderIdList(Collection<? extends MessageHeaderId> collection)
    {
        super(collection);
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
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
