package com.ayondo.academy.api.users;

import android.support.annotation.Nullable;
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
    @Nullable public Date userMarkingAsOfUtc;
    public Double userRoiSinceInception;
    public Double userPlSinceInceptionRefCcy;

    public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }
}
