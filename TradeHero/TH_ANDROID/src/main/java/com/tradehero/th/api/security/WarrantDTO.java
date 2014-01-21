package com.tradehero.th.api.security;

import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:26 PM To change this template use File | Settings | File Templates. */
public class WarrantDTO extends SecurityCompactDTO
{
    public static final String TAG = WarrantDTO.class.getSimpleName();

    public String warrantType;
    public Date expiryDate;
    public Double strikePrice;
    public String strikePriceCcy;

    public String issuerName;
    public String underlyingName;
    public String externalAppURL;

    public String fallbackExternalURL;

    //<editor-fold desc="Constructors">
    public WarrantDTO()
    {
        super();
    }

    public WarrantDTO(SecurityCompactDTO other)
    {
        super(other);
        this.putAll(other.getAll(), WarrantDTO.class);
    }

    public WarrantDTO(WarrantDTO other)
    {
        super(other);
        this.warrantType = other.warrantType;
        this.expiryDate = other.expiryDate;
        this.strikePrice = other.strikePrice;
        this.strikePriceCcy = other.strikePriceCcy;
        this.issuerName = other.issuerName;
        this.underlyingName = other.underlyingName;
        this.externalAppURL = other.externalAppURL;
        this.fallbackExternalURL = other.fallbackExternalURL;
        this.putAll(other.getAll(), WarrantDTO.class);
    }
    //</editor-fold>

    public boolean isMacquarieWarrant()
    {
        return this.issuerName != null &&
                (this.issuerName.toLowerCase().contains("mb") || this.issuerName.toLowerCase().contains("mbl"));
    }
}
