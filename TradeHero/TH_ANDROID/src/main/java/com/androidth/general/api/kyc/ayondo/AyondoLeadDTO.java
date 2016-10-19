package com.androidth.general.api.kyc.ayondo;

import android.support.annotation.Nullable;

import com.androidth.general.api.kyc.AnnualIncomeRange;
import com.androidth.general.api.kyc.Currency;
import com.androidth.general.api.kyc.EmploymentStatus;
import com.androidth.general.api.kyc.KYCAddress;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.NetWorthRange;
import com.androidth.general.api.kyc.PercentNetWorthForInvestmentRange;
import com.androidth.general.api.kyc.TradingPerQuarter;
import com.androidth.general.api.market.Country;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedList;
import java.util.List;

public class AyondoLeadDTO extends AyondoLeadAddressDTO
{
    @JsonProperty("PhonePrimaryCountryCode") @Nullable public final Country phonePrimaryCountryCode;

    @JsonProperty("AnnualIncome") @Nullable public final AnnualIncomeRange annualIncomeRange;
    @JsonProperty("NetWorth") @Nullable public final NetWorthRange netWorthRange;
    @JsonProperty("InvestmentPortfolio") @Nullable public final PercentNetWorthForInvestmentRange percentNetWorthForInvestmentRange;
    @JsonProperty("EmploymentStatus") @Nullable public final EmploymentStatus employmentStatus;
    @JsonProperty("IsEmployerRegulated") @Nullable public final Boolean employerRegulatedFinancial;

    @JsonProperty("HasProfessionalExperience") @Nullable public final Boolean workedInFinance1Year;
    @JsonProperty("HasAttendedTraining") @Nullable public final Boolean attendedSeminarAyondo;
    @JsonProperty("HasOtherQualification") @Nullable public final Boolean haveOtherQualification;

    @JsonProperty("LeveragedProducts") @Nullable public AyondoLeveragedProductList leveragedProducts;
    @JsonProperty("NumberOfMarginTrades") @Nullable public final TradingPerQuarter tradingPerQuarter;

    @JsonProperty("Email") @Nullable public final String email;
    @JsonProperty("Language") @Nullable public final String language;
    @JsonProperty("Currency") @Nullable public final Currency currency;
    @JsonProperty("ProductType") @Nullable public final AyondoProductType productType;

    @JsonProperty("SubscribeOffers") @Nullable public final Boolean subscribeOffers;
    @JsonProperty("SubscribeTradeNotifications") @Nullable public final Boolean subscribeTradeNotifications;

    @JsonProperty("Guid") @Nullable public final String guid;

    @JsonProperty("IsTestRecord") @Nullable public final Boolean isTestRecord;
    @JsonProperty("WhiteLabel") public final String whiteLabel;
    @JsonProperty("AdditionalData") public ProviderQuestionnaireAnswerDto[] additionalData;

    public AyondoLeadDTO() {
        super();
        this.phonePrimaryCountryCode = Country.TH;
        this.annualIncomeRange = AnnualIncomeRange.LESS15KUSD;
        this.netWorthRange = NetWorthRange.LESS15KUSD;
        this.percentNetWorthForInvestmentRange = PercentNetWorthForInvestmentRange.LESSTHAN25P;
        this.employmentStatus = EmploymentStatus.EMPLOYED;
        this.employerRegulatedFinancial = false;
        this.workedInFinance1Year = false;
        this.attendedSeminarAyondo = false;
        this.haveOtherQualification = false;
        this.tradingPerQuarter = TradingPerQuarter.NONE;
        this.email = "";
        this.language = "EN";
        this.currency = Currency.USD;
        this.productType = AyondoProductType.CFD;
        this.subscribeOffers = false;
        this.subscribeTradeNotifications = false;
        this.guid = "";
        this.isTestRecord = true;
        this.whiteLabel = "TradeHero";
    }

