package com.tradehero.th.api.alert;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 12:51 PM To change this template use File | Settings | File Templates. */
public class AlertCompactDTO implements DTO
{
    public static final String TAG = AlertCompactDTO.class.getSimpleName();

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

    public AlertId getAlertId(int userId)
    {
        return new AlertId(userId, id);
    }
}
