package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

public class AyondoLeadUserIdentityDTO
{
    @JsonProperty("DateOfBirth") @Nullable private String dob;

    @JsonProperty("FirstName") @Nullable private String firstName;
    @JsonProperty("LastName") @Nullable private String lastName;
    @JsonProperty("MiddleName") @Nullable private String middleName;

    @JsonProperty("Gender") @Nullable private AyondoGender ayondoGender;

    @JsonProperty("Nationality") @Nullable private CountryCode nationality;

    @JsonProperty("IdentificationDocument") @Nullable private AyondoIdentityDocumentType identificationDocument;
    @JsonProperty("IdentificationNumber") @Nullable private String identificationNumber;

    public AyondoLeadUserIdentityDTO()
    {
    }
}