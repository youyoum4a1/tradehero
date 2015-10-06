package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.kyc.AnnualIncomeRange;
import com.tradehero.th.api.kyc.Currency;
import com.tradehero.th.api.kyc.EmploymentStatus;
import com.tradehero.th.api.kyc.NetWorthRange;
import com.tradehero.th.api.kyc.PercentNetWorthForInvestmentRange;
import com.tradehero.th.api.kyc.TradingPerQuarter;
import com.tradehero.th.api.market.Country;

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

    @JsonProperty("LeveragedProducts") @Nullable public final AyondoLeveragedProductList leveragedProducts;
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
        return !(whiteLabel != null ? !whiteLabel.equals(that.whiteLabel) : that.whiteLabel != null);
    }
}
