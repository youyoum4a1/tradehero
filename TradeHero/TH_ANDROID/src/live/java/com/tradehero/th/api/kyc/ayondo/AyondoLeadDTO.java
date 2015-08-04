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
    @JsonProperty("Guid") @Nullable private UUID uid;
    @JsonProperty("PhonePrimaryCountryCode") @Nullable private Country phonePrimaryCountryCode;
    @JsonProperty("AnnualIncome") @Nullable private AnnualIncomeRange annualIncome;
    @JsonProperty("Currency") @Nullable private String currency;
    @JsonProperty("Email") @Nullable private String email;
    @JsonProperty("EmploymentStatus") @Nullable private EmploymentStatus employmentStatus;
    @JsonProperty("HasAttendedTraining") @Nullable private Boolean hasAttendedTraining;
    @JsonProperty("HasOtherQualification") @Nullable private Boolean hasOtherQualification;
    @JsonProperty("HasProfessionalExperience") @Nullable private Boolean hasProfessionalExperience;
    @JsonProperty("InvestmentPortfolio") @Nullable private PercentNetWorthForInvestmentRange investmentPortfolio;
    @JsonProperty("IsTestRecord") @Nullable private Boolean isTestRecord;
    @JsonProperty("IsEmployerRegulated") @Nullable private Boolean isEmployerRegulated;
    @JsonProperty("Language") @Nullable private Country language;
    @JsonProperty("LeveragedProducts") @Nullable private AyondoLeveragedProductList leveragedProducts;
    @JsonProperty("NetWorth") @Nullable private NetWorthRange netWorth;
    @JsonProperty("NumberOfMarginTrades") @Nullable private TradingPerQuarter numberOfMarginTrades;
    @JsonProperty("Password") @Nullable private String password;
    @JsonProperty("ProductType") @Nullable private AyondoProductType productType;
    @JsonProperty("SubscribeOffers") @Nullable private Boolean subscribeOffers;
    @JsonProperty("SubscribeTradeNotifications") @Nullable private Boolean subscribeTradeNotifications;
    @JsonProperty("UserName") @Nullable private String userName;

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

    @Nullable public Country getPhonePrimaryCountryCode()
    {
        return phonePrimaryCountryCode;
    }

    public void setPhonePrimaryCountryCode(@Nullable Country phonePrimaryCountryCode)
    {
        this.phonePrimaryCountryCode = phonePrimaryCountryCode;
    }

    @Nullable public AnnualIncomeRange getAnnualIncome()
    {
        return annualIncome;
    }

    public void setAnnualIncome(@Nullable AnnualIncomeRange annualIncome)
    {
        this.annualIncome = annualIncome;
    }

    @Nullable public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(@Nullable String currency)
    {
        this.currency = currency;
    }

    @Nullable public String getEmail()
    {
        return email;
    }

    public void setEmail(@Nullable String email)
    {
        this.email = email;
    }

    @Nullable public EmploymentStatus getEmploymentStatus()
    {
        return employmentStatus;
    }

    public void setEmploymentStatus(@Nullable EmploymentStatus employmentStatus)
    {
        this.employmentStatus = employmentStatus;
    }

    @Nullable public Boolean getHasAttendedTraining()
    {
        return hasAttendedTraining;
    }

    public void setHasAttendedTraining(@Nullable Boolean hasAttendedTraining)
    {
        this.hasAttendedTraining = hasAttendedTraining;
    }

    @Nullable public Boolean getHasOtherQualification()
    {
        return hasOtherQualification;
    }

    public void setHasOtherQualification(@Nullable Boolean hasOtherQualification)
    {
        this.hasOtherQualification = hasOtherQualification;
    }

    @Nullable public Boolean getHasProfessionalExperience()
    {
        return hasProfessionalExperience;
    }

    public void setHasProfessionalExperience(@Nullable Boolean hasProfessionalExperience)
    {
        this.hasProfessionalExperience = hasProfessionalExperience;
    }

    @Nullable public PercentNetWorthForInvestmentRange getInvestmentPortfolio()
    {
        return investmentPortfolio;
    }

    public void setInvestmentPortfolio(@Nullable PercentNetWorthForInvestmentRange investmentPortfolio)
    {
        this.investmentPortfolio = investmentPortfolio;
    }

    @Nullable public Boolean getIsTestRecord()
    {
        return isTestRecord;
    }

    public void setIsTestRecord(@Nullable Boolean isTestRecord)
    {
        this.isTestRecord = isTestRecord;
    }

    @Nullable public Boolean getIsEmployerRegulated()
    {
        return isEmployerRegulated;
    }

    public void setIsEmployerRegulated(@Nullable Boolean isEmployerRegulated)
    {
        this.isEmployerRegulated = isEmployerRegulated;
    }

    @Nullable public Country getLanguage()
    {
        return language;
    }

    public void setLanguage(@Nullable Country language)
    {
        this.language = language;
    }

    @Nullable public AyondoLeveragedProductList getLeveragedProducts()
    {
        return leveragedProducts;
    }

    public void setLeveragedProducts(@Nullable AyondoLeveragedProductList leveragedProducts)
    {
        this.leveragedProducts = leveragedProducts;
    }

    @Nullable public NetWorthRange getNetWorth()
    {
        return netWorth;
    }

    public void setNetWorth(@Nullable NetWorthRange netWorth)
    {
        this.netWorth = netWorth;
    }

    @Nullable public TradingPerQuarter getNumberOfMarginTrades()
    {
        return numberOfMarginTrades;
    }

    public void setNumberOfMarginTrades(@Nullable TradingPerQuarter numberOfMarginTrades)
    {
        this.numberOfMarginTrades = numberOfMarginTrades;
    }

    @Nullable public String getPassword()
    {
        return password;
    }

    public void setPassword(@Nullable String password)
    {
        this.password = password;
    }

    @Nullable public AyondoProductType getProductType()
    {
        return productType;
    }

    public void setProductType(@Nullable AyondoProductType productType)
    {
        this.productType = productType;
    }

    @Nullable public Boolean getSubscribeOffers()
    {
        return subscribeOffers;
    }

    public void setSubscribeOffers(@Nullable Boolean subscribeOffers)
    {
        this.subscribeOffers = subscribeOffers;
    }

    @Nullable public Boolean getSubscribeTradeNotifications()
    {
        return subscribeTradeNotifications;
    }

    public void setSubscribeTradeNotifications(@Nullable Boolean subscribeTradeNotifications)
    {
        this.subscribeTradeNotifications = subscribeTradeNotifications;
    }

    @Nullable public String getUserName()
    {
        return userName;
    }

    public void setUserName(@Nullable String userName)
    {
        this.userName = userName;
    }

    @Override public boolean isValidToCreateAccount()
    {
        return super.isValidToCreateAccount()
                && phonePrimaryCountryCode != null
                && annualIncome != null
                && currency != null
                && email != null
                && employmentStatus != null
                && hasAttendedTraining != null
                && hasOtherQualification != null
                && hasProfessionalExperience != null
                && investmentPortfolio != null
                // No need to text isTestRecord
                && isEmployerRegulated != null
                // TODO decide if we test language
                && leveragedProducts != null
                && netWorth != null
                && numberOfMarginTrades != null
                && password != null
                && subscribeOffers != null
                && subscribeTradeNotifications != null
                && userName != null;
    }
}
