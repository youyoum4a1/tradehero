package com.tradehero.th.fragments.social.hero;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.api.social.HeroIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.social.HeroKey;
import com.tradehero.th.persistence.social.HeroCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by xavier on 12/16/13.
 */
public class HeroManagerInfoFetcher
{
    public static final String TAG = HeroManagerInfoFetcher.class.getSimpleName();

    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected Lazy<HeroListCache> heroListCache;
    @Inject protected Lazy<HeroCache> heroCache;

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileFetchTask;
    private DTOCache.Listener<HeroKey, HeroIdExtWrapper> heroListListener;
    private DTOCache.GetOrFetchTask<HeroKey, HeroIdExtWrapper> heroListFetchTask;

    public HeroManagerInfoFetcher(DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener,
            DTOCache.Listener<HeroKey, HeroIdExtWrapper> heroListListener)
    {
        super();
        this.userProfileListener = userProfileListener;
        this.heroListListener = heroListListener;
        DaggerUtils.inject(this);
    }

    public void onPause()
    {
    }

    public void onDestroyView()
    {
        if (this.userProfileFetchTask != null)
        {
            this.userProfileFetchTask.setListener(null);
        }
        this.userProfileFetchTask = null;

        if (this.heroListFetchTask != null)
        {
            this.heroListFetchTask.setListener(null);
        }
        this.heroListFetchTask = null;

        setUserProfileListener(null);
        setHeroListListener(null);
    }

    public void setUserProfileListener(DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener)
    {
        this.userProfileListener = userProfileListener;
    }

    public void setHeroListListener(DTOCache.Listener<HeroKey, HeroIdExtWrapper> heroListListener)
    {
        this.heroListListener = heroListListener;
    }

    public void fetch(UserBaseKey userBaseKey, HeroType heroType)
    {
        fetchUserProfile(userBaseKey);
        fetchHeroes(userBaseKey,heroType);
    }

    public void fetchUserProfile(UserBaseKey userBaseKey)
    {
        if (this.userProfileFetchTask != null)
        {
            this.userProfileFetchTask.setListener(null);
        }
        this.userProfileFetchTask = this.userProfileCache.get().getOrFetch(userBaseKey, this.userProfileListener);
        this.userProfileFetchTask.execute();
    }

    public void fetchHeroes(UserBaseKey userBaseKey, HeroType heroType)
    {
        HeroKey heroKey = new HeroKey(userBaseKey,heroType);
        HeroIdExtWrapper heroIdExtWrapper = heroListCache.get().get(heroKey);
        HeroIdList heroIds = (heroIdExtWrapper != null)?heroIdExtWrapper.heroIdList:null;
        HeroDTOList heroDTOs = heroCache.get().get(heroIds);
        if (heroIds != null && heroDTOs != null && heroIds.size() == heroDTOs.size()) // We need this longer test in case DTO have been flushed.
        {
            Timber.d("fetchHeroes get the result and return %d heroType:%s",heroIds.size(),heroType);
            if (this.heroListListener != null)
            {
                this.heroListListener.onDTOReceived(heroKey, heroIdExtWrapper, true);
            }
        }
        else
        {
            Timber.d("fetchHeroes try to fetch heroType:%s",heroType);
            if (heroListFetchTask != null)
            {
                heroListFetchTask.setListener(null);
            }
            heroListFetchTask = heroListCache.get().getOrFetch(heroKey, heroListListener);
            heroListFetchTask.execute();
        }
    }
}
