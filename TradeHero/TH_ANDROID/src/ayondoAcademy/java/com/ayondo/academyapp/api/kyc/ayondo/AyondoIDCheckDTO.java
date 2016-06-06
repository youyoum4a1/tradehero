package com.ayondo.academyapp.api.kyc.ayondo;

import com.ayondo.academyapp.common.persistence.DTO;

public class AyondoIDCheckDTO implements DTO
{
    public String guid;
    public boolean isProofOfIdentificationRequired;

    public AyondoIDCheckDTO()
    {
        super();
    }
}
