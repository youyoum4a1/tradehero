package com.tradehero.th.fragments.social.hero;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeroManagerInfoFetcher
{
    @NotNull protected final Lazy<UserProfileCache> userProfileCache;
    @NotNull protected final Lazy<HeroListCache> heroListCache;

    @Nullable private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    @Nullable private DTOCacheNew.Listener<UserBaseKey, HeroDTOExtWrapper> heroListListener;

    //<editor-fold desc="Constructors">
    @Inject public HeroManagerInfoFetcher(
            @NotNull Lazy<UserProfileCache> userProfileCache,
            @NotNull Lazy<HeroListCache> heroListCache)
    {
        super();
        this.userProfileCache = userProfileCache;
        this.heroListCache = heroListCache;
    }
    //</editor-fold>

    public void onDestroyView()
    {
        detachUserProfileCache();
        detachHeroListCache();
        setUserProfileListener(null);
        setHeroListListener(null);
    }

    protected void detachUserProfileCache()
    {
        if (userProfileListener != null)
        {
            userProfileCache.get().unregister(userProfileListener);
        }
    }

    protected void detachHeroListCache()
    {
        if (heroListListener != null)
        {
            heroListCache.get().unregister(heroListListener);
        }
    }

    public void setUserProfileListener(@Nullable
            DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileListener)
    {
        this.userProfileListener = userProfileListener;
    }

    public void setHeroListListener(@Nullable
            DTOCacheNew.Listener<UserBaseKey, HeroDTOExtWrapper> heroListListener)
    {
        this.heroListListener = heroListListener;
    }

    public void fetch(@NotNull UserBaseKey followerId)
    {
        fetchUserProfile(followerId);
        fetchHeroes(followerId);
    }

    public void fetchUserProfile(@NotNull UserBaseKey userBaseKey)
    {
        detachUserProfileCache();
        this.userProfileCache.get().register(userBaseKey, userProfileListener);
        this.userProfileCache.get().getOrFetchAsync(userBaseKey);
    }

    public void fetchHeroes(@NotNull UserBaseKey followerId)
    {
        fetchHeroes(followerId, false);
    }

    public void reloadHeroes(UserBaseKey followerId)
    {
        fetchHeroes(followerId, true);
    }

    protected void fetchHeroes(@NotNull UserBaseKey followerId, boolean force)
    {
        detachHeroListCache();
        heroListCache.get().register(followerId, heroListListener);
        heroListCache.get().getOrFetchAsync(followerId, force);
    }
}
