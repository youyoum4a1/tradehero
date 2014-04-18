package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroIdExt;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.api.social.HeroIdList;
import com.tradehero.th.network.service.UserServiceWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class HeroListCache extends StraightDTOCache<HeroKey, HeroIdExtWrapper>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    protected UserServiceWrapper userServiceWrapper;
    protected HeroCache heroCache;

    //<editor-fold desc="Constructors">
    @Inject public HeroListCache(UserServiceWrapper userServiceWrapper, HeroCache heroCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
        this.heroCache = heroCache;
    }
    //</editor-fold>

    @Override protected HeroIdExtWrapper fetch(HeroKey key) throws Throwable
    {
        List<HeroDTO> allHeros = userServiceWrapper.getHeroes(key);
        Timber.d("HeroListCache#fetch fetchHeroes allHeros:%s",allHeros);
        return putInternal(key, allHeros);
    }

    @Override public HeroIdExtWrapper put(HeroKey key, HeroIdExtWrapper value)
    {
        //Just cache all heros,do not cache paid heros and free heros separately.
        if (key.heroType != HeroType.ALL)
        {
            return value;
        }
        Timber.d("HeroListCache#put key:%s value:%s",key,value);
        return super.put(key, value);
    }

    //04-18 20:04:37.145  26787-26787/com.tradehero.th D/TradeHero-HeroManagerInfoFetcher:116﹕ fetchHeroes try to fetch heroType:Premium
    //04-18 20:04:42.101  26787-26787/com.tradehero.th D/TradeHero-HeroListCache:70﹕ HeroListCache,fetchHeroes key HeroKey [UserBaseKey key=239284], HeroType Free, return HeroIdExtWrapper{heroIdList=[[heroId=375098; followerId=239284], [heroId=383332; followerId=239284], [heroId=394257; followerId=239284]], herosCountGetPaid=15, herosCountNotGetPaid=3}
    //04-18 20:04:42.101  26787-26787/com.tradehero.th D/TradeHero-HeroManagerInfoFetcher:108﹕ fetchHeroes get the result and return 3 heroType:Free
    //04-18 20:04:49.949  26787-26787/com.tradehero.th D/TradeHero-HeroManagerInfoFetcher:108﹕ fetchHeroes get the result and return 18 heroType:All
    //04-18 20:04:50.465  26787-26787/com.tradehero.th D/TradeHero-HeroListCache:70﹕ HeroListCache,fetchHeroes key HeroKey [UserBaseKey key=239284], HeroType Free, return HeroIdExtWrapper{heroIdList=[[heroId=375098; followerId=239284], [heroId=383332; followerId=239284], [heroId=394257; followerId=239284]], herosCountGetPaid=15, herosCountNotGetPaid=3}
    //04-18 20:04:50.465  26787-26787/com.tradehero.th D/TradeHero-HeroManagerInfoFetcher:108﹕ fetchHeroes get the result and return 3 heroType:Free
    //04-18 20:04:50.981  26787-26787/com.tradehero.th D/TradeHero-HeroListCache:70﹕ HeroListCache,fetchHeroes key HeroKey [UserBaseKey key=239284], HeroType Premium, return HeroIdExtWrapper{heroIdList=[[heroId=365543; followerId=239284], [heroId=366496; followerId=239284], [heroId=367422; followerId=239284], [heroId=371581; followerId=239284], [heroId=373205; followerId=239284], [heroId=373660; followerId=239284], [heroId=379647; followerId=239284], [heroId=387230; followerId=239284], [heroId=390992; followerId=239284], [heroId=391022; followerId=239284], [heroId=393429; followerId=239284], [heroId=393533; followerId=239284], [heroId=393931; followerId=239284], [heroId=393942; followerId=239284], [heroId=397448; followerId=239284]], herosCountGetPaid=15, herosCountNotGetPaid=3}
    //04-18 20:04:50.981  26787-26787/com.tradehero.th D/TradeHero-HeroManagerInfoFetcher:108﹕ fetchHeroes get the result and return 15 heroType:Premium
    //04-18 20:04:51.381  26787-26787/com.tradehero.th D/TradeHero-HeroListCache:70﹕ HeroListCache,fetchHeroes key HeroKey [UserBaseKey key=239284], HeroType Free, return HeroIdExtWrapper{heroIdList=[[heroId=375098; followerId=239284], [heroId=383332; followerId=239284], [heroId=394257; followerId=239284]], herosCountGetPaid=15, herosCountNotGetPaid=3}
    //04-18 20:04:51.381  26787-26787/com.tradehero.th D/TradeHero-HeroManagerInfoFetcher:108﹕ fetchHeroes get the result and return 3 heroType:Free
    //04-18 20:05:05.337  26787-26787/com.tradehero.th D/TradeHero-HeroesTabContentFragment$4:231﹕ onUserFollowSuccess,fetchHeroes return null
    //    04-18 20:05:05.337  26787-26787/com.tradehero.th D/TradeHero-HeroManagerInfoFetcher:116﹕ fetchHeroes try to fetch heroType:Free
    //04-18 20:05:12.333  26787-26787/com.tradehero.th D/TradeHero-HeroListCache:70﹕ HeroListCache,fetchHeroes key HeroKey [UserBaseKey key=239284], HeroType Premium, return HeroIdExtWrapper{heroIdList=[], herosCountGetPaid=15, herosCountNotGetPaid=2}
    //04-18 20:05:12.333  26787-26787/com.tradehero.th D/TradeHero-HeroManagerInfoFetcher:108﹕ fetchHeroes get the result and return 0 heroType:Premium

    @Override public HeroIdExtWrapper get(HeroKey key)
    {
        if (key.heroType == HeroType.ALL)
        {
            //Timber.d("HeroListCache#get,fetchHeroes key %s, return %s",key, super.get(key));
            return super.get(key);
        }
        else
        {
            HeroIdExtWrapper allHeros = get(new HeroKey(key.followerKey, HeroType.ALL));
            if (allHeros != null && allHeros.heroIdList != null)
            {
                Map<HeroType, HeroIdList> herosMap = splitHeros(allHeros.heroIdList);
                if (herosMap != null)
                {
                    HeroIdList heroIdList = herosMap.get(key.heroType);

                    HeroIdExtWrapper heroIdExtWrapper = new HeroIdExtWrapper();
                    heroIdExtWrapper.heroIdList = heroIdList;
                    heroIdExtWrapper.herosCountGetPaid = allHeros.herosCountGetPaid;
                    heroIdExtWrapper.herosCountNotGetPaid = allHeros.herosCountNotGetPaid;

                    //Timber.d("HeroListCache#get,fetchHeroes key %s, return %s",key, heroIdExtWrapper);
                    return heroIdExtWrapper;
                }
            }

            return null;
        }
    }

    protected HeroIdExtWrapper putInternal(HeroKey key, List<HeroDTO> fleshedValues)
    {
        Timber.d("HeroListCache#putInternal");
        HeroIdList heroIds = null;
        HeroIdList allHeroIds = null;

        HeroIdExtWrapper AllHeroIdExtWrapper = new HeroIdExtWrapper();
        HeroIdExtWrapper neededHeroIdExtWrapper = new HeroIdExtWrapper();

        if (fleshedValues != null)
        {
            heroIds = new HeroIdList();
            allHeroIds = new HeroIdList();
            HeroIdExt heroIdExt;
            boolean forHerosGetPaid = (key.heroType == HeroType.PREMIUM);
            boolean forAllHeros = (key.heroType == HeroType.ALL);
            for (HeroDTO heroDTO : fleshedValues)
            {
                //THLog.d(TAG, heroDTO.toString());
                if (!heroDTO.active)
                {
                    continue;
                }
                heroIdExt = new HeroIdExt(heroDTO.getHeroId(key.followerKey));
                heroIdExt.getPaid = !heroDTO.isFreeFollow;
                if (forAllHeros)
                {
                    heroIdExt.getPaid = !heroDTO.isFreeFollow;
                    heroIds.add(heroIdExt);
                }
                else if (forHerosGetPaid && !heroDTO.isFreeFollow)
                {
                    heroIdExt.getPaid = true;
                    heroIds.add(heroIdExt);
                }
                else if (!forAllHeros && !forHerosGetPaid && heroDTO.isFreeFollow)
                {
                    heroIdExt.getPaid = false;
                    heroIds.add(heroIdExt);
                }

                allHeroIds.add(heroIdExt);
                heroCache.put(heroIdExt, heroDTO);
            }

            int[] result = computeFollowersTypeCount(fleshedValues);
            AllHeroIdExtWrapper.herosCountGetPaid = result[0];
            AllHeroIdExtWrapper.herosCountNotGetPaid = result[1];
            AllHeroIdExtWrapper.heroIdList = allHeroIds;

            neededHeroIdExtWrapper.herosCountGetPaid = result[0];
            neededHeroIdExtWrapper.herosCountNotGetPaid = result[1];
            neededHeroIdExtWrapper.heroIdList = heroIds;

            Timber.d("HeroListCache#putInternal,all size %s,key size:%s,key:%s",AllHeroIdExtWrapper.heroIdList.size(),neededHeroIdExtWrapper.heroIdList.size(),key.heroType);

            if (forAllHeros)
            {
                put(key, AllHeroIdExtWrapper);
            }
            else
            {
                key = new HeroKey(key.followerKey, HeroType.ALL);
                //cache all heros
                put(key, AllHeroIdExtWrapper);
            }
        }

        //HeroIdExtWrapper all = get(new HeroKey(key.followerKey,HeroType.ALL));
        //HeroIdExtWrapper free = get(new HeroKey(key.followerKey,HeroType.FREE));
        //HeroIdExtWrapper paid = get(new HeroKey(key.followerKey,HeroType.PREMIUM));
        //
        //Timber.d("HeroListCache#putInternal,fetchHeroes free %s",free);
        //Timber.d("HeroListCache#putInternal,fetchHeroes paid %s",paid);
        //Timber.d("HeroListCache#putInternal,fetchHeroes all %s",all);

        //but just return needed heros
        return neededHeroIdExtWrapper;
    }

    ////////////////////

    private int[] computeFollowersTypeCount(List<HeroDTO> allHeros)
    {
        int[] result = new int[2];
        if (allHeros != null)
        {
            int paidCount = 0;
            int notPaidCount = 0;
            int totalCount = allHeros.size();
            for (HeroDTO hero : allHeros)
            {
                if (hero.active)
                {
                    if (!hero.isFreeFollow)
                    {
                        paidCount += 1;
                    }
                    else
                    {
                        notPaidCount += 1;
                    }
                }
            }

            result[0] = paidCount;
            result[1] = notPaidCount;
        }
        return result;
    }

    private Map<HeroType, HeroIdList> splitHeros(HeroIdList allHeroIdList)
    {
        if (allHeroIdList == null)
        {
            return null;
        }
        Map<HeroType, HeroIdList> map = new HashMap<>();
        HeroIdList herosGetPaid = new HeroIdList();
        HeroIdList herosNotGetPaid = new HeroIdList();
        map.put(HeroType.PREMIUM, herosGetPaid);
        map.put(HeroType.FREE, herosNotGetPaid);

        int size = allHeroIdList.size();
        for (int i = 0; i < size; i++)
        {
            HeroIdExt heroIdExt = (HeroIdExt) allHeroIdList.get(i);
            if (heroIdExt.getPaid)
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
