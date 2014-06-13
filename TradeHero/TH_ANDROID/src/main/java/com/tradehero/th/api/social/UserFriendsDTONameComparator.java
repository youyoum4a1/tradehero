package com.tradehero.th.api.social;

import java.util.Comparator;

public class UserFriendsDTONameComparator implements Comparator<UserFriendsDTO>
{
    @Override public int compare(UserFriendsDTO lhs, UserFriendsDTO rhs)
    {
        if (lhs == rhs)
        {
            return 0;
        }
        else if (lhs == null)
        {
            return -1;
        }
        else if (rhs == null)
        {
            return 1;
        }
        else if (lhs.name == null || lhs.name.isEmpty())
        {
            if (rhs.name == null || rhs.name.isEmpty())
            {
                return 0;
            }
            return -1;
        }
        else if (rhs.name == null || rhs.name.isEmpty())
        {
            return 1;
        }
        else if (lhs.name.equals(rhs.name))
        {
            return 0;
        }
        return lhs.name.compareTo(rhs.name);
    }
}
