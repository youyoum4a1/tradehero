package com.androidth.general.api.kyc.ayondo;

import com.androidth.general.common.persistence.DTO;

public class AyondoIDCheckDTO implements DTO
{
    public String guid;
    public boolean isProofOfIdentificationRequired;

    public AyondoIDCheckDTO()
    {
        super();
    }
}
