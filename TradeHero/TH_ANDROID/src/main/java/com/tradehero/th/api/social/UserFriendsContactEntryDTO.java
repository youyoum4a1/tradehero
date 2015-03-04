package com.tradehero.th.api.social;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.social.key.ContactFriendKey;

public class UserFriendsContactEntryDTO extends UserFriendsDTO
{
    @NonNull @Override public ContactFriendKey getFriendKey()
    {
        return new ContactFriendKey(email);
    }

    @Override public int getNetworkLabelImage()
    {
        return R.drawable.default_image;
    }

    @Override public InviteDTO createInvite()
    {
        return new InviteContactEntryDTO(email);
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
