package com.androidth.general.api.security;

import android.support.annotation.Nullable;
import java.util.ArrayList;

public class SecurityMediaDTOList extends ArrayList<SecurityMediaDTO>
{
    @Nullable public SecurityMediaDTO getFlavorSecurityForDisplay()
    {
        SecurityMediaDTO securityMediaDTO = null;
        for (SecurityMediaDTO m: this)
        {
            if (m.securityId != 0)
            {
                securityMediaDTO = m;
            }

            // we prefer the first security with photo
            if (securityMediaDTO != null && securityMediaDTO.url != null)
            {
                return securityMediaDTO;
            }
        }
        return securityMediaDTO;
    }

    @Nullable public SecurityId createFlavorSecurityIdForDisplay()
    {
        SecurityMediaDTO securityMediaDTO = getFlavorSecurityForDisplay();
        if (securityMediaDTO == null)
        {
            return null;
        }
        return securityMediaDTO.createSecurityId();
    }
}
