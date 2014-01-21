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

    public boolean isMacquarieWarrant()
    {
        return this.issuerName != null &&
                (this.issuerName.toLowerCase().contains("mb") || this.issuerName.toLowerCase().contains("mbl"));
    }
}
