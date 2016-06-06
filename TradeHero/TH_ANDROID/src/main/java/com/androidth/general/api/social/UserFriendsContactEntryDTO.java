package com.androidth.general.api.social;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.social.key.ContactFriendKey;

public class UserFriendsContactEntryDTO extends UserFriendsDTO
{
    @NonNull @Override public ContactFriendKey getFriendKey()
    {
        return new ContactFriendKey(email);
    }

    @Override @DrawableRes public int getNetworkLabelImage()
    {
        return R.drawable.default_image;
    }

    @Override public InviteDTO createInvite()
    {
        return new InviteContactEntryDTO(email);
    }

    @NonNull @Override public String getAnalyticsTag()
    {
        return "Contact";
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (email == null ? 0 : email.hashCode());
    }

    @Override protected boolean equalFields(@NonNull UserFriendsDTO other)
    {
        return super.equalFields(other) &&
                email == null ? other.email == null : email.equals(other.email);
    }
}
