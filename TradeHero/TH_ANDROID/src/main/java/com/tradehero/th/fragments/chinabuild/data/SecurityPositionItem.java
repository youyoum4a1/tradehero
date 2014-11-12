package com.tradehero.th.fragments.chinabuild.data;

import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

/**
 * Created by huhaiping on 14-8-26.
 */
public class SecurityPositionItem implements PositionInterface
{

    public static final int TYPE_ACTIVE = 0;
    public static final int TYPE_CLOSED = 1;
    public SecurityCompactDTO security;
    public PositionDTO position;
    public int type;

    public SecurityPositionItem(SecurityCompactDTO security, PositionDTO position)
    {
        this.security = security;
        this.position = position;
        type = TYPE_ACTIVE;
    }

    public SecurityPositionItem(SecurityCompactDTO security, PositionDTO position,int type)
    {
        this.security = security;
        this.position = position;
        this.type = type;
    }
}
