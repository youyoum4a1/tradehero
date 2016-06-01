package com.ayondo.academy.api.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;

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

    public int getActiveFreeHeroesCount()
    {
        return activeFreeHeroes.size();
    }

    public int getActivePremiumHeroesCount()
    {
        return activePremiumHeroes.size();
    }
}
