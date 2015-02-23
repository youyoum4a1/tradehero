package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;

public class SecuritySuperCompactFactory
{
    @NonNull public static SecuritySuperCompactDTOList create(@NonNull List<? extends SecurityCompactDTO> securityCompactDTOs)
    {
        SecuritySuperCompactDTOList list = new SecuritySuperCompactDTOList();
        for (SecurityCompactDTO compactDTO : securityCompactDTOs)
        {
            list.add(create(compactDTO));
        }
        return list;
    }

    @NonNull public static SecuritySuperCompactDTO create(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        SecuritySuperCompactDTO superCompact = new SecuritySuperCompactDTO();
        superCompact.id = securityCompactDTO.id;
        superCompact.name = securityCompactDTO.name;
        superCompact.symbol = securityCompactDTO.symbol;
        superCompact.marketCap = securityCompactDTO.marketCap == null ? 0 : securityCompactDTO.marketCap;
        superCompact.blobRef = securityCompactDTO.imageBlobUrl;
        return superCompact;
    }
}
