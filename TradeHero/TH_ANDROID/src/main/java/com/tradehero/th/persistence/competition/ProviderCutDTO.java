package com.tradehero.th.persistence.competition;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProviderCutDTO extends ProviderCompactCutDTO
{
    public Date startDateUtc;
    public Date endDateUtc;
    public String durationType;
    public String totalPrize;
    @Nullable public Boolean vip;

    public ProviderCutDTO(
            @NotNull ProviderDTO providerDTO,
            @NotNull PortfolioCompactCache portfolioCompactCache)
    {
        super(providerDTO, portfolioCompactCache);
        this.startDateUtc = providerDTO.startDateUtc;
        this.endDateUtc = providerDTO.endDateUtc;
        this.durationType = providerDTO.durationType;
        this.totalPrize = providerDTO.totalPrize;
        this.vip = providerDTO.vip;
    }

    @Nullable @Override ProviderDTO create(@NotNull PortfolioCompactCache portfolioCompactCache)
    {
        return populate(new ProviderDTO(), portfolioCompactCache);
    }

    @Nullable <T extends ProviderDTO> T populate(@NotNull T empty, @NotNull PortfolioCompactCache portfolioCompactCache)
    {
        T fromParent = super.populate(empty, portfolioCompactCache);
        if (fromParent == null)
        {
            return null;
        }
        fromParent.startDateUtc = this.startDateUtc;
        fromParent.endDateUtc = this.endDateUtc;
        fromParent.durationType = this.durationType;
        fromParent.totalPrize = this.totalPrize;
        fromParent.vip = this.vip;

        return fromParent;
    }
}
