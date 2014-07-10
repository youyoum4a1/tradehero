package com.tradehero.th.api.alert;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.Date;
import org.jetbrains.annotations.Nullable;

public class AlertCompactDTO implements DTO
{
    public int id;
    public double targetPrice;
    public Boolean upOrDown;
    public Double priceMovement;
    public boolean active;
    @Nullable public Date activeUntilDate;

    @Nullable public SecurityCompactDTO security;

    //<editor-fold desc="Constructors">
    public AlertCompactDTO()
    {
    }
    //</editor-fold>

    public AlertId getAlertId(UserBaseKey userBaseKey)
    {
        return getAlertId(userBaseKey.key);
    }

    public AlertId getAlertId(int userId)
    {
        return new AlertId(userId, id);
    }
}
