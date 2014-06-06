package com.tradehero.th.fragments.social.friend;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FriendsListCache extends StraightDTOCache<FriendsListKey, FriendDTOList>
{
    private UserServiceWrapper userServiceWrapper;

    @Inject
    public FriendsListCache(@ListCacheMaxSize IntPreference maxSize,
                                  UserServiceWrapper userServiceWrapper)
    {
        super(maxSize.get());
        this.userServiceWrapper = userServiceWrapper;
    }

    @Override protected FriendDTOList fetch(FriendsListKey key) throws Throwable
    {
        List<UserFriendsDTO> data =  userServiceWrapper.getSocialFriends(key.userBaseKey, key.socialNetworkEnum);
        return putInternal(data);
    }

    private FriendDTOList putInternal(List<UserFriendsDTO> data)
    {

        if (data != null)
        {
            FriendDTOList list = new FriendDTOList(data.size());
            list.addAll(data);
            return list;
        }
        return null;
    }


}
