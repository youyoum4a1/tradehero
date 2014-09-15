package com.tradehero.th.fragments.chinabuild.data;

import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

/**
 * Created by huhaiping on 14-8-26.
 */
public class SecurityPositionItem implements PositionInterface
{
    public SecurityCompactDTO security;
    public PositionDTO position;

    public SecurityPositionItem()
    {
    }

    public SecurityPositionItem(SecurityCompactDTO security, PositionDTO position)
    {
        this.security = security;
        this.position = position;
    }
}
