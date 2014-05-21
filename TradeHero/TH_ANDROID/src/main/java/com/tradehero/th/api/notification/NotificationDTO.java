package com.tradehero.th.api.notification;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.KeyGenerator;
import java.util.Date;

public class NotificationDTO implements DTO, KeyGenerator
{
    public String imageUrl;
    public String text;
    public Date createdAtUtc;
    public Integer referencedUserId;
    public Integer replyableId;
    public Integer threadId;
    public Integer replyableTypeId;
    public int pushId;
    public String pushGuid;

    // "legacy" fields to replace custom payloads in iOS pushes
    public Integer relatesToHeroUserId;
    public NotificationTradeDTO trade;
    public NotificationStockAlertDTO stockAlert;
    public Integer providerId;
    public Integer pushTypeId;

    public boolean useSysIcon;
    public boolean unread;

    @Override public NotificationKey getDTOKey()
    {
        return new NotificationKey(pushId);
    }

    @Override public String toString()
    {
        return new StringBuilder().append("NotificationDTO text=")
                .append(text)
                .append(" pushId=")
                .append(pushId)
                .append(" replyableId=")
                .append(replyableId)
                .append(" threadId=")
                .append(threadId)
                .append(" referencedUserId=")
                .append(referencedUserId)
                .toString();
    }
}
