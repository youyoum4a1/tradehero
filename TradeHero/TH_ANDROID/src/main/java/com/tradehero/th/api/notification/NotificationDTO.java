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
    public Integer replyableTypeId;
    public int pushId;

    public String referencedUserName;

    // "legacy" fields to replace custom payloads in iOS pushes
    public Integer providerId;
    public Integer pushTypeId;
    public Integer relatesToCompetitionId;

    public boolean useSysIcon;
    public boolean unread;

    @Override public NotificationKey getDTOKey()
    {
        return new NotificationKey(pushId);
    }

}
