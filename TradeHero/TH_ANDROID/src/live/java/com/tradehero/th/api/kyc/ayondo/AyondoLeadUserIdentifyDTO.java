package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.fastfill.Gender;
import com.tradehero.th.models.fastfill.IdentityScannedDocumentType;
import java.util.Date;

public class AyondoLeadUserIdentifyDTO
{
    @JsonProperty("DateOfBirth") @Nullable private Date dob;
    @JsonProperty("FirstName") @Nullable private String firstName;
    @JsonProperty("LastName") @Nullable private String lastName;
    @JsonProperty("MiddleName") @Nullable private String middleName;
    @JsonProperty("Gender") @Nullable private AyondoGender ayondoGender;
    @JsonProperty("IdentificationDocument") @Nullable private AyondoIdentityDocumentType identificationDocument;
    @JsonProperty("IdentificationNumber") @Nullable private String identificationNumber;
    @JsonProperty("Nationality") @Nullable private Country nationality;

    public AyondoLeadUserIdentifyDTO()
    {
    }

    @Nullable public Date getDob()
    {
        return dob;
    }

    public void setDob(@Nullable Date dob)
    {
        this.dob = dob;
    }

    @Nullable public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(@Nullable String firstName)
    {
        this.firstName = firstName;
    }

    @Nullable public String getLastName()
    {
        return lastName;
    }

    public void setLastName(@Nullable String lastName)
    {
        this.lastName = lastName;
    }

    @Nullable public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName(@Nullable String middleName)
    {
        this.middleName = middleName;
    }

    @Nullable public AyondoGender getAyondoGender()
    {
        return ayondoGender;
    }

    public void setAyondoGender(@Nullable AyondoGender ayondoGender)
    {
        this.ayondoGender = ayondoGender;
    }

    @Nullable public Gender getGender()
    {
        return ayondoGender == null ? null : ayondoGender.gender;
    }

    public void setGender(@Nullable Gender gender)
    {
        this.ayondoGender = gender == null ? null : AyondoGender.getAyondoGender(gender);
    }

    @Nullable public AyondoIdentityDocumentType getIdentificationDocument()
    {
        return identificationDocument;
    }

    public void setIdentificationDocument(@Nullable AyondoIdentityDocumentType identificationDocument)
    {
        this.identificationDocument = identificationDocument;
    }

    @Nullable public IdentityScannedDocumentType getIdentityScannedDocumentType()
    {
        return identificationDocument == null ? null : identificationDocument.scannedDocumentType;
    }

    public void setIdentityScannedDocumentType(@Nullable IdentityScannedDocumentType identityScannedDocumentType)
    {
        this.identificationDocument = identityScannedDocumentType == null
                ? null
                : AyondoIdentityDocumentType.getAyondoIdentityDocumentType(identityScannedDocumentType);
    }

    @Nullable public String getIdentificationNumber()
    {
        return identificationNumber;
    }

    public void setIdentificationNumber(@Nullable String identificationNumber)
    {
        this.identificationNumber = identificationNumber;
    }

    @Nullable public Country getNationality()
    {
        return nationality;
    }

    public void setNationality(@Nullable Country nationality)
    {
        this.nationality = nationality;
    }

    public boolean isValidToCreateAccount()
    {
        return dob != null
                && firstName != null
                && lastName != null
                // middleName can be null
                && ayondoGender != null
                && identificationDocument != null
                && identificationNumber != null
                && nationality != null;
    }
}