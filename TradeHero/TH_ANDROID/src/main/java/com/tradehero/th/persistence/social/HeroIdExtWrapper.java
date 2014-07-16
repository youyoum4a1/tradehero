package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.social.FollowerHeroRelationIdList;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;

class HeroIdExtWrapper implements DTO
{
    @NotNull public final FollowerHeroRelationIdList allActiveHeroes;
    @NotNull public final FollowerHeroRelationIdList activeFreeHeroes;
    @NotNull public final FollowerHeroRelationIdList activePremiumHeroes;

    //<editor-fold desc="Constructors">
    public HeroIdExtWrapper(
            @NotNull HeroDTOExtWrapper heroDTOExtWrapper,
            @NotNull UserBaseKey followerId)
    {
        this(heroDTOExtWrapper.allActiveHeroes.createKeys(followerId),
                heroDTOExtWrapper.activeFreeHeroes.createKeys(followerId),
                heroDTOExtWrapper.activePremiumHeroes.createKeys(followerId));
    }

    public HeroIdExtWrapper(
            @NotNull FollowerHeroRelationIdList allActiveHeroes,
            @NotNull FollowerHeroRelationIdList activeFreeHeroes,
            @NotNull FollowerHeroRelationIdList activePremiumHeroes)
    {
        this.allActiveHeroes = allActiveHeroes;
        this.activeFreeHeroes = activeFreeHeroes;
        this.activePremiumHeroes = activePremiumHeroes;
    }
    //</editor-fold>
}
