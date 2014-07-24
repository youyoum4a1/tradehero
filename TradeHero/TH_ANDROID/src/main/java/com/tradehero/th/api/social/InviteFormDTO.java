package com.tradehero.th.api.social;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class InviteFormDTO
{
    public String msg;
    public List<InviteDTO> users;
    public boolean isWeiboInvite = false;

    //<editor-fold desc="Constructors">
    public InviteFormDTO()
    {
        msg = "";
    }

    public InviteFormDTO(String msg,boolean isWeiboInvite)
    {
        this.msg = msg;
        this.isWeiboInvite = isWeiboInvite;
    }
    //</editor-fold>

    public void addAll(@NotNull List<UserFriendsDTO> userFriendsDTOs)
    {
        if (users == null)
        {
            users = new ArrayList<>();
        }
        for(@NotNull UserFriendsDTO userFriendsDTO : userFriendsDTOs)
        {
            users.add(userFriendsDTO.createInvite());
        }
    }
}
