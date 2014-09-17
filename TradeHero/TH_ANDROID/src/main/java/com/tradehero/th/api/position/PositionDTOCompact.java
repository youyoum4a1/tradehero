package com.tradehero.th.api.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.utils.SecurityUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PositionDTOCompact implements DTO
{
    public int id;
    @Nullable public Integer shares;
    public int portfolioId;

    // This price is always in the portfolio currency
    public Double averagePriceRefCcy;
    // This is the portfolio currency
    @Nullable public String currencyDisplay;
    @Nullable public String currencyISO;

    //<editor-fold desc="Constructors">
    public PositionDTOCompact()
    {
        super();
    }
    //</editor-fold>

    @JsonIgnore
    public Boolean isClosed()
    {
        if (shares == null)
        {
            return null;
        }
        return shares == 0;
    }

    @JsonIgnore
    public Boolean isOpen()
    {
        if (shares == null)
        {
            return null;
        }
        return shares != 0;
    }

    @JsonIgnore @NotNull
    public PositionCompactId getPositionCompactId()
    {
        return new PositionCompactId(id);
    }

    @JsonIgnore @NotNull
    public String getNiceCurrency()
    {
        if (currencyDisplay != null && !currencyDisplay.isEmpty())
        {
            return currencyDisplay;
        }
        return SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public static List<PositionCompactId> getPositionCompactIds(
            @Nullable List<PositionDTOCompact> positionDTOCompacts)
    {
        if (positionDTOCompacts == null)
        {
            return null;
        }

        List<PositionCompactId> positionCompactIds = new ArrayList<>();
        for (PositionDTOCompact positionDTOCompact: positionDTOCompacts)
        {
            positionCompactIds.add(positionDTOCompact.getPositionCompactId());
        }
        return positionCompactIds;
    }

    @Override public String toString()
    {
        return "PositionDTOCompact{" +
                "id=" + id +
                ", shares=" + shares +
                ", portfolioId=" + portfolioId +
                ", averagePriceRefCcy=" + averagePriceRefCcy +
                ", currencyDisplay=" + currencyDisplay +
                ", currencyISO=" + currencyISO +
                '}';
    }
}
