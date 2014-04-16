package com.tradehero.th.api.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 3:27 PM To change this template use File | Settings | File Templates. */
public class PositionDTOCompact extends ExtendedDTO
{
    public int id;
    public Integer shares;
    public int portfolioId;

    // This price is always is USD
    public Double averagePriceRefCcy;
    public String currencyDisplay;
    public String currencyISO;

    //<editor-fold desc="Constructors">
    public PositionDTOCompact()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> PositionDTOCompact(ExtendedDTOType other, Class<? extends ExtendedDTO> myClass)
    {
        super(other, myClass);
    }

    public<PositionDTOCompactType extends PositionDTOCompact> PositionDTOCompact(PositionDTOCompactType other,
            Class<? extends PositionDTOCompact> myClass)
    {
        super(other, myClass);
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

    @JsonIgnore
    public PositionCompactId getPositionCompactId()
    {
        return new PositionCompactId(id);
    }

    @JsonIgnore
    public String getNiceCurrency()
    {
        if (currencyDisplay != null && !currencyDisplay.isEmpty())
        {
            return currencyDisplay;
        }
        return SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    public static List<PositionCompactId> getPositionCompactIds(List<PositionDTOCompact> positionDTOCompacts)
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
                ", extras={" + formatExtras(", ").toString() + "}" +
                '}';
    }
}
