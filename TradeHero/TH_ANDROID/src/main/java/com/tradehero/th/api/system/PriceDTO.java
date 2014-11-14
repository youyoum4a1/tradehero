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
    //</editor-fold>
}
