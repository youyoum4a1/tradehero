package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class WarrantDTOUtil
{
    @Inject public WarrantDTOUtil()
    {
    }

    public boolean areAllWarrants(List<SecurityCompactDTO> securityCompactDTOs)
    {
        for (SecurityCompactDTO securityCompactDTO: securityCompactDTOs)
        {
            if (!(securityCompactDTO instanceof WarrantDTO))
            {
                return false;
            }
        }
        return true;
    }
}