package com.tradehero.th.fragments.social.hero;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.api.social.HeroIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.social.HeroCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class HeroManagerInfoFetcher
{
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected Lazy<HeroListCache> heroListCache;
    @Inject protected Lazy<HeroCache> heroCache;

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileFetchTask;
    private DTOCacheNew.Listener<UserBaseKey, HeroIdExtWrapper> heroListListener;

    public HeroManagerInfoFetcher(
            DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener,
            DTOCacheNew.Listener<UserBaseKey, HeroIdExtWrapper> heroListListener)
    {
        super();
        this.userProfileListener = userProfileListener;
        this.heroListListener = heroListListener;
        DaggerUtils.inject(this);
    }

    public void onDestroyView()
    {
        if (this.userProfileFetchTask != null)
        {
            this.userProfileFetchTask.setListener(null);
        }
        this.userProfileFetchTask = null;

        detachHeroListCache();
        setUserProfileListener(null);
        setHeroListListener(null);
    }

    protected void detachHeroListCache()
    {
        if (heroListListener != null)
        {
            heroListCache.get().unregister(heroListListener);
        }
    }

    public void setUserProfileListener(
            DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener)
    {
        this.userProfileListener = userProfileListener;
    }

    public void setHeroListListener(
            DTOCacheNew.Listener<UserBaseKey, HeroIdExtWrapper> heroListListener)
    {
        this.heroListListener = heroListListener;
    }

    public void fetch(UserBaseKey userBaseKey)
    {
        fetchUserProfile(userBaseKey);
        fetchHeroes(userBaseKey);
    }

    public void fetchUserProfile(UserBaseKey userBaseKey)
    {
        if (this.userProfileFetchTask != null)
        {
            this.userProfileFetchTask.setListener(null);
        }
        this.userProfileFetchTask =
                this.userProfileCache.get().getOrFetch(userBaseKey, this.userProfileListener);
        this.userProfileFetchTask.execute();
    }

    public void fetchHeroes(UserBaseKey userBaseKey)
    {
        HeroIdExtWrapper heroIdExtWrapper = heroListCache.get().get(userBaseKey);
        HeroIdList heroIds = (heroIdExtWrapper != null) ? heroIdExtWrapper.allActiveHeroes : null;
        HeroDTOList heroDTOs = heroCache.get().get(heroIds);
        if (heroIds != null
                && heroDTOs != null
                && heroIds.size()
                == heroDTOs.size()) // We need this longer test in case DTO have been flushed.
        {
            if (this.heroListListener != null)
            {
                this.heroListListener.onDTOReceived(userBaseKey, heroIdExtWrapper);
            }
        }
        else
        {
            detachHeroListCache();
            heroListCache.get().register(userBaseKey, heroListListener);
            heroListCache.get().getOrFetchAsync(userBaseKey);
        }
    }

    public void reloadHeroes(UserBaseKey userBaseKey,
            DTOCacheNew.Listener<UserBaseKey, HeroIdExtWrapper> heroListListener)
    {
        detachHeroListCache();
        heroListCache.get().register(userBaseKey, heroListListener);
        heroListCache.get().getOrFetchAsync(userBaseKey, true);
    }
}
