package com.androidth.general.api.social.key;

import android.support.annotation.NonNull;

public class ContactFriendKey implements FriendKey
{
    @NonNull public final String email;

    //<editor-fold desc="Constructors">
    public ContactFriendKey(@NonNull String email)
    {
        this.email = email;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return email.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof ContactFriendKey
                && ((ContactFriendKey) other).email.equals(email);
    }
}
