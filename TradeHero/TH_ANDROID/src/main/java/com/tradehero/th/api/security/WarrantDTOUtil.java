package com.tradehero.th.api.security;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/23/14.
 */
@Singleton public class WarrantDTOUtil
{
    public static final String TAG = WarrantDTOUtil.class.getSimpleName();

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
