package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:25 PM To change this template use File | Settings | File Templates. */
public class UserFollowerDTO extends UserProfileCompactDTO implements DTO
{
    public static final String TAG = UserFollowerDTO.class.getSimpleName();

    public List<FollowerTransactionDTO> followerTransactions;
    public double totalRevenue;

    public UserFollowerDTO()
    {
        super();
    }
}
