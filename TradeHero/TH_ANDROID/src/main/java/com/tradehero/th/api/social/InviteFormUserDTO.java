package com.tradehero.th.api.social;

import java.util.ArrayList;
import java.util.List;
import android.support.annotation.NonNull;

public class InviteFormUserDTO extends InviteFormMessageDTO
{
    @NonNull public List<InviteDTO> users;

    //<editor-fold desc="Constructors">
    public InviteFormUserDTO()
    {
        super("");
        users = new ArrayList<>();
    }

    public InviteFormUserDTO(@NonNull List<? extends UserFriendsDTO> userFriendsDTOs)
    {
        this();
        addAll(userFriendsDTOs);
    }
    //</editor-fold>

    public void addAll(@NonNull List<? extends UserFriendsDTO> userFriendsDTOs)
    {
        for (UserFriendsDTO userFriendsDTO : userFriendsDTOs)
        {
            add(userFriendsDTO);
        }
    }

    public void add(@NonNull UserFriendsDTO userFriendsDTO)
    {
        users.add(userFriendsDTO.createInvite());
    }
}
