package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.market.Country;

public class AyondoLeadAddressDTO extends AyondoLeadUserIdentifyDTO
{
    @JsonProperty("PhonePrimary") @Nullable private String phonePrimary;
    @JsonProperty("AddressCity") @Nullable private String addressCity;
    @JsonProperty("AddressCountry") @Nullable private Country addressCountry;
    @JsonProperty("AddressLine1") @Nullable private String addressLine1;
    @JsonProperty("AddressLine2") @Nullable private String addressLine2;
    @JsonProperty("AddressZip") @Nullable private String addressZip;
    @JsonProperty("PreviousAddressCity") @Nullable private String previousAddressCity;
    @JsonProperty("PreviousAddressCountry") @Nullable private Country previousAddressCountry;
    @JsonProperty("PreviousAddressLine1") @Nullable private String previousAddressLine1;
    @JsonProperty("PreviousAddressLine2") @Nullable private String previousAddressLine2;
    @JsonProperty("PreviousAddressZip") @Nullable private String previousAddressZip;

    public AyondoLeadAddressDTO()
    {
    }

    @Nullable public String getPhonePrimary()
    {
        return phonePrimary;
    }

    public void setPhonePrimary(@Nullable String phonePrimary)
    {
        this.phonePrimary = phonePrimary;
    }

    @Nullable public String getAddressCity()
    {
        return addressCity;
    }

    public void setAddressCity(@Nullable String addressCity)
    {
        this.addressCity = addressCity;
    }

    @Nullable public Country getAddressCountry()
    {
        return addressCountry;
    }

    public void setAddressCountry(@Nullable Country addressCountry)
    {
        this.addressCountry = addressCountry;
    }

    @Nullable public String getAddressLine1()
    {
        return addressLine1;
    }

    public void setAddressLine1(@Nullable String addressLine1)
    {
        this.addressLine1 = addressLine1;
    }

    @Nullable public String getAddressLine2()
    {
        return addressLine2;
    }

    public void setAddressLine2(@Nullable String addressLine2)
    {
        this.addressLine2 = addressLine2;
    }

    @Nullable public String getAddressZip()
    {
        return addressZip;
    }

    public void setAddressZip(@Nullable String addressZip)
    {
        this.addressZip = addressZip;
    }

    @Nullable public String getPreviousAddressCity()
    {
        return previousAddressCity;
    }

    public void setPreviousAddressCity(@Nullable String previousAddressCity)
    {
        this.previousAddressCity = previousAddressCity;
    }

    @Nullable public Country getPreviousAddressCountry()
    {
        return previousAddressCountry;
    }

    public void setPreviousAddressCountry(@Nullable Country previousAddressCountry)
    {
        this.previousAddressCountry = previousAddressCountry;
    }

    @Nullable public String getPreviousAddressLine1()
    {
        return previousAddressLine1;
    }

    public void setPreviousAddressLine1(@Nullable String previousAddressLine1)
    {
        this.previousAddressLine1 = previousAddressLine1;
    }

    @Nullable public String getPreviousAddressLine2()
    {
        return previousAddressLine2;
    }

    public void setPreviousAddressLine2(@Nullable String previousAddressLine2)
    {
        this.previousAddressLine2 = previousAddressLine2;
    }

    @Nullable public String getPreviousAddressZip()
    {
        return previousAddressZip;
    }

    public void setPreviousAddressZip(@Nullable String previousAddressZip)
    {
        this.previousAddressZip = previousAddressZip;
    }

    @Override public boolean isValidToCreateAccount()
    {
        return super.isValidToCreateAccount()
                && phonePrimary != null
                && addressCity != null
                && addressCountry != null
                && addressLine1 != null
                // addressLine2 can be null
                && addressZip != null
                // Previous address can be null
                ;
    }
}
