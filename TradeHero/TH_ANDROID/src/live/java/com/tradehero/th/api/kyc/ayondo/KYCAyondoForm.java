package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.AnnualIncomeRange;
import com.tradehero.th.api.kyc.EmploymentStatus;
import com.tradehero.th.api.kyc.KYCAddress;
import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.NetWorthRange;
import com.tradehero.th.api.kyc.PercentNetWorthForInvestmentRange;
import com.tradehero.th.api.kyc.StepStatus;
import com.tradehero.th.api.kyc.TradingPerQuarter;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.fastfill.IdentityScannedDocumentType;
import com.tradehero.th.models.fastfill.ResidenceScannedDocumentType;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.utils.DateUtils;
import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class KYCAyondoForm implements KYCForm
{
    public static final String KEY_AYONDO_TYPE = "AYD";
    public static final String DATE_FORMAT_AYONDO = "dd-MM-yyyy";

    @NonNull private Country country;
    @Nullable private String fullName;
    @Nullable private String email;
    @Nullable private String verifiedEmail;
    @Nullable private Integer mobileNumberDialingPrefix;
    @Nullable private String mobileNumber;
    @Nullable private Integer verifiedMobileNumberDialingPrefix;
    @Nullable private String verifiedMobileNumber;
    @Nullable private CountryCode nationality;
    @Nullable private CountryCode residency;
    @Nullable private String dob;
    @Nullable private AnnualIncomeRange annualIncomeRange;
    @Nullable private NetWorthRange netWorthRange;
    @Nullable private PercentNetWorthForInvestmentRange percentNetWorthForInvestmentRange;
    @Nullable private EmploymentStatus employmentStatus;
    @Nullable private Boolean employerRegulatedFinancial;
    @Nullable private Boolean workedInFinance1Year;
    @Nullable private Boolean attendedSeminarAyondo;
    @Nullable private Boolean haveOtherQualification;
    @Nullable private TradingPerQuarter tradingPerQuarter;
    @Nullable private Boolean tradedSharesBonds;
    @Nullable private Boolean tradedOtcDerivative;
    @Nullable private Boolean tradedEtc;
    @Nullable private List<KYCAddress> addresses;
    @Nullable private IdentityScannedDocumentType identityDocumentType;
    @Nullable private File identityDocumentFile;
    @Nullable private ResidenceScannedDocumentType residenceDocumentType;
    @Nullable private File residenceDocumentFile;
    @Nullable private Boolean agreeTermsConditions;
    @Nullable private Boolean agreeRisksWarnings;
    @Nullable private Boolean agreeDataSharing;
    @Nullable private Boolean agreeFundsPolicy;

    private List<StepStatus> stepStatuses;

    @Override @StringRes public int getBrokerName()
    {
        return R.string.broker_name_ayondo;
    }

    @Override public void pickFrom(@NonNull ScannedDocument scannedDocument)
    {
        String fullName = scannedDocument.getFullName();
        if (fullName != null)
        {
            this.fullName = fullName;
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
            this.fullName = ayondoForm.getFullName() != null ? ayondoForm.getFullName() : this.fullName;
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
            this.dob = ayondoForm.getDob() != null ? ayondoForm.getDob() : this.dob;
            this.annualIncomeRange = ayondoForm.getAnnualIncomeRange() != null ? ayondoForm.getAnnualIncomeRange() : this.annualIncomeRange;
            this.netWorthRange = ayondoForm.getNetWorthRange() != null ? ayondoForm.getNetWorthRange() : this.netWorthRange;
            this.percentNetWorthForInvestmentRange = ayondoForm.getPercentNetWorthForInvestmentRange() != null
                    ? ayondoForm.getPercentNetWorthForInvestmentRange()
                    : this.percentNetWorthForInvestmentRange;
            this.employmentStatus = ayondoForm.getEmploymentStatus() != null ? ayondoForm.getEmploymentStatus() : this.employmentStatus;
            this.employerRegulatedFinancial = ayondoForm.isEmployerRegulatedFinancial() != null ? ayondoForm.isEmployerRegulatedFinancial() : this.employerRegulatedFinancial;
            this.workedInFinance1Year = ayondoForm.isWorkedInFinance1Year() != null ? ayondoForm.isWorkedInFinance1Year() : this.workedInFinance1Year;
            this.attendedSeminarAyondo = ayondoForm.isAttendedSeminarAyondo() != null ? ayondoForm.isAttendedSeminarAyondo() : this.attendedSeminarAyondo;
            this.haveOtherQualification = ayondoForm.isHaveOtherQualification() != null ? ayondoForm.isHaveOtherQualification() : this.haveOtherQualification;
            this.tradingPerQuarter = ayondoForm.getTradingPerQuarter() != null ? ayondoForm.getTradingPerQuarter() : this.tradingPerQuarter;
            this.tradedSharesBonds = ayondoForm.isTradedSharesBonds() != null ? ayondoForm.isTradedSharesBonds() : this.tradedSharesBonds;
            this.tradedOtcDerivative = ayondoForm.isTradedOtcDerivative() != null ? ayondoForm.isTradedOtcDerivative() : this.tradedOtcDerivative;
            this.tradedEtc = ayondoForm.isTradedEtc() != null ? ayondoForm.isTradedEtc() : this.tradedEtc;
            this.addresses = ayondoForm.getAddresses() != null ? ayondoForm.getAddresses() : this.addresses;
            this.identityDocumentType = ayondoForm.getIdentityDocumentType() != null ? ayondoForm.getIdentityDocumentType() : this.identityDocumentType;
            this.identityDocumentFile = ayondoForm.identityDocumentFile != null ? ayondoForm.identityDocumentFile : this.identityDocumentFile;
            this.residenceDocumentType = ayondoForm.getResidenceDocumentType() != null ? ayondoForm.getResidenceDocumentType() : this.residenceDocumentType;
            this.residenceDocumentFile = ayondoForm.residenceDocumentFile != null ? ayondoForm.residenceDocumentFile : this.residenceDocumentFile;
            this.agreeTermsConditions = ayondoForm.agreeTermsConditions != null ? ayondoForm.agreeTermsConditions : this.agreeTermsConditions;
            this.agreeRisksWarnings = ayondoForm.agreeRisksWarnings != null ? ayondoForm.agreeRisksWarnings : this.agreeRisksWarnings;
            this.agreeDataSharing = ayondoForm.agreeDataSharing != null ? ayondoForm.agreeDataSharing : this.agreeDataSharing;
            this.agreeFundsPolicy = ayondoForm.agreeFundsPolicy != null ? ayondoForm.agreeFundsPolicy : this.agreeFundsPolicy;
            if (other.getStepStatuses() != null)
            {
                this.stepStatuses = other.getStepStatuses();
            }
        }
    }

    @Override public void setStepStatuses(@NonNull List<StepStatus> stepStatuses)
    {
        this.stepStatuses = stepStatuses;
    }

    @NonNull
    public List<StepStatus> getStepStatuses()
    {
        if (stepStatuses != null)
        {
            return Collections.unmodifiableList(stepStatuses);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @NonNull @Override public Country getCountry()
    {
        return country;
    }

    public void setCountry(@NonNull Country country)
    {
        this.country = country;
    }

    @Nullable public String getFullName()
    {
        return fullName;
    }

    public void setFullName(@Nullable String fullName)
    {
        this.fullName = fullName;
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

    //<editor-fold desc="Phone Number">
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
    //</editor-fold>

    //<editor-fold desc="Countries">
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
    //</editor-fold>

    @Nullable public String getDob()
    {
        return dob;
    }

    public void setDob(@Nullable String dob)
    {
        this.dob = dob;
    }

    //<editor-fold desc="Wealth">
    @Nullable public AnnualIncomeRange getAnnualIncomeRange()
    {
        return annualIncomeRange;
    }

    public void setAnnualIncomeRange(@Nullable AnnualIncomeRange annualIncomeRange)
    {
        this.annualIncomeRange = annualIncomeRange;
    }

    @Nullable public NetWorthRange getNetWorthRange()
    {
        return netWorthRange;
    }

    public void setNetWorthRange(@Nullable NetWorthRange netWorthRange)
    {
        this.netWorthRange = netWorthRange;
    }

    @Nullable public PercentNetWorthForInvestmentRange getPercentNetWorthForInvestmentRange()
    {
        return percentNetWorthForInvestmentRange;
    }

    public void setPercentNetWorthForInvestmentRange(@Nullable PercentNetWorthForInvestmentRange percentNetWorthForInvestmentRange)
    {
        this.percentNetWorthForInvestmentRange = percentNetWorthForInvestmentRange;
    }
    //</editor-fold>

    //<editor-fold desc="Employment">
    @Nullable public EmploymentStatus getEmploymentStatus()
    {
        return employmentStatus;
    }

    public void setEmploymentStatus(@Nullable EmploymentStatus employmentStatus)
    {
        this.employmentStatus = employmentStatus;
    }

    @Nullable public Boolean isEmployerRegulatedFinancial()
    {
        return employerRegulatedFinancial;
    }

    public void setEmployerRegulatedFinancial(@Nullable Boolean employerRegulatedFinancial)
    {
        this.employerRegulatedFinancial = employerRegulatedFinancial;
    }
    //</editor-fold>

    //<editor-fold desc="Academic Knowledge">
    @Nullable public Boolean isWorkedInFinance1Year()
    {
        return workedInFinance1Year;
    }

    public void setWorkedInFinance1Year(@Nullable Boolean workedInFinance1Year)
    {
        this.workedInFinance1Year = workedInFinance1Year;
    }

    @Nullable public Boolean isAttendedSeminarAyondo()
    {
        return attendedSeminarAyondo;
    }

    public void setAttendedSeminarAyondo(@Nullable Boolean attendedSeminarAyondo)
    {
        this.attendedSeminarAyondo = attendedSeminarAyondo;
    }

    @Nullable public Boolean isHaveOtherQualification()
    {
        return haveOtherQualification;
    }

    public void setHaveOtherQualification(@Nullable Boolean haveOtherQualification)
    {
        this.haveOtherQualification = haveOtherQualification;
    }
    //</editor-fold>

    //<editor-fold desc="Experience">
    @Nullable public TradingPerQuarter getTradingPerQuarter()
    {
        return tradingPerQuarter;
    }

    public void setTradingPerQuarter(@Nullable TradingPerQuarter tradingPerQuarter)
    {
        this.tradingPerQuarter = tradingPerQuarter;
    }

    @Nullable public Boolean isTradedSharesBonds()
    {
        return tradedSharesBonds;
    }

    public void setTradedSharesBonds(@Nullable Boolean tradedSharesBonds)
    {
        this.tradedSharesBonds = tradedSharesBonds;
    }

    @Nullable public Boolean isTradedOtcDerivative()
    {
        return tradedOtcDerivative;
    }

    public void setTradedOtcDerivative(@Nullable Boolean tradedOtcDerivative)
    {
        this.tradedOtcDerivative = tradedOtcDerivative;
    }

    @Nullable public Boolean isTradedEtc()
    {
        return tradedEtc;
    }

    public void setTradedEtc(@Nullable Boolean tradedEtc)
    {
        this.tradedEtc = tradedEtc;
    }
    //</editor-fold>

    //<editor-fold desc="Addresses">
    @Nullable public List<KYCAddress> getAddresses()
    {
        return addresses;
    }
    //</editor-fold>

    //<editor-fold desc="Identity Document File">
    @Nullable public IdentityScannedDocumentType getIdentityDocumentType()
    {
        return identityDocumentType;
    }

    public void setIdentityDocumentType(@Nullable IdentityScannedDocumentType identityDocumentType)
    {
        this.identityDocumentType = identityDocumentType;
    }

    @Nullable @JsonIgnore public File getIdentityDocumentFile()
    {
        return identityDocumentFile;
    }

    @Nullable public String getIdentityDocumentFileString()
    {
        return identityDocumentFile == null
                ? null
                : identityDocumentFile.toURI().toString();
    }

    @JsonIgnore public void setIdentityDocumentFile(@Nullable File identityDocumentFile)
    {
        this.identityDocumentFile = identityDocumentFile;
    }

    public void setIdentityDocumentFileString(@Nullable String identityDocumentFileString)
    {
        this.identityDocumentFile = identityDocumentFileString == null
                ? null
                : new File(URI.create(identityDocumentFileString));
    }
    //</editor-fold>

    //<editor-fold desc="Residence Document File">
    @Nullable public ResidenceScannedDocumentType getResidenceDocumentType()
    {
        return residenceDocumentType;
    }

    public void setResidenceDocumentType(@Nullable ResidenceScannedDocumentType residenceDocumentType)
    {
        this.residenceDocumentType = residenceDocumentType;
    }

    @Nullable @JsonIgnore public File getResidenceDocumentFile()
    {
        return residenceDocumentFile;
    }

    @Nullable public String getResidenceDocumentFileString()
    {
        return residenceDocumentFile == null
                ? null
                : residenceDocumentFile.toURI().toString();
    }

    @JsonIgnore public void setResidenceDocumentFile(@Nullable File residenceDocumentFile)
    {
        this.residenceDocumentFile = residenceDocumentFile;
    }

    public void setResidenceDocumentFileString(@Nullable String residenceDocumentFileString)
    {
        this.residenceDocumentFile = residenceDocumentFileString == null
                ? null
                : new File(URI.create(residenceDocumentFileString));
    }
    //</editor-fold>

    //<editor-fold desc="Agreements">
    @Nullable public Boolean isAgreeTermsConditions()
    {
        return agreeTermsConditions;
    }

    public void setAgreeTermsConditions(@Nullable Boolean agreeTermsConditions)
    {
        this.agreeTermsConditions = agreeTermsConditions;
    }

    @Nullable public Boolean isAgreeRisksWarnings()
    {
        return agreeRisksWarnings;
    }

    public void setAgreeRisksWarnings(@Nullable Boolean agreeRisksWarnings)
    {
        this.agreeRisksWarnings = agreeRisksWarnings;
    }

    @Nullable public Boolean isAgreeDataSharing()
    {
        return agreeDataSharing;
    }

    public void setAgreeDataSharing(@Nullable Boolean agreeDataSharing)
    {
        this.agreeDataSharing = agreeDataSharing;
    }

    @Nullable public Boolean isAgreeFundsPolicy()
    {
        return agreeFundsPolicy;
    }

    public void setAgreeFundsPolicy(@Nullable Boolean agreeFundsPolicy)
    {
        this.agreeFundsPolicy = agreeFundsPolicy;
    }
    //</editor-fold>

    @Override public boolean hasSameFields(@NonNull KYCForm kycForm)
    {
        boolean same;
        if (kycForm instanceof KYCAyondoForm)
        {
            KYCAyondoForm ayondoForm = (KYCAyondoForm) kycForm;
            same = country.equals(ayondoForm.country);
            same &= fullName == null ? ayondoForm.fullName == null : fullName.equals(ayondoForm.fullName);
            same &= email == null ? ayondoForm.email == null : email.equals(ayondoForm.email);
            same &= verifiedEmail == null
                    ? ayondoForm.verifiedEmail == null
                    : verifiedEmail.equals(ayondoForm.verifiedEmail);
            same &= mobileNumberDialingPrefix == null
                    ? ayondoForm.mobileNumberDialingPrefix == null
                    : mobileNumberDialingPrefix.equals(ayondoForm.mobileNumberDialingPrefix);
            same &= mobileNumber == null ? ayondoForm.mobileNumber == null : mobileNumber.equals(ayondoForm.mobileNumber);
            same &= verifiedMobileNumberDialingPrefix == null
                    ? ayondoForm.verifiedMobileNumberDialingPrefix == null
                    : verifiedMobileNumberDialingPrefix.equals(ayondoForm.verifiedMobileNumberDialingPrefix);
            same &= verifiedMobileNumber == null
                    ? ayondoForm.verifiedMobileNumber == null
                    : verifiedMobileNumber.equals(ayondoForm.verifiedMobileNumber);
            same &= nationality == null ? ayondoForm.nationality == null : nationality.equals(ayondoForm.nationality);
            same &= residency == null ? ayondoForm.residency == null : residency.equals(ayondoForm.residency);
            same &= dob == null ? ayondoForm.dob == null : dob.equals(ayondoForm.dob);
            same &= annualIncomeRange == null ? ayondoForm.annualIncomeRange == null : annualIncomeRange.equals(ayondoForm.annualIncomeRange);
            same &= netWorthRange == null ? ayondoForm.netWorthRange == null : netWorthRange.equals(ayondoForm.netWorthRange);
            same &= percentNetWorthForInvestmentRange == null
                    ? ayondoForm.percentNetWorthForInvestmentRange == null
                    : percentNetWorthForInvestmentRange.equals(ayondoForm.percentNetWorthForInvestmentRange);
            same &= employmentStatus == null ? ayondoForm.employmentStatus == null : employmentStatus.equals(ayondoForm.employmentStatus);
            same &= employerRegulatedFinancial == null ? ayondoForm.employerRegulatedFinancial == null : employerRegulatedFinancial.equals(ayondoForm.employerRegulatedFinancial);
            same &= workedInFinance1Year == null ? ayondoForm.workedInFinance1Year == null : workedInFinance1Year.equals(ayondoForm.workedInFinance1Year);
            same &= attendedSeminarAyondo == null ? ayondoForm.attendedSeminarAyondo == null : attendedSeminarAyondo.equals(ayondoForm.attendedSeminarAyondo);
            same &= haveOtherQualification == null ? ayondoForm.haveOtherQualification == null : haveOtherQualification.equals(ayondoForm.haveOtherQualification);
            same &= tradingPerQuarter == null ? ayondoForm.tradingPerQuarter == null : tradingPerQuarter.equals(ayondoForm.tradingPerQuarter);
            same &= tradedSharesBonds == null ? ayondoForm.tradedSharesBonds == null : tradedSharesBonds.equals(ayondoForm.tradedSharesBonds);
            same &= tradedOtcDerivative == null ? ayondoForm.tradedOtcDerivative == null : tradedOtcDerivative.equals(ayondoForm.tradedOtcDerivative);
            same &= tradedEtc == null ? ayondoForm.tradedEtc == null : tradedEtc.equals(ayondoForm.tradedEtc);
            same &= addresses == null ? ayondoForm.addresses == null : addresses.equals(ayondoForm.addresses);
            same &= identityDocumentType == null ? ayondoForm.identityDocumentType
                    == null : identityDocumentType.equals(ayondoForm.identityDocumentType);
            same &= identityDocumentFile == null ? ayondoForm.identityDocumentFile == null : identityDocumentFile.equals(ayondoForm.identityDocumentFile);
            same &= residenceDocumentType == null ? ayondoForm.residenceDocumentType == null : residenceDocumentType.equals(ayondoForm.residenceDocumentType);
            same &= residenceDocumentFile == null ? ayondoForm.residenceDocumentFile == null : residenceDocumentFile.equals(ayondoForm.residenceDocumentFile);
            same &= agreeTermsConditions == null ? ayondoForm.agreeTermsConditions == null : agreeTermsConditions.equals(ayondoForm.agreeTermsConditions);
            same &= agreeRisksWarnings == null ? ayondoForm.agreeRisksWarnings == null : agreeRisksWarnings.equals(ayondoForm.agreeRisksWarnings);
            same &= agreeDataSharing == null ? ayondoForm.agreeDataSharing == null : agreeDataSharing.equals(ayondoForm.agreeDataSharing);
            same &= agreeFundsPolicy == null ? ayondoForm.agreeFundsPolicy == null : agreeFundsPolicy.equals(ayondoForm.agreeFundsPolicy);
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
}