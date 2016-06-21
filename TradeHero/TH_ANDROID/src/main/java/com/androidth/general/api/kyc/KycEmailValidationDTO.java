package com.androidth.general.api.kyc;

import com.androidth.general.common.persistence.DTO;

/**
 * Created by ayushnvijay on 6/20/16.
 */
public class KycEmailValidationDTO implements DTO {
    private  String email;
    private boolean isValid;
    public KycEmailValidationDTO(String email, boolean isValid){
        this.email = email;
        this.isValid = isValid;
    }

    public String getEmail() {
        return email;
    }

    public boolean isValid() {
        return isValid;
    }
}
