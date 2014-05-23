package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKey;

public class DisplayNameDTO implements DTOKey
{
    public final String displayName;

    public DisplayNameDTO(String displayName)
    {
        this.displayName = displayName;
    }

    public boolean isSameName(String otherName)
    {
        return displayName == null ? otherName == null : displayName.equals(otherName);
    }
}
