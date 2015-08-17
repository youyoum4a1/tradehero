package com.tradehero.th.api.kyc.ayondo;

import com.tradehero.common.persistence.DTO;

public class AyondoIDCheckDTO implements DTO
{
    public final String guid;
    public final boolean isProofOfIdentificationRequired;

    public AyondoIDCheckDTO(String guid,
            boolean isProofOfIdentificationRequired)
    {
        this.guid = guid;
        this.isProofOfIdentificationRequired = isProofOfIdentificationRequired;
    }
}
