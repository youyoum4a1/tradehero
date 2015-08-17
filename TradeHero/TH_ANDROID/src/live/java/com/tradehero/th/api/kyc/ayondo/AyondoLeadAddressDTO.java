package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

public class AyondoLeadAddressDTO extends AyondoLeadUserIdentityDTO
{
    @JsonProperty("AddressCity") @Nullable private String addressCity;
    @JsonProperty("AddressCountry") @Nullable private CountryCode addressCountry;
    @JsonProperty("AddressLine1") @Nullable private String addressLine1;
    @JsonProperty("AddressLine2") @Nullable private String addressLine2;
    @JsonProperty("AddressZip") @Nullable private String addressZip;

    @JsonProperty("PhonePrimary") @Nullable private String mobileNumber;

    @JsonProperty("PreviousAddressCity") @Nullable private String previousAddressCity;
    @JsonProperty("PreviousAddressCountry") @Nullable private CountryCode previousAddressCountry;
    @JsonProperty("PreviousAddressLine1") @Nullable private String previousAddressLine1;
    @JsonProperty("PreviousAddressLine2") @Nullable private String previousAddressLine2;
    @JsonProperty("PreviousAddressZip") @Nullable private String previousAddressZip;

    public AyondoLeadAddressDTO()
    {
    }
}
