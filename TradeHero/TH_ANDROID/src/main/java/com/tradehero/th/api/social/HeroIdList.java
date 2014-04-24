package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import java.util.ArrayList;
import java.util.Collection;

public class HeroIdList extends ArrayList<FollowerHeroRelationId> implements DTO
{
    //<editor-fold desc="Constructors">
    public HeroIdList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public HeroIdList()
    {
        super();
    }

    public HeroIdList(Collection<? extends FollowerHeroRelationId> c)
    {
        super(c);
    }
    //</editor-fold>
}
