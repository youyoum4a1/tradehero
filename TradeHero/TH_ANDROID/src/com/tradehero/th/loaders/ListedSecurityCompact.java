package com.tradehero.th.loaders;

import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 4:10 PM To change this template use File | Settings | File Templates. */
public class ListedSecurityCompact extends SecurityCompactDTO implements ItemWithComparableId<Integer>
{
    private int index;

    public ListedSecurityCompact(final SecurityCompactDTO securityCompactDTO)
    {
        super(securityCompactDTO);
    }

    public ListedSecurityCompact(final SecurityCompactDTO securityCompactDTO, final int index)
    {
        super(securityCompactDTO);
        this.index = index;
    }

    @Override public Integer getId()
    {
        return index;
    }

    @Override public void setId(final Integer id)
    {
        this.index = id;
    }

    @Override public int compareTo(final ItemWithComparableId<Integer> other)
    {
        if (getId() == null)
        {
            throw new IllegalArgumentException("Item id is not set");
        }
        return getId().compareTo(other.getId());
    }

}
