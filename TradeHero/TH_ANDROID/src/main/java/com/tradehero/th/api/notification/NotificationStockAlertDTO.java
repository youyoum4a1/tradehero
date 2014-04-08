package com.tradehero.th.api.notification;

import com.tradehero.common.persistence.DTO;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationStockAlertDTO implements DTO
{
    public Integer securityId;
    public String exchangeName;
    public String securitySymbol;
}
