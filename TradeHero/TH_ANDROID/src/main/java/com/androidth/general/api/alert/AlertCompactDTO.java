package com.androidth.general.api.alert;

import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.users.UserBaseKey;
import java.util.Date;

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
