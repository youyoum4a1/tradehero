package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.UserBaseKey;

public class HeroIdExtWrapper implements DTO
{
    public HeroIdList allActiveHeroes;
    public HeroIdList activeFreeHeroes;
    public HeroIdList activePremiumHeroes;

    //<editor-fold desc="Constructors">
    public HeroIdExtWrapper()
    {
        super();
    }

    public HeroIdExtWrapper(UserBaseKey followerId, HeroDTOList heroDTOs)
    {
        activeFreeHeroes = heroDTOs.getFreeActiveHeroIds(followerId);
        activePremiumHeroes = heroDTOs.getPremiumActiveHeroIds(followerId);
        allActiveHeroes = heroDTOs.getAllActiveHeroIds(followerId);
    }
    //</editor-fold>

    public int getActiveFreeHeroesCount()
    {
        return activeFreeHeroes.size();
    }

    public int getActivePremiumHeroesCount()
    {
        return activePremiumHeroes.size();
    }
}
