package com.tradehero.th.api.notification;

import com.tradehero.common.persistence.DTO;


public class NotificationStockAlertDTO implements DTO
{
    public Integer securityId;
    public String exchangeName;
    public String securitySymbol;
}
