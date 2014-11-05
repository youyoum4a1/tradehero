package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import android.support.annotation.NonNull;

public class HeroDTOExtWrapper implements DTO
{
    @NonNull public final HeroDTOList allActiveHeroes;
    @NonNull public final HeroDTOList activeFreeHeroes;
    @NonNull public final HeroDTOList activePremiumHeroes;

    //<editor-fold desc="Constructors">
    public HeroDTOExtWrapper(@NonNull HeroDTOList heroDTOs)
    {
        activeFreeHeroes = heroDTOs.getFreeActiveHeroIds();
        activePremiumHeroes = heroDTOs.getPremiumActiveHeroIds();
        allActiveHeroes = heroDTOs.getAllActiveHeroIds();
    }
    //</editor-fold>

    public boolean hasNullItem()
    {
        return allActiveHeroes.hasNullItem()
                || activeFreeHeroes.hasNullItem()
                || activePremiumHeroes.hasNullItem();
    }

    public int getActiveFreeHeroesCount()
    {
        return activeFreeHeroes.size();
    }

    public int getActivePremiumHeroesCount()
    {
        return activePremiumHeroes.size();
    }
}
