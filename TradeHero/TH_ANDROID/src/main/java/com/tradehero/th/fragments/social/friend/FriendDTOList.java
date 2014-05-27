package com.tradehero.th.fragments.social.friend;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.social.UserFriendsDTO;

import java.util.ArrayList;

/**
 * Created by wangliang on 14-5-27.
 */
public class FriendDTOList extends ArrayList<UserFriendsDTO>
        implements DTO {

    public FriendDTOList()
    {
        super();
    }

    public FriendDTOList(int capacity)
    {
        super(capacity);
    }
}
