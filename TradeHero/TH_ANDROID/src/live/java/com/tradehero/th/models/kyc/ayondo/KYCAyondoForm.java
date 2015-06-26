package com.tradehero.th.models.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.StepStatus;
import java.util.Collections;
import java.util.List;

public class KYCAyondoForm implements KYCForm
{
    public static final String KEY_AYONDO_TYPE = "Ayondo";

    @Nullable private String firstName;
    @Nullable private String lastName;
    @Nullable private String email;
    @Nullable private String verifiedEmail;
    @JsonProperty("mobileCC")
    @Nullable private Integer mobileNumberCountryCode;
    @Nullable private Integer mobileNumber;
    @JsonProperty("verifiedMobileCC")
    @Nullable private Integer verifiedMobileNumberCountryCode;
    @Nullable private Integer verifiedMobileNumber;
    @Nullable private CountryCode nationality;

    private List<StepStatus> stepStatuses;

    @Override public void pickFrom(@NonNull ScannedDocument scannedDocument)
    {
        String firstName = scannedDocument.getFirstName();
        if (firstName != null)
        {
            this.firstName = firstName;
        }

        String lastName = scannedDocument.getLastName();
        if (lastName != null)
        {
            this.lastName = lastName;
        }

        CountryCode issuingCountry = scannedDocument.getIssuingCountry();
        if (issuingCountry != null)
        {
            this.nationality = issuingCountry;
        }
    }

    @Override public void setStepStatuses(@NonNull List<StepStatus> stepStatuses)
    {
        this.stepStatuses = stepStatuses;
    }

    @JsonIgnore
    public List<StepStatus> getStepStatuses()
    {
        return Collections.unmodifiableList(stepStatuses);
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

    @Nullable public String getEmail()
    {
        return email;
    }

    public void setEmail(@Nullable String email)
    {
        this.email = email;
    }

    @Nullable String getVerifiedEmail()
    {
        return verifiedEmail;
    }

    public void setVerifiedEmail(@Nullable String verifiedEmail)
    {
        this.verifiedEmail = verifiedEmail;
    }

    @Nullable public Integer getMobileNumberCountryCode()
    {
        return mobileNumberCountryCode;
    }

    public void setMobileNumberCountryCode(@Nullable Integer mobileNumberCountryCode)
    {
        this.mobileNumberCountryCode = mobileNumberCountryCode;
    }

    @Nullable public Integer getMobileNumber()
    {
        return mobileNumber;
    }

    public void setMobileNumber(@Nullable Integer mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    @Nullable public Integer getVerifiedMobileNumberCountryCode()
    {
        return verifiedMobileNumberCountryCode;
    }

    public void setVerifiedMobileNumberCountryCode(@Nullable Integer verifiedMobileNumberCountryCode)
    {
        this.verifiedMobileNumberCountryCode = verifiedMobileNumberCountryCode;
    }

    @Nullable public Integer getVerifiedMobileNumber()
    {
        return verifiedMobileNumber;
    }

    public void setVerifiedMobileNumber(@Nullable Integer verifiedMobileNumber)
    {
        this.verifiedMobileNumber = verifiedMobileNumber;
    }

    @Nullable public CountryCode getNationality()
    {
        return nationality;
    }

    public void setNationality(@Nullable CountryCode nationality)
    {
        this.nationality = nationality;
    }
}
