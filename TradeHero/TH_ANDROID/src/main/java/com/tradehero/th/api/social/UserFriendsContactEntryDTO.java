package com.tradehero.th.api.social;

import android.support.annotation.NonNull;
import com.tradehero.th.R;

public class UserFriendsContactEntryDTO extends UserFriendsDTO
{
    //<editor-fold desc="Constructors">
    public UserFriendsContactEntryDTO()
    {
        super();
    }
    //</editor-fold>

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

    @Override protected boolean equals(@NonNull UserFriendsDTO other)
    {
        return super.equals(other) &&
                email == null ? other.email == null : email.equals(other.email);
    }
}
