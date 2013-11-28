package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroId;
import com.tradehero.th.api.social.HeroIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserService;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class HeroListCache extends StraightDTOCache<UserBaseKey, HeroIdList>
{
    public static final String TAG = HeroListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<UserService> userService;
    @Inject protected Lazy<HeroCache> heroCache;

    //<editor-fold desc="Constructors">
    @Inject public HeroListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected HeroIdList fetch(UserBaseKey key) throws Throwable
    {
        return putInternal(key, userService.get().getHeroes(key.key));
    }

    protected HeroIdList putInternal(UserBaseKey key, List<HeroDTO> fleshedValues)
    {
        HeroIdList heroIds = null;
        if (fleshedValues != null)
        {
            heroIds = new HeroIdList();
            HeroId heroId;
            for (HeroDTO heroDTO: fleshedValues)
            {
                //THLog.d(TAG, heroDTO.toString());
                heroId = heroDTO.getHeroId(key);
                heroIds.add(heroId);
                heroCache.get().put(heroId, heroDTO);
            }
            put(key, heroIds);
        }
        return heroIds;
    }
}
