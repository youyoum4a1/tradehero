package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class UserSearchResultDTO implements DTO
{
    public String userFirstName;
    public String userLastName;
    public String userthDisplayName;
    public Integer userId;
    public String userPicture;

    public Double userCashBalanceRefCcy;
    public Date userMarkingAsOfUtc;
    public Double userRoiSinceInception;
    public Double userPlSinceInceptionRefCcy;

    public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }
}
