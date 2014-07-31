package com.tradehero.th.api.social;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class InviteFormUserDTO extends InviteFormMessageDTO implements InviteFormDTO
{
    public List<InviteDTO> users;

    //<editor-fold desc="Constructors">
    public InviteFormUserDTO()
    {
        msg = "";
    }
    //</editor-fold>

    public void addAll(@NotNull List<UserFriendsDTO> userFriendsDTOs)
    {
        if (users == null)
        {
            users = new ArrayList<>();
        }
        for (@NotNull UserFriendsDTO userFriendsDTO : userFriendsDTOs)
        {
            users.add(userFriendsDTO.createInvite());
        }
    }

    public void add(@NotNull UserFriendsDTO userFriendsDTO)
    {
        if (users == null)
        {
            users = new ArrayList<>();
        }
        users.add(userFriendsDTO.createInvite());
    }
}
