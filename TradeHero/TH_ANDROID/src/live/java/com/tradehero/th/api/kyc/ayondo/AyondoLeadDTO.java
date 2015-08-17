package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.kyc.AnnualIncomeRange;
import com.tradehero.th.api.kyc.EmploymentStatus;
import com.tradehero.th.api.kyc.NetWorthRange;
import com.tradehero.th.api.kyc.PercentNetWorthForInvestmentRange;
import com.tradehero.th.api.kyc.TradingPerQuarter;
import com.tradehero.th.api.market.Country;
import java.util.UUID;

public class AyondoLeadDTO extends AyondoLeadAddressDTO
{
    @JsonProperty("PhonePrimaryCountryCode") @Nullable private Country phonePrimaryCountryCode;

    @JsonProperty("AnnualIncome") @Nullable private AnnualIncomeRange annualIncomeRange;
    @JsonProperty("NetWorth") @Nullable private NetWorthRange netWorthRange;
    @JsonProperty("InvestmentPortfolio") @Nullable private PercentNetWorthForInvestmentRange percentNetWorthForInvestmentRange;
    @JsonProperty("EmploymentStatus") @Nullable private EmploymentStatus employmentStatus;
    @JsonProperty("IsEmployerRegulated") @Nullable private Boolean employerRegulatedFinancial;

    @JsonProperty("HasProfessionalExperience") @Nullable private Boolean workedInFinance1Year;
    @JsonProperty("HasAttendedTraining") @Nullable private Boolean attendedSeminarAyondo;
    @JsonProperty("HasOtherQualification") @Nullable private Boolean haveOtherQualification;

    @JsonProperty("LeveragedProducts") @Nullable private AyondoLeveragedProductList leveragedProducts;
    @JsonProperty("NumberOfMarginTrades") @Nullable private TradingPerQuarter tradingPerQuarter;

    @JsonProperty("Email") @Nullable private String email;
    @JsonProperty("Language") @Nullable private Country language;
    @JsonProperty("Currency") @Nullable private String currency;
    @JsonProperty("ProductType") @Nullable private AyondoProductType productType;

    @JsonProperty("SubscribeOffers") @Nullable private Boolean subscribeOffers;
    @JsonProperty("SubscribeTradeNotifications") @Nullable private Boolean subscribeTradeNotifications;

    @JsonProperty("Guid") @Nullable private UUID uid;

    @JsonProperty("IsTestRecord") @Nullable private Boolean isTestRecord;
    @JsonProperty("WhiteLabel") private String whiteLabel;

    public AyondoLeadDTO()
    {
    }

    @Nullable public UUID getUid()
    {
        return uid;
    }

    public void setUid(@Nullable UUID uid)
    {
        this.uid = uid;
    }

    @Nullable public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(@Nullable String currency)
    {
        this.currency = currency;
    }

    @Nullable public Boolean getIsTestRecord()
    {
        return isTestRecord;
    }

    public void setIsTestRecord(@Nullable Boolean isTestRecord)
    {
        this.isTestRecord = isTestRecord;
    }

    @Nullable public Country getLanguage()
    {
        return language;
    }

    public void setLanguage(@Nullable Country language)
    {
        this.language = language;
    }

    @Nullable public AyondoProductType getProductType()
    {
        return productType;
    }

    public void setProductType(@Nullable AyondoProductType productType)
    {
        this.productType = productType;
    }

    public boolean isValidToCreateAccount()
    {
        return currency != null
                // No need to text isTestRecord
                // TODO decide if we test language
                ;
    }
}
