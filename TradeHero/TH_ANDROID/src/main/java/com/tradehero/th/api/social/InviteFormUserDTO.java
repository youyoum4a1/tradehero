package com.tradehero.th.api.social;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class InviteFormUserDTO extends InviteFormMessageDTO
{
    @NotNull public List<InviteDTO> users;

    //<editor-fold desc="Constructors">
    public InviteFormUserDTO()
    {
        super("");
        users = new ArrayList<>();
    }

    public InviteFormUserDTO(@NotNull List<? extends UserFriendsDTO> userFriendsDTOs)
    {
        this();
        addAll(userFriendsDTOs);
    }
    //</editor-fold>

    public void addAll(@NotNull List<? extends UserFriendsDTO> userFriendsDTOs)
    {
        for (@NotNull UserFriendsDTO userFriendsDTO : userFriendsDTOs)
        {
            add(userFriendsDTO);
        }
    }

    public void add(@NotNull UserFriendsDTO userFriendsDTO)
    {
        users.add(userFriendsDTO.createInvite());
    }
}
