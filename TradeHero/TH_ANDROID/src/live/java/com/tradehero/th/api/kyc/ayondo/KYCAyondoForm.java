package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import com.tradehero.th.models.fastfill.Gender;
import com.tradehero.th.models.fastfill.IdentityScannedDocumentType;
import com.tradehero.th.models.fastfill.ResidenceScannedDocumentType;
import com.tradehero.th.models.fastfill.ScanReference;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.utils.DateUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class KYCAyondoForm implements KYCForm
{
    public static final String KEY_AYONDO_TYPE = "AYD";
    public static final String DATE_FORMAT_AYONDO = "yyyy-MM-dd";

    @Nullable private Country country;
    @Nullable private ScanReference scanReference;
    @JsonProperty("Gender") @Nullable private AyondoGender ayondoGender;
    @JsonIgnore @Deprecated @Nullable private String fullName;
    @JsonProperty("FirstName") @Nullable private String firstName;
    @JsonProperty("LastName") @Nullable private String lastName;
    @JsonProperty("MiddleName") @Nullable private String middleName;
    @JsonProperty("Email") @Nullable private String email;
    @Nullable private String verifiedEmail;
    @JsonProperty("PhonePrimaryCountryCode") @Nullable private Country phonePrimaryCountryCode;
    @JsonProperty("PhonePrimary") @Nullable private String mobileNumber;
    @Nullable private Integer verifiedMobileNumberDialingPrefix;
    @Nullable private String verifiedMobileNumber;
    @JsonProperty("Nationality") @Nullable private CountryCode nationality;
    @Nullable private CountryCode residency;
    @JsonProperty("DateOfBirth") @Nullable private String dob;
    @JsonProperty("AnnualIncome") @Nullable private AnnualIncomeRange annualIncomeRange;
    @JsonProperty("NetWorth") @Nullable private NetWorthRange netWorthRange;
    @JsonProperty("InvestmentPortfolio") @Nullable private PercentNetWorthForInvestmentRange percentNetWorthForInvestmentRange;
    @JsonProperty("EmploymentStatus") @Nullable private EmploymentStatus employmentStatus;
    @JsonProperty("IsEmployerRegulated") @Nullable private Boolean employerRegulatedFinancial;
    @JsonProperty("HasProfessionalExperience") @Nullable private Boolean workedInFinance1Year;
    @JsonProperty("HasAttendedTraining") @Nullable private Boolean attendedSeminarAyondo;
    @JsonProperty("HasOtherQualification") @Nullable private Boolean haveOtherQualification;
    @JsonProperty("NumberOfMarginTrades") @Nullable private TradingPerQuarter tradingPerQuarter;
    @JsonProperty("LeveragedProducts") @Nullable private AyondoLeveragedProductList leveragedProducts;
    @JsonProperty("AddressCity") @Nullable private String addressCity;
    @JsonProperty("AddressCountry") @Nullable private CountryCode addressCountry;
    @JsonProperty("AddressLine1") @Nullable private String addressLine1;
    @JsonProperty("AddressLine2") @Nullable private String addressLine2;
    @JsonProperty("AddressZip") @Nullable private String addressZip;
    @JsonProperty("PreviousAddressCity") @Nullable private String previousAddressCity;
    @JsonProperty("PreviousAddressCountry") @Nullable private CountryCode previousAddressCountry;
    @JsonProperty("PreviousAddressLine1") @Nullable private String previousAddressLine1;
    @JsonProperty("PreviousAddressLine2") @Nullable private String previousAddressLine2;
    @JsonProperty("PreviousAddressZip") @Nullable private String previousAddressZip;
    @JsonProperty("IdentificationDocument") @Nullable private AyondoIdentityDocumentType identificationDocument;
    @JsonProperty("IdentificationNumber") @Nullable private String identificationNumber;
    @Nullable private String identityDocumentUrl;
    @Nullable @JsonIgnore private Boolean clearIdentityDocumentUrl;
    @Nullable private ResidenceScannedDocumentType residenceDocumentType;
    @Nullable private String residenceDocumentUrl;
    @Nullable @JsonIgnore private Boolean clearResidenceDocumentUrl;
    @Nullable private Boolean agreeTermsConditions;
    @Nullable private Boolean agreeRisksWarnings;
    @Nullable private Boolean agreeDataSharing;
    @Nullable private Boolean needIdentityDocument;
    @Nullable private Boolean needResidencyDocument;

    @JsonProperty("Guid") @Nullable private String guid;
    @JsonProperty("AddressCheckGuid") @Nullable private String addressCheckUid;
    @JsonProperty("IdentityCheckGuid") @Nullable private String identityCheckUid;
    @JsonProperty("LeadGuid") @Nullable private String leadGuid;

    @JsonProperty("SubscribeOffers") @Nullable private Boolean subscribeOffers;
    @JsonProperty("SubscribeTradeNotifications") @Nullable private Boolean subscribeTradeNotifications;

    //TODO Hardcoded for now
    @JsonProperty("Language") private final String language = "EN";
    @JsonProperty("Currency") private final String currency = "USD";
    @JsonProperty("WhiteLabel") private final String whiteLabel = "AyondoMarketsTH";

    private List<StepStatus> stepStatuses;

    @Override @StringRes public int getBrokerNameResId()
    {
        return R.string.broker_name_ayondo;
    }

    @Override public void pickFrom(@NonNull ScannedDocument scannedDocument)
    {
        this.scanReference = scannedDocument.getScanReference();

        Gender gender = scannedDocument.getGender();
        if (gender != null)
        {
            this.setGender(gender);
        }

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

        String middleName = scannedDocument.getMiddleName();
        if (middleName != null)
        {
            this.middleName = middleName;
        }

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

        String identificationNumber = scannedDocument.getIdNumber();
        if(identificationNumber != null)
        {
            this.identificationNumber = identificationNumber;
        }

        Date dob = scannedDocument.getDob();
        if (dob != null)
        {
            this.dob = DateUtils.getDisplayableDate(dob, KYCAyondoForm.DATE_FORMAT_AYONDO);
        }
    }

    @Override public void pickFrom(@NonNull KYCForm other)
    {
        this.country = other.getCountry() != null ? other.getCountry() : this.country;
        if (other instanceof KYCAyondoForm)
        {
            KYCAyondoForm ayondoForm = (KYCAyondoForm) other;
            this.scanReference = ayondoForm.scanReference != null ? ayondoForm.scanReference : this.scanReference;
            this.ayondoGender = ayondoForm.ayondoGender != null ? ayondoForm.ayondoGender : this.ayondoGender;
            this.firstName = ayondoForm.firstName != null ? ayondoForm.firstName : this.firstName;
            this.lastName = ayondoForm.lastName != null ? ayondoForm.lastName : this.lastName;
            this.middleName = ayondoForm.middleName != null ? ayondoForm.middleName : this.middleName;
            this.email = ayondoForm.getEmail() != null ? ayondoForm.getEmail() : this.email;
            this.verifiedEmail = ayondoForm.getVerifiedEmail() != null ? ayondoForm.getVerifiedEmail() : this.verifiedEmail;
            this.phonePrimaryCountryCode =
                    ayondoForm.phonePrimaryCountryCode != null ? ayondoForm.phonePrimaryCountryCode : this.phonePrimaryCountryCode;
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
            this.employerRegulatedFinancial =
                    ayondoForm.isEmployerRegulatedFinancial() != null ? ayondoForm.isEmployerRegulatedFinancial() : this.employerRegulatedFinancial;
            this.workedInFinance1Year = ayondoForm.isWorkedInFinance1Year() != null ? ayondoForm.isWorkedInFinance1Year() : this.workedInFinance1Year;
            this.attendedSeminarAyondo =
                    ayondoForm.isAttendedSeminarAyondo() != null ? ayondoForm.isAttendedSeminarAyondo() : this.attendedSeminarAyondo;
            this.haveOtherQualification =
                    ayondoForm.isHaveOtherQualification() != null ? ayondoForm.isHaveOtherQualification() : this.haveOtherQualification;
            this.tradingPerQuarter = ayondoForm.getTradingPerQuarter() != null ? ayondoForm.getTradingPerQuarter() : this.tradingPerQuarter;
            this.leveragedProducts = ayondoForm.leveragedProducts != null ? ayondoForm.leveragedProducts : this.leveragedProducts;
            this.addressCity = ayondoForm.addressCity != null ? ayondoForm.addressCity : this.addressCity;
            this.addressCountry = ayondoForm.addressCountry != null ? ayondoForm.addressCountry : this.addressCountry;
            this.addressLine1 = ayondoForm.addressLine1 != null ? ayondoForm.addressLine1 : this.addressLine1;
            this.addressLine2 = ayondoForm.addressLine2 != null ? ayondoForm.addressLine2 : this.addressLine2;
            this.addressZip = ayondoForm.addressZip != null ? ayondoForm.addressZip : this.addressZip;
            this.previousAddressCity = ayondoForm.previousAddressCity != null ? ayondoForm.previousAddressCity : this.previousAddressCity;
            this.previousAddressCountry = ayondoForm.previousAddressCountry != null ? ayondoForm.previousAddressCountry : this.previousAddressCountry;
            this.previousAddressLine1 = ayondoForm.previousAddressLine1 != null ? ayondoForm.previousAddressLine1 : this.previousAddressLine1;
            this.previousAddressLine2 = ayondoForm.previousAddressLine2 != null ? ayondoForm.previousAddressLine2 : this.previousAddressLine2;
            this.previousAddressZip = ayondoForm.previousAddressZip != null ? ayondoForm.addressZip : this.addressZip;
            this.identificationDocument = ayondoForm.identificationDocument != null ? ayondoForm.identificationDocument : this.identificationDocument;
            this.identityDocumentUrl = ayondoForm.identityDocumentUrl != null ? ayondoForm.identityDocumentUrl : this.identityDocumentUrl;
            if (ayondoForm.clearIdentityDocumentUrl != null && ayondoForm.clearIdentityDocumentUrl)
            {
                this.identityDocumentUrl = null;
            }
            this.residenceDocumentType =
                    ayondoForm.getResidenceDocumentType() != null ? ayondoForm.getResidenceDocumentType() : this.residenceDocumentType;
            this.residenceDocumentUrl = ayondoForm.residenceDocumentUrl != null ? ayondoForm.residenceDocumentUrl : this.residenceDocumentUrl;
            if (ayondoForm.clearResidenceDocumentUrl != null && ayondoForm.clearResidenceDocumentUrl)
            {
                this.residenceDocumentUrl = null;
            }
            this.agreeTermsConditions = ayondoForm.agreeTermsConditions != null ? ayondoForm.agreeTermsConditions : this.agreeTermsConditions;
            this.agreeRisksWarnings = ayondoForm.agreeRisksWarnings != null ? ayondoForm.agreeRisksWarnings : this.agreeRisksWarnings;
            this.agreeDataSharing = ayondoForm.agreeDataSharing != null ? ayondoForm.agreeDataSharing : this.agreeDataSharing;
            this.needIdentityDocument = ayondoForm.needIdentityDocument != null ? ayondoForm.needIdentityDocument : this.needIdentityDocument;
            this.needResidencyDocument = ayondoForm.needResidencyDocument != null ? ayondoForm.needResidencyDocument : this.needResidencyDocument;
            this.guid = ayondoForm.guid != null ? ayondoForm.guid : this.guid;
            this.addressCheckUid = ayondoForm.addressCheckUid != null ? ayondoForm.addressCheckUid : this.addressCheckUid;
            this.identityCheckUid = ayondoForm.identityCheckUid != null ? ayondoForm.identityCheckUid : this.identityCheckUid;
            this.leadGuid = ayondoForm.leadGuid != null ? ayondoForm.leadGuid : this.leadGuid;
            this.stepStatuses = ayondoForm.stepStatuses != null ? ayondoForm.stepStatuses : this.stepStatuses;
        }
    }

    @Override public void setStepStatuses(@NonNull List<StepStatus> stepStatuses)
    {
        this.stepStatuses = Collections.unmodifiableList(stepStatuses);
    }

    @Nullable public List<StepStatus> getStepStatuses()
    {
        return stepStatuses;
    }

    @Override @Nullable public ScanReference getScanReference()
    {
        return scanReference;
    }

    public void setScanReference(@Nullable ScanReference scanReference)
    {
        this.scanReference = scanReference;
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

    @Nullable @Override public Country getCountry()
    {
        return country;
    }

    public void setCountry(@NonNull Country country)
    {
        this.country = country;
    }

    @Deprecated @Nullable public String getFullName()
    {
        return fullName;
    }

    @Deprecated public void setFullName(@Nullable String fullName)
    {
        this.fullName = fullName;
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
    @Nullable public Country getPhonePrimaryCountryCode()
    {
        return phonePrimaryCountryCode;
    }

    public void setPhonePrimaryCountryCode(@Nullable Country phonePrimaryCountryCode)
    {
        this.phonePrimaryCountryCode = phonePrimaryCountryCode;
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

    @Nullable @JsonIgnore AyondoLeveragedProductList getLeveragedProductList()
    {
        return leveragedProducts;
    }

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
        return leveragedProducts != null && leveragedProducts.contains(AyondoLeveragedProduct.SHARE_AND_BOND);
    }

    public void setTradedSharesBonds(@Nullable Boolean tradedSharesBonds)
    {
        setLeveragedProduct(AyondoLeveragedProduct.SHARE_AND_BOND, tradedSharesBonds != null && tradedSharesBonds);
    }

    @Nullable public Boolean isTradedOtcDerivative()
    {
        return leveragedProducts != null && leveragedProducts.contains(AyondoLeveragedProduct.OTC_DERIVATE);
    }

    public void setTradedOtcDerivative(@Nullable Boolean tradedOtcDerivative)
    {
        setLeveragedProduct(AyondoLeveragedProduct.OTC_DERIVATE, tradedOtcDerivative != null && tradedOtcDerivative);
    }

    @Nullable public Boolean isTradedEtc()
    {
        return leveragedProducts != null && leveragedProducts.contains(AyondoLeveragedProduct.EXCHANGE_TRADED_DERIVATIVE);
    }

    public void setTradedEtc(@Nullable Boolean tradedEtc)
    {
        setLeveragedProduct(AyondoLeveragedProduct.EXCHANGE_TRADED_DERIVATIVE, tradedEtc != null && tradedEtc);
    }

    private synchronized void setLeveragedProduct(@NonNull AyondoLeveragedProduct product, boolean yes)
    {
        if (leveragedProducts == null)
        {
            leveragedProducts = AyondoLeveragedProductList.createAyondoLeveragedProductList(null);
        }
        if (yes && !leveragedProducts.contains(product))
        {
            leveragedProducts.add(product);
        }
        else if (!yes)
        {
            leveragedProducts.remove(product);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Addresses">
    @Nullable public List<KYCAddress> getAddresses()
    {
        if (addressCity == null
                && addressCountry == null
                && addressLine1 == null
                && addressLine2 == null
                && addressZip == null)
        {
            return null;
        }
        boolean hasSecondAddress = previousAddressCity == null
                && previousAddressCountry == null
                && previousAddressLine1 == null
                && previousAddressLine2 == null
                && previousAddressZip == null;
        KYCAddress address1 = new KYCAddress(addressLine1, addressLine2, addressCity, addressCountry, addressZip, hasSecondAddress);
        if (!hasSecondAddress)
        {
            return Collections.singletonList(address1);
        }
        KYCAddress address2 =
                new KYCAddress(previousAddressLine1, previousAddressLine2, previousAddressCity, previousAddressCountry, previousAddressZip);
        return Collections.unmodifiableList(Arrays.asList(address1, address2));
    }

    public void setAddresses(@Nullable List<KYCAddress> addresses)
    {
        if (addresses == null || addresses.size() == 0)
        {
            setFirstAddress(null);
            setSecondAddress(null);
        }
        else
        {
            setFirstAddress(addresses.get(0));
            if (addresses.size() >= 2)
            {
                setSecondAddress(addresses.get(1));
            }
        }
    }

    protected void setFirstAddress(@Nullable KYCAddress address)
    {
        if (address == null)
        {
            addressCity = null;
            addressCountry = null;
            addressLine1 = null;
            addressLine2 = null;
            addressZip = null;
        }
        else
        {
            addressCity = address.city;
            addressCountry = address.country;
            addressLine1 = address.addressLine1;
            addressLine2 = address.addressLine2;
            addressZip = address.postalCode;
        }
    }

    protected void setSecondAddress(@Nullable KYCAddress address)
    {
        if (address == null)
        {
            previousAddressCity = null;
            previousAddressCountry = null;
            previousAddressLine1 = null;
            previousAddressLine2 = null;
            previousAddressZip = null;
        }
        else
        {
            previousAddressCity = address.city;
            previousAddressCountry = address.country;
            previousAddressLine1 = address.addressLine1;
            previousAddressLine2 = address.addressLine2;
            previousAddressZip = address.postalCode;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Identity Document File">
    @Nullable public AyondoIdentityDocumentType getIdentificationDocument()
    {
        return identificationDocument;
    }

    public void setIdentificationDocument(@Nullable AyondoIdentityDocumentType identificationDocument)
    {
        this.identificationDocument = identificationDocument;
    }

    @Nullable public IdentityScannedDocumentType getIdentityDocumentType()
    {
        return identificationDocument == null ? null : identificationDocument.scannedDocumentType;
    }

    public void setIdentityDocumentType(@Nullable IdentityScannedDocumentType identityScannedDocumentType)
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

    public void setIdentityDocumentUrl(@NonNull String documentUrl)
    {
        this.identityDocumentUrl = documentUrl;
    }

    public String getIdentityDocumentUrl()
    {
        return this.identityDocumentUrl;
    }

    public void setClearIdentityDocumentUrl(@Nullable Boolean clearIdentityDocumentUrl)
    {
        this.clearIdentityDocumentUrl = clearIdentityDocumentUrl;
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

    @Nullable @JsonIgnore public String getResidenceDocumentUrl()
    {
        return residenceDocumentUrl;
    }

    @JsonIgnore public void setResidenceDocumentUrl(@Nullable String residenceDocumentUrl)
    {
        this.residenceDocumentUrl = residenceDocumentUrl;
    }

    public void setClearResidenceDocumentUrl(@Nullable Boolean clearResidenceDocumentUrl)
    {
        this.clearResidenceDocumentUrl = clearResidenceDocumentUrl;
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

    @Nullable public Boolean isSubscribeOffers()
    {
        return subscribeOffers;
    }

    public void setSubscribeOffers(@Nullable Boolean subscribeOffers)
    {
        this.subscribeOffers = subscribeOffers;
    }

    @Nullable public Boolean isSubscribeTradeNotifications()
    {
        return subscribeTradeNotifications;
    }

    public void setSubscribeTradeNotifications(@Nullable Boolean subscribeTradeNotifications)
    {
        this.subscribeTradeNotifications = subscribeTradeNotifications;
    }
    //</editor-fold>

    @Nullable public Boolean getNeedIdentityDocument()
    {
        return needIdentityDocument;
    }

    public void setNeedIdentityDocument(@Nullable Boolean needIdentityDocument)
    {
        this.needIdentityDocument = needIdentityDocument;
    }

    @Nullable public Boolean getNeedResidencyDocument()
    {
        return needResidencyDocument;
    }

    public void setNeedResidencyDocument(@Nullable Boolean needResidencyDocument)
    {
        this.needResidencyDocument = needResidencyDocument;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getCurrency()
    {
        return currency;
    }

    public AyondoProductType getProductType()
    {
        return AyondoProductType.CFD;
    }

    public Boolean isTestRecord()
    {
        return true;
    }

    public String getWhiteLabel()
    {
        return whiteLabel;
    }

    public void setGuid(String guid)
    {
        this.guid = guid;
    }

    public void setAddressCheckUid(String addressCheckUid)
    {
        this.addressCheckUid = addressCheckUid;
    }

    public void setIdentityCheckUid(String identityCheckUid)
    {
        this.identityCheckUid = identityCheckUid;
    }

    public void setLeadGuid(String leadGuid)
    {
        this.leadGuid = leadGuid;
    }

    public String getGuid()
    {
        return guid;
    }

    public String getAddressCheckUid()
    {
        return addressCheckUid;
    }

    public String getIdentityCheckUid()
    {
        return identityCheckUid;
    }

    public String getLeadGuid()
    {
        return leadGuid;
    }

    @Override public boolean equals(@Nullable Object o)
    {
        if (o == null) return false;
        if (o == this) return true;
        boolean same;
        if (o instanceof KYCAyondoForm)
        {
            KYCAyondoForm ayondoForm = (KYCAyondoForm) o;
            same = country == null ? ayondoForm.country == null : country.equals(ayondoForm.country);
            same &= scanReference == null ? ayondoForm.scanReference == null : scanReference.equals(ayondoForm.scanReference);
            same &= ayondoGender == null ? ayondoForm.ayondoGender == null : ayondoGender.equals(ayondoForm.ayondoGender);
            same &= fullName == null ? ayondoForm.fullName == null : fullName.equals(ayondoForm.fullName);
            same &= firstName == null ? ayondoForm.firstName == null : firstName.equals(ayondoForm.firstName);
            same &= lastName == null ? ayondoForm.lastName == null : lastName.equals(ayondoForm.lastName);
            same &= middleName == null ? ayondoForm.middleName == null : middleName.equals(ayondoForm.middleName);
            same &= email == null ? ayondoForm.email == null : email.equals(ayondoForm.email);
            same &= verifiedEmail == null
                    ? ayondoForm.verifiedEmail == null
                    : verifiedEmail.equals(ayondoForm.verifiedEmail);
            same &= phonePrimaryCountryCode == null
                    ? ayondoForm.phonePrimaryCountryCode == null
                    : phonePrimaryCountryCode.equals(ayondoForm.phonePrimaryCountryCode);
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
            same &= employerRegulatedFinancial == null ? ayondoForm.employerRegulatedFinancial == null
                    : employerRegulatedFinancial.equals(ayondoForm.employerRegulatedFinancial);
            same &= workedInFinance1Year == null ? ayondoForm.workedInFinance1Year == null
                    : workedInFinance1Year.equals(ayondoForm.workedInFinance1Year);
            same &= attendedSeminarAyondo == null ? ayondoForm.attendedSeminarAyondo == null
                    : attendedSeminarAyondo.equals(ayondoForm.attendedSeminarAyondo);
            same &= haveOtherQualification == null ? ayondoForm.haveOtherQualification == null
                    : haveOtherQualification.equals(ayondoForm.haveOtherQualification);
            same &= tradingPerQuarter == null ? ayondoForm.tradingPerQuarter == null : tradingPerQuarter.equals(ayondoForm.tradingPerQuarter);
            if (same && leveragedProducts != null && ayondoForm.leveragedProducts != null)
            {
                //noinspection ConstantConditions
                same &= leveragedProducts.size() == ayondoForm.leveragedProducts.size();
                if (same)
                {
                    for (int index = 0; index < leveragedProducts.size(); index++)
                    {
                        same &= leveragedProducts.get(index).equals(ayondoForm.leveragedProducts.get(index));
                    }
                }
            }
            else
            {
                same &= (leveragedProducts == null && (ayondoForm.leveragedProducts == null || ayondoForm.leveragedProducts.isEmpty()));
            }
            same &= addressCity == null ? ayondoForm.addressCity == null : addressCity.equals(ayondoForm.addressCity);
            same &= addressCountry == null ? ayondoForm.addressCountry == null : addressCountry.equals(ayondoForm.addressCountry);
            same &= addressLine1 == null ? ayondoForm.addressLine1 == null : addressLine1.equals(ayondoForm.addressLine1);
            same &= addressLine2 == null ? ayondoForm.addressLine2 == null : addressLine2.equals(ayondoForm.addressLine2);
            same &= addressZip == null ? ayondoForm.addressZip == null : addressZip.equals(ayondoForm.addressZip);
            same &= previousAddressCity == null ? ayondoForm.previousAddressCity == null : previousAddressCity.equals(ayondoForm.previousAddressCity);
            same &= previousAddressCountry == null ? ayondoForm.previousAddressCountry == null
                    : previousAddressCountry.equals(ayondoForm.previousAddressCountry);
            same &= previousAddressLine1 == null ? ayondoForm.previousAddressLine1 == null
                    : previousAddressLine1.equals(ayondoForm.previousAddressLine1);
            same &= previousAddressLine2 == null ? ayondoForm.previousAddressLine2 == null
                    : previousAddressLine2.equals(ayondoForm.previousAddressLine2);
            same &= previousAddressZip == null ? ayondoForm.previousAddressZip == null : previousAddressZip.equals(ayondoForm.previousAddressZip);
            same &= identificationDocument == null ? ayondoForm.identificationDocument == null
                    : identificationDocument.equals(ayondoForm.identificationDocument);
            same &= identityDocumentUrl == null ? ayondoForm.identityDocumentUrl == null
                    : identityDocumentUrl.equals(ayondoForm.identityDocumentUrl);
            // Do not compare clearIdentityDocumentUrl
            same &= residenceDocumentType == null ? ayondoForm.residenceDocumentType == null
                    : residenceDocumentType.equals(ayondoForm.residenceDocumentType);
            same &= residenceDocumentUrl == null ? ayondoForm.residenceDocumentUrl == null
                    : residenceDocumentUrl.equals(ayondoForm.residenceDocumentUrl);
            // Do not compare clearResidenceDocumentFile
            same &= agreeTermsConditions == null ? ayondoForm.agreeTermsConditions == null
                    : agreeTermsConditions.equals(ayondoForm.agreeTermsConditions);
            same &= agreeRisksWarnings == null ? ayondoForm.agreeRisksWarnings == null : agreeRisksWarnings.equals(ayondoForm.agreeRisksWarnings);
            same &= agreeDataSharing == null ? ayondoForm.agreeDataSharing == null : agreeDataSharing.equals(ayondoForm.agreeDataSharing);
            same &= needIdentityDocument == null ? ayondoForm.needIdentityDocument == null
                    : needIdentityDocument.equals(ayondoForm.needIdentityDocument);
            same &= needResidencyDocument == null ? ayondoForm.needResidencyDocument == null
                    : needResidencyDocument.equals(ayondoForm.needResidencyDocument);
            same &= guid == null ? ayondoForm.guid == null : guid.equals(ayondoForm.guid);
            same &= addressCheckUid == null ? ayondoForm.addressCheckUid == null : addressCheckUid.equals(ayondoForm.addressCheckUid);
            same &= identityCheckUid == null ? ayondoForm.identityCheckUid == null : identityCheckUid.equals(ayondoForm.identityCheckUid);
            same &= leadGuid == null ? ayondoForm.leadGuid == null : leadGuid.equals(ayondoForm.leadGuid);
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

    @Override public int hashCode()
    {
        int code = country == null ? 0 : country.hashCode();
        code ^= scanReference == null ? 0 : scanReference.hashCode();
        code ^= ayondoGender == null ? 0 : ayondoGender.hashCode();
        code ^= fullName == null ? 0 : fullName.hashCode();
        code ^= firstName == null ? 0 : firstName.hashCode();
        code ^= lastName == null ? 0 : lastName.hashCode();
        code ^= middleName == null ? 0 : middleName.hashCode();
        code ^= email == null ? 0 : email.hashCode();
        code ^= verifiedEmail == null ? 0 : verifiedEmail.hashCode();
        code ^= phonePrimaryCountryCode == null ? 0 : phonePrimaryCountryCode.hashCode();
        code ^= mobileNumber == null ? 0 : mobileNumber.hashCode();
        code ^= verifiedMobileNumberDialingPrefix == null ? 0 : verifiedMobileNumberDialingPrefix.hashCode();
        code ^= verifiedMobileNumber == null ? 0 : verifiedMobileNumber.hashCode();
        code ^= nationality == null ? 0 : nationality.hashCode();
        code ^= residency == null ? 0 : residency.hashCode();
        code ^= dob == null ? 0 : dob.hashCode();
        code ^= annualIncomeRange == null ? 0 : annualIncomeRange.hashCode();
        code ^= netWorthRange == null ? 0 : netWorthRange.hashCode();
        code ^= percentNetWorthForInvestmentRange == null ? 0 : percentNetWorthForInvestmentRange.hashCode();
        code ^= employmentStatus == null ? 0 : employmentStatus.hashCode();
        code ^= employerRegulatedFinancial == null ? 0 : employerRegulatedFinancial.hashCode();
        code ^= workedInFinance1Year == null ? 0 : workedInFinance1Year.hashCode();
        code ^= attendedSeminarAyondo == null ? 0 : attendedSeminarAyondo.hashCode();
        code ^= haveOtherQualification == null ? 0 : haveOtherQualification.hashCode();
        code ^= tradingPerQuarter == null ? 0 : tradingPerQuarter.hashCode();
        if (leveragedProducts != null)
        {
            for (AyondoLeveragedProduct product : leveragedProducts.products)
            {
                code ^= product.hashCode();
            }
        }
        else
        {
            code ^= 0;
        }
        code ^= addressCity == null ? 0 : addressCity.hashCode();
        code ^= addressCountry == null ? 0 : addressCountry.hashCode();
        code ^= addressLine1 == null ? 0 : addressLine1.hashCode();
        code ^= addressLine2 == null ? 0 : addressLine2.hashCode();
        code ^= addressZip == null ? 0 : addressZip.hashCode();
        code ^= previousAddressCity == null ? 0 : previousAddressCity.hashCode();
        code ^= previousAddressCountry == null ? 0 : previousAddressCountry.hashCode();
        code ^= previousAddressLine1 == null ? 0 : previousAddressLine1.hashCode();
        code ^= previousAddressLine2 == null ? 0 : previousAddressLine2.hashCode();
        code ^= previousAddressZip == null ? 0 : previousAddressZip.hashCode();
        code ^= identificationDocument == null ? 0 : identificationDocument.hashCode();
        code ^= identityDocumentUrl == null ? 0 : identityDocumentUrl.hashCode();
        // Do not hash clearIdentityDocumentUrl
        code ^= residenceDocumentType == null ? 0 : residenceDocumentType.hashCode();
        code ^= residenceDocumentUrl == null ? 0 : residenceDocumentUrl.hashCode();
        // Do not hash clearResidenceDocumentUrl
        code ^= agreeTermsConditions == null ? 0 : agreeTermsConditions.hashCode();
        code ^= agreeRisksWarnings == null ? 0 : agreeRisksWarnings.hashCode();
        code ^= agreeDataSharing == null ? 0 : agreeDataSharing.hashCode();
        code ^= needIdentityDocument == null ? 0 : needIdentityDocument.hashCode();
        code ^= needResidencyDocument == null ? 0 : needResidencyDocument.hashCode();
        code ^= guid == null ? 0 : guid.hashCode();
        code ^= addressCheckUid == null ? 0 : addressCheckUid.hashCode();
        code ^= identityCheckUid == null ? 0 : identityCheckUid.hashCode();
        code ^= leadGuid == null ? 0 : leadGuid.hashCode();
        return code;
    }

    @Override public String toString()
    {
        return "KYCAyondoForm{" +
                "country=" + country +
                ", scanReference=" + scanReference +
                ", ayondoGender=" + ayondoGender +
                ", fullName='" + fullName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", email='" + email + '\'' +
                ", verifiedEmail='" + verifiedEmail + '\'' +
                ", phonePrimaryCountryCode=" + phonePrimaryCountryCode +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", verifiedMobileNumberDialingPrefix=" + verifiedMobileNumberDialingPrefix +
                ", verifiedMobileNumber='" + verifiedMobileNumber + '\'' +
                ", nationality=" + nationality +
                ", residency=" + residency +
                ", dob='" + dob + '\'' +
                ", annualIncomeRange=" + annualIncomeRange +
                ", netWorthRange=" + netWorthRange +
                ", percentNetWorthForInvestmentRange=" + percentNetWorthForInvestmentRange +
                ", employmentStatus=" + employmentStatus +
                ", employerRegulatedFinancial=" + employerRegulatedFinancial +
                ", workedInFinance1Year=" + workedInFinance1Year +
                ", attendedSeminarAyondo=" + attendedSeminarAyondo +
                ", haveOtherQualification=" + haveOtherQualification +
                ", tradingPerQuarter=" + tradingPerQuarter +
                ", leveragedProducts=" + leveragedProducts +
                ", addressCity='" + addressCity + '\'' +
                ", addressCountry=" + addressCountry +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", addressZip='" + addressZip + '\'' +
                ", previousAddressCity='" + previousAddressCity + '\'' +
                ", previousAddressCountry=" + previousAddressCountry +
                ", previousAddressLine1='" + previousAddressLine1 + '\'' +
                ", previousAddressLine2='" + previousAddressLine2 + '\'' +
                ", previousAddressZip='" + previousAddressZip + '\'' +
                ", identificationDocument=" + identificationDocument +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", identityDocumentUrl=" + identityDocumentUrl +
                ", clearIdentityDocumentUrl=" + clearIdentityDocumentUrl +
                ", residenceDocumentType=" + residenceDocumentType +
                ", residenceDocumentUrl=" + residenceDocumentUrl +
                ", clearResidenceDocumentUrl=" + clearResidenceDocumentUrl +
                ", agreeTermsConditions=" + agreeTermsConditions +
                ", agreeRisksWarnings=" + agreeRisksWarnings +
                ", agreeDataSharing=" + agreeDataSharing +
                ", needIdentityDocument=" + needIdentityDocument +
                ", needResidencyDocument=" + needResidencyDocument +
                ", guid=" + guid +
                ", addressCheckUid=" + addressCheckUid +
                ", identityCheckUid=" + identityCheckUid +
                ", leadGuid=" + leadGuid +
                ", stepStatuses=" + stepStatuses +
                '}';
    }
}
