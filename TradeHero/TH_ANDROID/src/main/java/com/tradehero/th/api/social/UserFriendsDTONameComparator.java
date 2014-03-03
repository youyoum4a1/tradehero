package com.tradehero.th.api.social;

import java.util.Comparator;

/**
 * Created by xavier on 3/3/14.
 */
public class UserFriendsDTONameComparator implements Comparator<UserFriendsDTO>
{
    public static final String TAG = UserFriendsDTONameComparator.class.getSimpleName();

    @Override public int compare(UserFriendsDTO lhs, UserFriendsDTO rhs)
    {
        if (lhs == rhs) return 0;
        else if (lhs == null) return -1;
        else if (rhs == null) return 1;
        else if (lhs.name == null || lhs.name.isEmpty()) return -1;
        else if (rhs.name == null || lhs.name.isEmpty()) return 1;
        else if (lhs.name.equals(rhs.name)) return 0;
        else return lhs.name.compareTo(rhs.name);
    }
}
