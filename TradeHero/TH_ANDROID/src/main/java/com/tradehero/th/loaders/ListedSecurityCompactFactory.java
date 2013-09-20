package com.tradehero.th.loaders;

import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 5:17 PM To change this template use File | Settings | File Templates. */
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
