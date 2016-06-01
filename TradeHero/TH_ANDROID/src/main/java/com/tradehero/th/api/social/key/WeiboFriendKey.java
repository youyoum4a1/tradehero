package com.ayondo.academy.api.social.key;

import android.support.annotation.NonNull;

public class WeiboFriendKey implements FriendKey
{
    @NonNull public final String wbId;

    //<editor-fold desc="Constructors">
    public WeiboFriendKey(@NonNull String wbId)
    {
        this.wbId = wbId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return wbId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof WeiboFriendKey
                && ((WeiboFriendKey) other).wbId.equals(wbId);
    }
}
