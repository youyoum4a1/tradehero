package com.ayondo.academy.api.social.key;

import android.support.annotation.NonNull;

public class FacebookFriendKey implements FriendKey
{
    @NonNull public final String fbId;

    //<editor-fold desc="Constructors">
    public FacebookFriendKey(@NonNull String fbId)
    {
        this.fbId = fbId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return fbId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof FacebookFriendKey
                && ((FacebookFriendKey) other).fbId.equals(fbId);
    }
}
