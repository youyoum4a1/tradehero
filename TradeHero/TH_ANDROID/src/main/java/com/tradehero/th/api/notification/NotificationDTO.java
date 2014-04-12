package com.tradehero.th.api.notification;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.KeyGenerator;
import java.util.Date;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationDTO implements DTO, KeyGenerator
{
    public String imageUrl;
    public String text;
    public Date createdAtUtc;
    public Integer referencedUserId;
    public Integer replyableId;
    public Integer replyableTypeId;
    public int pushId;

    // "legacy" fields to replace custom payloads in iOS pushes
    public Integer relatesToHeroUserId;
    public NotificationTradeDTO trade;
    public NotificationStockAlertDTO stockAlert;
    public Integer providerId;
    public Integer pushTypeId;
    public boolean unread;

    @Override public NotificationKey getDTOKey()
    {
        return new NotificationKey(pushId);
    }
}
