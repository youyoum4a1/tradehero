package com.androidth.general.api.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.androidth.general.R;
import com.androidth.general.utils.metrics.AnalyticsConstants;

public enum MessageType
{
    PRIVATE(1, R.string.na, AnalyticsConstants.PrivateMessage),
    @Deprecated BROADCAST_FREE_FOLLOWERS(2, R.string.follower_type_free, AnalyticsConstants.BroadcastFreeFollowers),
    @Deprecated BROADCAST_PAID_FOLLOWERS(3, R.string.follower_type_premium, AnalyticsConstants.BroadcastPremiumFollowers),
    BROADCAST_ALL_FOLLOWERS(4, R.string.follower_type_all, AnalyticsConstants.BroadcastAllFollowers),;

    public final int typeId;
    @Deprecated @StringRes public final int titleResource;
    public final String localyticsResource;

    //<editor-fold desc="Constructors">
    private MessageType(int typeId, @StringRes int titleResource, String localyticsResource)
    {
        this.typeId = typeId;
        this.titleResource = titleResource;
        this.localyticsResource = localyticsResource;
    }
    //</editor-fold>

    @JsonCreator @NonNull public static MessageType fromId(int id)
    {
        MessageType[] arr = MessageType.values();
        for (MessageType type : arr)
        {
            if (type.typeId == id)
            {
                return type;
            }
        }
        throw new IllegalArgumentException("Unrecognised id " + id);
    }

    @NonNull public static MessageType[] getBroadcastTypes()
    {
        return new MessageType[] {
                BROADCAST_PAID_FOLLOWERS,
                BROADCAST_FREE_FOLLOWERS,
                BROADCAST_ALL_FOLLOWERS
        };
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonValue
    final int value()
    {
        return typeId;
    }

}