package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.th.api.kyc.KYCAddress;
import java.util.List;

public class AyondoLeadAddressDTO extends AyondoLeadUserIdentityDTO
{
    @JsonProperty("AddressCity") @Nullable public final String addressCity;
    @JsonProperty("AddressCountry") @Nullable public final CountryCode addressCountry;
    @JsonProperty("AddressLine1") @Nullable public final String addressLine1;
    @JsonProperty("AddressLine2") @Nullable public final String addressLine2;
    @JsonProperty("AddressZip") @Nullable public final String addressZip;

    @JsonProperty("PhonePrimary") @Nullable public final String mobileNumber;

    @JsonProperty("PreviousAddressCity") @Nullable public final String previousAddressCity;
    @JsonProperty("PreviousAddressCountry") @Nullable public final CountryCode previousAddressCountry;
    @JsonProperty("PreviousAddressLine1") @Nullable public final String previousAddressLine1;
    @JsonProperty("PreviousAddressLine2") @Nullable public final String previousAddressLine2;
    @JsonProperty("PreviousAddressZip") @Nullable public final String previousAddressZip;

    public AyondoLeadAddressDTO(KYCAyondoForm kycAyondoForm)
    {
        super(kycAyondoForm);
        List<KYCAddress> kycAddresses = kycAyondoForm.getAddresses();
        if (kycAddresses != null && !kycAddresses.isEmpty())
        {
            KYCAddress priAddress = kycAddresses.get(0);
            this.addressCity = priAddress.city;
            this.addressCountry = priAddress.country;
            this.addressLine1 = priAddress.addressLine1;
            this.addressLine2 = priAddress.addressLine2;
            this.addressZip = priAddress.postalCode;
            if (kycAddresses.size() > 1)
            {
                KYCAddress secAddress = kycAddresses.get(1);
                this.previousAddressCity = secAddress.city;
                this.previousAddressCountry = secAddress.country;
                this.previousAddressLine1 = secAddress.addressLine1;
                this.previousAddressLine2 = secAddress.addressLine2;
                this.previousAddressZip = secAddress.postalCode;
            }
            else
            {
                this.previousAddressCity = null;
                this.previousAddressCountry = null;
                this.previousAddressLine1 = null;
                this.previousAddressLine2 = null;
                this.previousAddressZip = null;
            }
        }
        else
        {
            this.addressCity = null;
            this.addressCountry = null;
            this.addressLine1 = null;
            this.addressLine2 = null;
            this.addressZip = null;
            this.previousAddressCity = null;
            this.previousAddressCountry = null;
            this.previousAddressLine1 = null;
            this.previousAddressLine2 = null;
            this.previousAddressZip = null;
        }
        this.mobileNumber = kycAyondoForm.getVerifiedMobileNumber();
    }
}
