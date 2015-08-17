package com.tradehero.th.api.kyc.ayondo;

import com.tradehero.common.persistence.DTO;

public class AyondoAddressCheckDTO implements DTO
{
    public final String guid;
    public final boolean isProofOfAddressRequired;

    public AyondoAddressCheckDTO(String guid,
            boolean isProofOfAddressRequired)
    {
        this.guid = guid;
        this.isProofOfAddressRequired = isProofOfAddressRequired;
    }
}
