package com.ayondo.academy.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

public class AyondoLeadUserIdentityDTO
{
    @JsonProperty("DateOfBirth") @Nullable public final String dob;

    @JsonProperty("FirstName") @Nullable public final String firstName;
    @JsonProperty("LastName") @Nullable public final String lastName;
    @JsonProperty("MiddleName") @Nullable public final String middleName;

    @JsonProperty("Gender") @Nullable public final AyondoGender ayondoGender;

    @JsonProperty("Nationality") @Nullable public final CountryCode nationality;

    @JsonProperty("IdentificationDocument") @Nullable public final AyondoIdentityDocumentType identificationDocument;
    @JsonProperty("IdentificationNumber") @Nullable public final String identificationNumber;

    public AyondoLeadUserIdentityDTO(KYCAyondoForm kycAyondoForm)
    {
        this.dob = kycAyondoForm.getDob();
        this.firstName = kycAyondoForm.getFirstName();
        this.middleName = kycAyondoForm.getMiddleName();
        this.lastName = kycAyondoForm.getLastName();
        this.ayondoGender = kycAyondoForm.getAyondoGender();
        this.nationality = kycAyondoForm.getNationality();
        this.identificationDocument = kycAyondoForm.getIdentificationDocument();
        this.identificationNumber = kycAyondoForm.getIdentificationNumber();
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AyondoLeadUserIdentityDTO)) return false;

        AyondoLeadUserIdentityDTO that = (AyondoLeadUserIdentityDTO) o;

        if (dob != null ? !dob.equals(that.dob) : that.dob != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) return false;
        if (ayondoGender != that.ayondoGender) return false;
        if (nationality != that.nationality) return false;
        if (identificationDocument != that.identificationDocument) return false;
        return !(identificationNumber != null ? !identificationNumber.equals(that.identificationNumber) : that.identificationNumber != null);
    }
}