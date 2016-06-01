package com.ayondo.academy.api.notification;

import com.tradehero.common.persistence.DTO;

public class NotificationStockAlertDTO implements DTO
{
    public Integer securityId;
    public String exchangeName;
    public String securitySymbol;
}
