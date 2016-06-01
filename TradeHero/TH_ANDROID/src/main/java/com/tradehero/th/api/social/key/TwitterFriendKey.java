package com.ayondo.academy.api.social.key;

import android.support.annotation.NonNull;

public class TwitterFriendKey implements FriendKey
{
    @NonNull public final String twId;

    //<editor-fold desc="Constructors">
    public TwitterFriendKey(@NonNull String twId)
    {
        this.twId = twId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return twId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof TwitterFriendKey
                && ((TwitterFriendKey) other).twId.equals(twId);
    }
}
