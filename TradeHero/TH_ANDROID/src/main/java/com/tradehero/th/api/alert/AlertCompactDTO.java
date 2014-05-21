package com.tradehero.th.api.alert;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.Date;

public class AlertCompactDTO implements DTO
{
    public int id;
    public double targetPrice;
    public Boolean upOrDown;
    public Double priceMovement;
    public boolean active;
    public Date activeUntilDate;

    public SecurityCompactDTO security;

    public AlertCompactDTO()
    {
    }

    public AlertId getAlertId(UserBaseKey userBaseKey)
    {
        return getAlertId(userBaseKey.key);
    }

    public AlertId getAlertId(int userId)
    {
        return new AlertId(userId, id);
    }
}
