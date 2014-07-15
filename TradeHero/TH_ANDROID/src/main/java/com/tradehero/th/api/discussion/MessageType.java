package com.tradehero.th.api.discussion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;

public enum MessageType
{
    PRIVATE(1, LocalyticsConstants.PrivateMessage),
    BROADCAST_FREE_FOLLOWERS(2, R.string.follower_type_free, LocalyticsConstants.BroadcastFreeFollowers),
    BROADCAST_PAID_FOLLOWERS(3,R.string.follower_type_premium, LocalyticsConstants.BroadcastPremiumFollowers),
    BROADCAST_ALL_FOLLOWERS(4,R.string.follower_type_all, LocalyticsConstants.BroadcastAllFollowers);

    public final int typeId;
    public final int titleResource;
    public final String localyticsResource;

    private MessageType(int typeId, String localyticsResource)
    {
        this.typeId = typeId;
        this.titleResource = 0;
        this.localyticsResource = localyticsResource;
    }

    private MessageType(int typeId,int titleResource, String localyticsResource)
    {
        this.typeId = typeId;
        this.titleResource = titleResource;
        this.localyticsResource = localyticsResource;
    }

    @JsonCreator public static MessageType fromId(int id)
    {
        MessageType[] arr = MessageType.values();
        for (MessageType type : arr)
        {
            if (type.typeId == id)
            {
                return type;
            }
        }
        return null;
    }

    public static MessageType[] getShowingTypes()
    {
        MessageType[] r = new MessageType[3];
        r[0] = BROADCAST_PAID_FOLLOWERS;
        r[1] = BROADCAST_FREE_FOLLOWERS;
        r[2] = BROADCAST_ALL_FOLLOWERS;
        return r;
    }

    @Override public String toString()
    {
        switch (this)
        {
            case BROADCAST_PAID_FOLLOWERS:
                return "Premium";
            case BROADCAST_FREE_FOLLOWERS:
                return "Free";
            case BROADCAST_ALL_FOLLOWERS:
                return "All";
        }
        return null;
    }

    @JsonValue
    final int value()
    {
        return typeId;
    }

}