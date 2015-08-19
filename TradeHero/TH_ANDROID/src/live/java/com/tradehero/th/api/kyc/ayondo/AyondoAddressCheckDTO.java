package com.tradehero.th.api.kyc.ayondo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class AyondoAddressCheckDTO implements DTO
{
    public final String guid;
    public final boolean isProofOfAddressRequired;

    public AyondoAddressCheckDTO(@JsonProperty("AddressCheckGuid") String guid,
            @JsonProperty("isProofOfAddressRequired") boolean isProofOfAddressRequired)
    {
        this.guid = guid;
        this.isProofOfAddressRequired = isProofOfAddressRequired;
    }

    @Override public String toString()
    {
        return "AyondoAddressCheckDTO{" +
                "guid='" + guid + '\'' +
                ", isProofOfAddressRequired=" + isProofOfAddressRequired +
                '}';
    }
}
