package com.tradehero.th.api.kyc.ayondo;

import com.tradehero.common.persistence.DTO;

public class AyondoIDCheckDTO implements DTO
{
    public String guid;
    public boolean isProofOfIdentificationRequired;

    public AyondoIDCheckDTO()
    {
        super();
    }
}
