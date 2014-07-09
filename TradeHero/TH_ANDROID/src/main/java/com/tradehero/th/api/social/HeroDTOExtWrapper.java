package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;

public class HeroDTOExtWrapper implements DTO
{
    @NotNull public final HeroDTOList allActiveHeroes;
    @NotNull public final HeroDTOList activeFreeHeroes;
    @NotNull public final HeroDTOList activePremiumHeroes;

    //<editor-fold desc="Constructors">
    public HeroDTOExtWrapper(
            @NotNull HeroDTOList allActiveHeroes,
            @NotNull HeroDTOList activeFreeHeroes,
            @NotNull HeroDTOList activePremiumHeroes)
    {
        this.allActiveHeroes = allActiveHeroes;
        this.activeFreeHeroes = activeFreeHeroes;
        this.activePremiumHeroes = activePremiumHeroes;
    }

    public HeroDTOExtWrapper(@NotNull HeroDTOList heroDTOs)
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
