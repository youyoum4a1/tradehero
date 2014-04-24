package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import java.util.ArrayList;

public class HeroIdList extends ArrayList<FollowerHeroRelationId> implements DTO
{
    public static final String TAG = HeroIdList.class.getSimpleName();
}
