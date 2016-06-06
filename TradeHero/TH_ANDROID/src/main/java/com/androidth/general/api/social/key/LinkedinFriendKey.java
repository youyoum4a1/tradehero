package com.androidth.general.api.social.key;

import android.support.annotation.NonNull;

public class LinkedinFriendKey implements FriendKey
{
    @NonNull public final String liId;

    //<editor-fold desc="Constructors">
    public LinkedinFriendKey(@NonNull String liId)
    {
        this.liId = liId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return liId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof LinkedinFriendKey
                && ((LinkedinFriendKey) other).liId.equals(liId);
    }
}
