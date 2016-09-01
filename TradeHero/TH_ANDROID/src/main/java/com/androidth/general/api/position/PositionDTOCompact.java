package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.api.BaseResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.utils.SecurityUtils;

public class PositionDTOCompact extends BaseResponseDTO implements DTO
{
    public int id;
    @Nullable public String exchange;
    @Nullable public String symbol;
    @Nullable public Integer shares;
    @Nullable public Integer portfolioId;

    // This price is always in the portfolio currency
    @Nullable public Double averagePriceRefCcy;

    // This price is always in the security currency
    @Nullable public Double averagePriceSecCcy;

    // This is the portfolio currency
    @Nullable public String currencyDisplay;
    @Nullable public String currencyISO;

    @Nullable public Double fxRate;
    @Nullable public PositionStatus positionStatus;

    public boolean isShort;

    //<editor-fold desc="Constructors">
    public PositionDTOCompact()
    {
        super();
    }
    //</editor-fold>

    @JsonIgnore @Nullable
    public Boolean isClosed()
    {
        if (positionStatus != null &&
                (positionStatus.equals(PositionStatus.CLOSED)
                        || positionStatus.equals(PositionStatus.FORCE_CLOSED)))
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
        if (positionStatus != null &&
                (positionStatus.equals(PositionStatus.CLOSED)
                        || positionStatus.equals(PositionStatus.FORCE_CLOSED)))
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

    @Override public String toString()
    {
        return "PositionDTOCompact{" +
                "id=" + id +
                ", shares=" + shares +
                ", portfolioId=" + portfolioId +
                ", averagePriceRefCcy=" + averagePriceRefCcy +
                ", currencyDisplay=" + currencyDisplay +
                ", currencyISO=" + currencyISO +
                ", isShort=" + isShort +
                '}';
    }
}
