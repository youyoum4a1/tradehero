package com.tradehero.th.loaders;

import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

public class ListedSecurityCompactFactory
{
    public static List<ListedSecurityCompact> createList(final List<SecurityCompactDTO> securityCompactDTOList, int firstIndex)
    {
        if (securityCompactDTOList == null)
        {
            return null;
        }

        List<ListedSecurityCompact> listedList = new ArrayList<>();
        for(SecurityCompactDTO securityCompactDTO: securityCompactDTOList)
        {
            listedList.add(new ListedSecurityCompact(securityCompactDTO, firstIndex++));
        }
        return listedList;
    }
}
