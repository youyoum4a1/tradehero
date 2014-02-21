package com.tradehero.th.api.security;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/21/14.
 */
@Singleton public class SecurityCompactDTOFactory
{
    public static final String TAG = SecurityCompactDTOFactory.class.getSimpleName();

    @Inject public SecurityCompactDTOFactory()
    {
    }

    public SecurityCompactDTO clonePerType(SecurityCompactDTO securityCompactDTO)
    {
        SecurityCompactDTO returned = securityCompactDTO;
        if (securityCompactDTO == null || securityCompactDTO.securityType == SecurityType.EQUITY.value)
        {
            // Nothing to do
        }
        else if (securityCompactDTO.securityType == SecurityType.WARRANT.value)
        {
            if (!(securityCompactDTO instanceof WarrantDTO))
            {
                securityCompactDTO = new WarrantDTO(securityCompactDTO);
            }
        }
        // TODO other types

        return securityCompactDTO;
    }

    public List<SecurityCompactDTO> clonePerType(List<SecurityCompactDTO> list)
    {
        if (list == null)
        {
            return null;
        }

        List<SecurityCompactDTO> returned = new ArrayList<>();

        for (SecurityCompactDTO securityCompactDTO : list)
        {
            returned.add(clonePerType(securityCompactDTO));
        }

        return returned;
    }
}
