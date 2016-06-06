package com.androidth.general.api.kyc.ayondo;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

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
