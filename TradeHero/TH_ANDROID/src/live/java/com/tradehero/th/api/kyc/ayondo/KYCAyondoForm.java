package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.StepStatus;
import com.tradehero.th.utils.DateUtils;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class KYCAyondoForm implements KYCForm
{
    public static final String KEY_AYONDO_TYPE = "Ayondo";
    public static final String DATE_FORMAT_AYONDO = "dd-MM-yyyy";

    @NonNull private Country country;
    @Nullable private String firstName;
    @Nullable private String lastName;
    @Nullable private String email;
    @Nullable private String verifiedEmail;
    @Nullable private Integer mobileNumberDialingPrefix;
    @Nullable private String mobileNumber;
    @Nullable private Integer verifiedMobileNumberDialingPrefix;
    @Nullable private String verifiedMobileNumber;
    @Nullable private CountryCode nationality;
    @Nullable private CountryCode residency;
    @Nullable private String dob;

    private List<StepStatus> stepStatuses;

    @Override @StringRes public int getBrokerName()
    {
        return R.string.broker_name_ayondo;
    }

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

        Date dob = scannedDocument.getDob();
        if (dob != null)
        {
            this.dob = DateUtils.getDisplayableDate(dob, KYCAyondoForm.DATE_FORMAT_AYONDO);
        }
    }

    @Override public void pickFrom(@NonNull KYCForm other)
    {
        this.country = other.getCountry();
        if (other instanceof KYCAyondoForm)
        {
            KYCAyondoForm ayondoForm = (KYCAyondoForm) other;
            this.firstName = ayondoForm.getFirstName() != null ? ayondoForm.getFirstName() : this.firstName;
            this.lastName = ayondoForm.getLastName() != null ? ayondoForm.getLastName() : this.lastName;
            this.email = ayondoForm.getEmail() != null ? ayondoForm.getEmail() : this.email;
            this.verifiedEmail = ayondoForm.getVerifiedEmail() != null ? ayondoForm.getVerifiedEmail() : this.verifiedEmail;
            this.mobileNumberDialingPrefix =
                    ayondoForm.getMobileNumberDialingPrefix() != null ? ayondoForm.getMobileNumberDialingPrefix() : this.mobileNumberDialingPrefix;
            this.mobileNumber = ayondoForm.getMobileNumber() != null ? ayondoForm.getMobileNumber() : this.mobileNumber;
            this.verifiedMobileNumberDialingPrefix =
                    ayondoForm.getVerifiedMobileNumberDialingPrefix() != null ? ayondoForm.getVerifiedMobileNumberDialingPrefix()
                            : this.verifiedMobileNumberDialingPrefix;
            this.verifiedMobileNumber =
                    ayondoForm.getVerifiedMobileNumber() != null ? ayondoForm.getVerifiedMobileNumber() : this.verifiedMobileNumber;
            this.nationality = ayondoForm.getNationality() != null ? ayondoForm.getNationality() : this.nationality;
            this.residency = ayondoForm.getResidency() != null ? ayondoForm.getResidency() : this.residency;
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

    @NonNull @Override public Country getCountry()
    {
        return country;
    }

    public void setCountry(@NonNull Country country)
    {
        this.country = country;
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

    @Nullable public Integer getMobileNumberDialingPrefix()
    {
        return mobileNumberDialingPrefix;
    }

    public void setMobileNumberDialingPrefix(@Nullable Integer mobileNumberDialingPrefix)
    {
        this.mobileNumberDialingPrefix = mobileNumberDialingPrefix;
    }

    @Nullable public String getMobileNumber()
    {
        return mobileNumber;
    }

    public void setMobileNumber(@Nullable String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    @Nullable public Integer getVerifiedMobileNumberDialingPrefix()
    {
        return verifiedMobileNumberDialingPrefix;
    }

    public void setVerifiedMobileNumberDialingPrefix(@Nullable Integer verifiedMobileNumberDialingPrefix)
    {
        this.verifiedMobileNumberDialingPrefix = verifiedMobileNumberDialingPrefix;
    }

    @Nullable public String getVerifiedMobileNumber()
    {
        return verifiedMobileNumber;
    }

    public void setVerifiedMobileNumber(@Nullable String verifiedMobileNumber)
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

    @Nullable public CountryCode getResidency()
    {
        return residency;
    }

    public void setResidency(@Nullable CountryCode residency)
    {
        this.residency = residency;
    }

    @Override public boolean hasSameFields(@NonNull KYCForm kycForm)
    {
        boolean same;
        if (kycForm instanceof KYCAyondoForm)
        {
            KYCAyondoForm ayondoForm = (KYCAyondoForm) kycForm;
            same = country.equals(ayondoForm.country);
            same &= firstName == null ? ayondoForm.firstName == null : firstName.equals(ayondoForm.firstName);
            same &= lastName == null ? ayondoForm.lastName == null : lastName.equals(ayondoForm.lastName);
            same &= email == null ? ayondoForm.email == null : email.equals(ayondoForm.email);
            same &= verifiedEmail == null ? ayondoForm.verifiedEmail == null : verifiedEmail.equals(ayondoForm.verifiedEmail);
            same &= mobileNumberDialingPrefix == null ? ayondoForm.mobileNumberDialingPrefix
                    == null : mobileNumberDialingPrefix.equals(ayondoForm.mobileNumberDialingPrefix);
            same &= mobileNumber == null ? ayondoForm.mobileNumber == null : mobileNumber.equals(ayondoForm.mobileNumber);
            same &= verifiedMobileNumberDialingPrefix == null ? ayondoForm.verifiedMobileNumberDialingPrefix == null
                    : verifiedMobileNumberDialingPrefix
                            .equals(ayondoForm.verifiedMobileNumberDialingPrefix);
            same &= verifiedMobileNumber == null ? ayondoForm.verifiedMobileNumber == null : verifiedMobileNumber.equals(
                    ayondoForm.verifiedMobileNumber);
            same &= nationality == null ? ayondoForm.nationality == null : nationality.equals(ayondoForm.nationality);
            same &= residency == null ? ayondoForm.residency == null : residency.equals(ayondoForm.residency);
            same &= dob == null ? ayondoForm.dob == null : dob.equals(ayondoForm.dob);
            same &= stepStatuses == null ? ayondoForm.stepStatuses == null
                    : (ayondoForm.stepStatuses != null && stepStatuses.size() == ayondoForm.stepStatuses.size());
            if (same && stepStatuses != null && ayondoForm.stepStatuses != null)
            {
                for (int index = 0; index < stepStatuses.size(); index++)
                {
                    same &= stepStatuses.get(index).equals(ayondoForm.stepStatuses.get(index));
                }
            }
        }
        else
        {
            same = false;
        }
        return same;
    }

    public String getDob()
    {
        return dob;
    }

    public void setDateOfBirth(String dob)
    {
        this.dob = dob;
    }
}