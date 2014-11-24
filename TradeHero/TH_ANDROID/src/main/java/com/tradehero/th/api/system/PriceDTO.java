package com.tradehero.th.api.system;

import com.tradehero.common.persistence.DTO;

public class PriceDTO implements DTO
{
    public double priceRefCcy;
    public String currencyDisplay;
    public String currencyISO;

    //<editor-fold desc="Constructors">
    PriceDTO()
    {
    }

    public PriceDTO(double priceRefCcy, String currencyDisplay, String currencyISO)
    {
        this.priceRefCcy = priceRefCcy;
        this.currencyDisplay = currencyDisplay;
        this.currencyISO = currencyISO;
    }
    //</editor-fold>
}
