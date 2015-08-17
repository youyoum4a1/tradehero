package com.tradehero.th.api.kyc.ayondo;

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
}