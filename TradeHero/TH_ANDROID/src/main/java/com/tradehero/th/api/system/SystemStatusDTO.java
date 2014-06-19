package com.tradehero.th.api.system;

import com.tradehero.common.persistence.DTO;

public class SystemStatusDTO implements DTO
{
    public static final boolean DEFAULT_ALERTS_ARE_FREE = false;

    /**
     * When true, alerts are free and need no IAP
     */
    public boolean alertsAreFree = DEFAULT_ALERTS_ARE_FREE;

    //<editor-fold desc="Constructors">
    public SystemStatusDTO()
    {
        super();
    }
    //</editor-fold>
}
