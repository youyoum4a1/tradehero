package com.tradehero.th.api.security;

import java.util.ArrayList;

public class SecurityMediaDTOList extends ArrayList<SecurityMediaDTO>
{
    public SecurityIdList getMediaSecurityIds()
    {
        SecurityIdList securityIds = new SecurityIdList();
        for (SecurityMediaDTO securityMediaDTO : this)
        {
            if (securityMediaDTO != null &&
                    securityMediaDTO.hasValidSecurityId())
            {
                securityIds.add(securityMediaDTO.createSecurityId());
            }
        }
        return securityIds;
    }

    public SecurityMediaDTO getFlavorSecurityForDisplay()
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

    public SecurityId createFlavorSecurityIdForDisplay()
    {
        SecurityMediaDTO securityMediaDTO = getFlavorSecurityForDisplay();
        if (securityMediaDTO == null)
        {
            return null;
        }
        return securityMediaDTO.createSecurityId();
    }
}
