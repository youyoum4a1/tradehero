package com.tradehero.th.api.kyc.ayondo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class AyondoAddressCheckDTO implements DTO
{
    @JsonProperty("AddressCheckGuid") public String guid;
    @JsonProperty("isProofOfAddressRequired") public boolean isProofOfAddressRequired;

    public AyondoAddressCheckDTO()
    {
        super();
    }

    @Override public String toString()
    {
        return "AyondoAddressCheckDTO{" +
                "guid='" + guid + '\'' +
                ", isProofOfAddressRequired=" + isProofOfAddressRequired +
                '}';
    }
}
