package com.tradehero.th.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.List;

public class PositionDTOCompact implements DTO
{
    public int id;
    @NonNull public String exchange;
    @NonNull public String symbol;
    @Nullable public Integer shares;
    public int portfolioId;

    // This price is always in the portfolio currency
    public Double averagePriceRefCcy;
    // This is the portfolio currency
    @Nullable public String currencyDisplay;
    @Nullable public String currencyISO;
    @Nullable public Double fxRate;
    @Nullable public PositionStatus positionStatus;

    //<editor-fold desc="Constructors">
    public PositionDTOCompact()
    {
        super();
    }
    //</editor-fold>

    @JsonIgnore @Nullable
    public Boolean isClosed()
    {
        if (positionStatus != null && positionStatus.equals(PositionStatus.CLOSED))
        {
            return true;
        }
        if (shares == null)
        {
            return null;
        }
        return shares == 0;
    }

    @JsonIgnore @Nullable
    public Boolean isOpen()
    {
        if (positionStatus != null && positionStatus.equals(PositionStatus.CLOSED))
        {
            return false;
        }
        if (shares == null)
        {
            return null;
        }
        return shares != 0;
    }

    @JsonIgnore @NonNull
    public PositionCompactId getPositionCompactId()
    {
        return new PositionCompactId(id);
    }

    @JsonIgnore @NonNull
    public String getNiceCurrency()
    {
        if (currencyDisplay != null && !currencyDisplay.isEmpty())
        {
            return currencyDisplay;
        }
        return SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    @Nullable
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