    public AyondoLeadDTO(KYCAyondoForm kycAyondoForm)
    {
        super(kycAyondoForm);
        this.phonePrimaryCountryCode = kycAyondoForm.getPhonePrimaryCountryCode();
        this.annualIncomeRange = kycAyondoForm.getAnnualIncomeRange();
        this.netWorthRange = kycAyondoForm.getNetWorthRange();
        this.percentNetWorthForInvestmentRange = kycAyondoForm.getPercentNetWorthForInvestmentRange();
        this.employmentStatus = kycAyondoForm.getEmploymentStatus();
        this.employerRegulatedFinancial = kycAyondoForm.isEmployerRegulatedFinancial();
        this.workedInFinance1Year = kycAyondoForm.isWorkedInFinance1Year();
        this.attendedSeminarAyondo = kycAyondoForm.isAttendedSeminarAyondo();
        this.haveOtherQualification = kycAyondoForm.isHaveOtherQualification();
        this.leveragedProducts = kycAyondoForm.getLeveragedProductList();
        this.tradingPerQuarter = kycAyondoForm.getTradingPerQuarter();
        this.email = kycAyondoForm.getEmail();
        this.language = kycAyondoForm.getLanguage();
        this.currency = kycAyondoForm.getCurrency();
        this.productType = kycAyondoForm.getProductType();
        this.subscribeOffers = kycAyondoForm.isSubscribeOffers();
        this.subscribeTradeNotifications = kycAyondoForm.isSubscribeTradeNotifications();
        this.guid = kycAyondoForm.getGuid();
        this.isTestRecord = kycAyondoForm.isTestRecord();
        this.whiteLabel = kycAyondoForm.getWhiteLabel();
        this.additionalData = kycAyondoForm.getAdditionalData();
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AyondoLeadDTO)) return false;
        if (!super.equals(o)) return false;

        AyondoLeadDTO that = (AyondoLeadDTO) o;

        if (phonePrimaryCountryCode != that.phonePrimaryCountryCode) return false;
        if (annualIncomeRange != that.annualIncomeRange) return false;
        if (netWorthRange != that.netWorthRange) return false;
        if (percentNetWorthForInvestmentRange != that.percentNetWorthForInvestmentRange) return false;
        if (employmentStatus != that.employmentStatus) return false;
        if (employerRegulatedFinancial != null ? !employerRegulatedFinancial.equals(that.employerRegulatedFinancial)
                : that.employerRegulatedFinancial != null)
        {
            return false;
        }
        if (workedInFinance1Year != null ? !workedInFinance1Year.equals(that.workedInFinance1Year) : that.workedInFinance1Year != null) return false;
        if (attendedSeminarAyondo != null ? !attendedSeminarAyondo.equals(that.attendedSeminarAyondo) : that.attendedSeminarAyondo != null)
        {
            return false;
        }
        if (haveOtherQualification != null ? !haveOtherQualification.equals(that.haveOtherQualification) : that.haveOtherQualification != null)
        {
            return false;
        }
        if (leveragedProducts != null ? !leveragedProducts.equals(that.leveragedProducts) : that.leveragedProducts != null) return false;
        if (tradingPerQuarter != that.tradingPerQuarter) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (productType != that.productType) return false;
        if (subscribeOffers != null ? !subscribeOffers.equals(that.subscribeOffers) : that.subscribeOffers != null) return false;
        if (subscribeTradeNotifications != null ? !subscribeTradeNotifications.equals(that.subscribeTradeNotifications)
                : that.subscribeTradeNotifications != null)
        {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) return false;
        if (isTestRecord != null ? !isTestRecord.equals(that.isTestRecord) : that.isTestRecord != null) return false;

        if (additionalData != null ? !additionalData.equals(that.additionalData) : that.additionalData != null) return false;

        return !(whiteLabel != null ? !whiteLabel.equals(that.whiteLabel) : that.whiteLabel != null);

    }

    public KYCAyondoForm getKYCAyondoForm() {
        KYCAyondoForm kycAyondoForm = new KYCAyondoForm();
        kycAyondoForm.setPhonePrimaryCountryCode(this.phonePrimaryCountryCode);
        kycAyondoForm.setAnnualIncomeRange(this.annualIncomeRange);
        kycAyondoForm.setNetWorthRange(this.netWorthRange);
        kycAyondoForm.setPercentNetWorthForInvestmentRange(this.percentNetWorthForInvestmentRange);
        kycAyondoForm.setEmploymentStatus(this.employmentStatus);
        kycAyondoForm.setEmployerRegulatedFinancial(this.employerRegulatedFinancial);
        kycAyondoForm.setWorkedInFinance1Year(this.workedInFinance1Year);
        kycAyondoForm.setAttendedSeminarAyondo(this.attendedSeminarAyondo);
        kycAyondoForm.setHaveOtherQualification(this.haveOtherQualification);
        //this.leveragedProducts = kycAyondoForm.getLeveragedProductList();
        kycAyondoForm.setTradingPerQuarter(this.tradingPerQuarter);
        kycAyondoForm.setEmail(this.email);
        kycAyondoForm.setVerifiedEmailAddress(this.email);
        //this.language = kycAyondoForm.getLanguage();
        kycAyondoForm.setCurrency(this.currency);
        //this.productType = kycAyondoForm.getProductType();
        kycAyondoForm.setSubscribeOffers(this.subscribeOffers );
        kycAyondoForm.setSubscribeTradeNotifications(this.subscribeTradeNotifications);
        kycAyondoForm.setGuid(this.guid);
        //this.isTestRecord = kycAyondoForm.isTestRecord();
        //this.whiteLabel = kycAyondoForm.getWhiteLabel();
        kycAyondoForm.setAdditionalData(this.additionalData);

        List<KYCAddress> addresses = new LinkedList<>();
        addresses.add(new KYCAddress(this.addressLine1, this.addressLine2, this.addressCity, this.addressCountry, this.addressZip));
        addresses.add(new KYCAddress(this.previousAddressLine1, this.previousAddressLine2, this.previousAddressCity, this.previousAddressCountry, this.previousAddressZip));

        kycAyondoForm.setAddresses(addresses);
        kycAyondoForm.setMobileNumber(this.mobileNumber);
        kycAyondoForm.setVerifiedMobileNumber(this.mobileNumber);
        kycAyondoForm.setDob(this.dob);
        kycAyondoForm.setFirstName(this.firstName);
        kycAyondoForm.setMiddleName(this.middleName);
        kycAyondoForm.setLastName(this.lastName);
        kycAyondoForm.setAyondoGender(this.ayondoGender);
        kycAyondoForm.setNationality(this.nationality);
        kycAyondoForm.setIdentificationDocument(this.identificationDocument);
        kycAyondoForm.setIdentificationNumber(this.identificationNumber);
        kycAyondoForm.setVerifiedIdentificationNumber(this.identificationNumber);

        return kycAyondoForm;
    }
}
