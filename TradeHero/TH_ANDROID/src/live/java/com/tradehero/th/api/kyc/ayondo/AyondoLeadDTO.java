package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.market.Country;
import java.util.UUID;

public class AyondoLeadDTO extends AyondoLeadAddressDTO
{
    @JsonProperty("Guid") @Nullable private UUID uid;
    @JsonProperty("PhonePrimaryCountryCode") @Nullable private Country phonePrimaryCountryCode;
    @JsonProperty("Currency") @Nullable private String currency;
    @JsonProperty("IsTestRecord") @Nullable private Boolean isTestRecord;
    @JsonProperty("Language") @Nullable private Country language;
    @JsonProperty("LeveragedProducts") @Nullable private AyondoLeveragedProductList leveragedProducts;
    @JsonProperty("Password") @Nullable private String password;
    @JsonProperty("ProductType") @Nullable private AyondoProductType productType;
    @JsonProperty("SubscribeOffers") @Nullable private Boolean subscribeOffers;
    @JsonProperty("SubscribeTradeNotifications") @Nullable private Boolean subscribeTradeNotifications;

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

    @Nullable public AyondoLeveragedProductList getLeveragedProducts()
    {
        return leveragedProducts;
    }

    public void setLeveragedProducts(@Nullable AyondoLeveragedProductList leveragedProducts)
    {
        this.leveragedProducts = leveragedProducts;
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

    public boolean isValidToCreateAccount()
    {
        return phonePrimaryCountryCode != null
                && currency != null
                // No need to text isTestRecord
                // TODO decide if we test language
                && leveragedProducts != null
                && password != null
                && subscribeOffers != null
                && subscribeTradeNotifications != null;
    }
}
