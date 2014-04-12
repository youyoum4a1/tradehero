package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroIdExt;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.api.social.HeroIdList;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class HeroListCache extends StraightDTOCache<HeroKey, HeroIdExtWrapper>
{
    public static final String TAG = HeroListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<UserServiceWrapper> userService;
    @Inject protected Lazy<HeroCache> heroCache;

    //<editor-fold desc="Constructors">
    @Inject public HeroListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected HeroIdExtWrapper fetch(HeroKey key) throws Throwable
    {
        List<HeroDTO> allHeros = userService.get().getHeroes(key);
        return putInternal(key, allHeros);
    }

    @Override public HeroIdExtWrapper put(HeroKey key, HeroIdExtWrapper value)
    {
        //Just cache all heros,do not cache paid heros and free heros separately.
        if (key.heroType != HeroType.ALL)
        {
            return value;
        }
        return super.put(key, value);
    }

    @Override public HeroIdExtWrapper get(HeroKey key)
    {
        if (key.heroType == HeroType.ALL)
        {
            return super.get(key);
        }
        else {
            HeroIdExtWrapper allHeros = get(new HeroKey(key.followerKey,HeroType.ALL));
            if (allHeros != null && allHeros.heroIdList != null)
            {
                Map<HeroType,HeroIdList> herosMap = splitHeros(allHeros.heroIdList);
                if (herosMap != null)
                {
                    HeroIdList heroIdList = herosMap.get(key.heroType);

                    HeroIdExtWrapper heroIdExtWrapper = new HeroIdExtWrapper();
                    heroIdExtWrapper.heroIdList = heroIdList;
                    heroIdExtWrapper.herosCountGetPaid = allHeros.herosCountGetPaid;
                    heroIdExtWrapper.herosCountNotGetPaid = allHeros.herosCountNotGetPaid;
                    return heroIdExtWrapper;
                }
            }

           return null;
        }

    }

    protected HeroIdExtWrapper putInternal(HeroKey key, List<HeroDTO> fleshedValues)
    {
        HeroIdList heroIds = null;
        HeroIdList allHeroIds = null;

        HeroIdExtWrapper AllHeroIdExtWrapper = new HeroIdExtWrapper();
        HeroIdExtWrapper neededHeroIdExtWrapper = new HeroIdExtWrapper();

        if (fleshedValues != null)
        {
            heroIds = new HeroIdList();
            allHeroIds = new HeroIdList();
            HeroIdExt heroIdExt;
            boolean forHerosGetPaied = (key.heroType == HeroType.PREMIUM);
            boolean forAllHeros = (key.heroType == HeroType.ALL);
            for (HeroDTO heroDTO : fleshedValues)
            {
                //THLog.d(TAG, heroDTO.toString());
                heroIdExt = new HeroIdExt(heroDTO.getHeroId(key.followerKey));

                if (forAllHeros)
                {
                    heroIdExt.getPaied = heroDTO.isFreeFollow;
                    heroIds.add(heroIdExt);
                }
                else if (forHerosGetPaied && heroDTO.isFreeFollow)
                {
                    heroIdExt.getPaied = true;
                    heroIds.add(heroIdExt);
                }
                else if (!forAllHeros && !forHerosGetPaied && !heroDTO.isFreeFollow)
                {
                    heroIdExt.getPaied = false;
                    heroIds.add(heroIdExt);
                }
                allHeroIds.add(heroIdExt);
                heroCache.get().put(heroIdExt, heroDTO);
            }

            int[]result  = computeFollowersTypeCount(fleshedValues);
            AllHeroIdExtWrapper.herosCountGetPaid = result[0];
            AllHeroIdExtWrapper.herosCountNotGetPaid = result[1];
            AllHeroIdExtWrapper.heroIdList = allHeroIds;

            neededHeroIdExtWrapper.herosCountGetPaid = result[0];
            neededHeroIdExtWrapper.herosCountNotGetPaid = result[1];
            neededHeroIdExtWrapper.heroIdList = heroIds;
            if (forAllHeros)
            {
                put(key, AllHeroIdExtWrapper);
            }else
            {
                key = new HeroKey(key.followerKey,HeroType.ALL);
                //cache all heros
                put(key, AllHeroIdExtWrapper);
            }

        }

        //but just return needed heros
        return neededHeroIdExtWrapper;
    }


    ////////////////////

    private int[] computeFollowersTypeCount(List<HeroDTO> allHeros)
    {
        int [] result = new int[2];
        if (allHeros != null)
        {

            int paidCount = 0;
            int notPaidCount = 0;
            int totalCount = allHeros.size();
                for(HeroDTO hero:allHeros)
                {
                    if(hero.active)
                    {
                        if (!hero.isFreeFollow)
                        {
                            paidCount += 1;
                        }
                        else
                        {
                            notPaidCount+=1;
                        }
                    }


                }

            result[0] = paidCount;
            result[1] = notPaidCount;
        }
        return result;

    }

    private Map<HeroType,HeroIdList> splitHeros(HeroIdList allHeroIdList)
    {
        if (allHeroIdList == null)
        {
            return null;
        }
        Map<HeroType,HeroIdList> map = new HashMap<>();
        HeroIdList herosGetPaid = new HeroIdList();
        HeroIdList herosNotGetPaid = new HeroIdList();
        map.put(HeroType.PREMIUM,herosGetPaid);
        map.put(HeroType.FREE,herosNotGetPaid);

        int size = allHeroIdList.size();
        for(int i=0;i<size;i++)
        {
            HeroIdExt heroIdExt = (HeroIdExt)allHeroIdList.get(i);
            if (heroIdExt.getPaied)
            {
                herosGetPaid.add(heroIdExt);
            }
            else
            {
                herosNotGetPaid.add(heroIdExt);
            }
        }
        return map;
    }

}
