package com.ayondo.academy.api.social;

import com.tradehero.common.persistence.DTO;

/**
 * This is just the identifier for a ReferralCode share request.
 */
public class ReferralCodeDTO implements DTO
{
    public String referralCode;

    public ReferralCodeDTO(String referralCode)
    {
        super();
        this.referralCode = referralCode;
    }
}
