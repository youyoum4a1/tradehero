package com.tradehero.th.loaders;

import com.tradehero.th.api.security.SecurityCompactDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 4:10 PM To change this template use File | Settings | File Templates. */
public class ListedSecurityCompact extends SecurityCompactDTO implements ItemWithComparableId<Integer>
{
    private int listIndex;

    public ListedSecurityCompact(SecurityCompactDTO securityCompactDTO)
    {
        super(securityCompactDTO);
    }

    public ListedSecurityCompact(SecurityCompactDTO securityCompactDTO, int listIndex)
    {
        super(securityCompactDTO);
        this.listIndex = listIndex;
    }

    @Override public Integer getId()
    {
        return listIndex;
    }

    @Override public void setId(Integer id)
    {
        this.listIndex = id;
    }

    @Override public int compareTo(ItemWithComparableId<Integer> other)
    {
        if (getId() == null)
        {
            throw new IllegalArgumentException("Item id is not set");
        }
        return getId().compareTo(other.getId());
    }

}
