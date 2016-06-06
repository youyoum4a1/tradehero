package com.ayondo.academyapp.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ayondo.academyapp.api.kyc.AnnualIncomeRange;
import com.ayondo.academyapp.api.kyc.Currency;
import com.ayondo.academyapp.api.kyc.EmploymentStatus;
import com.ayondo.academyapp.api.kyc.KYCFormOptionsDTO;
import com.ayondo.academyapp.api.kyc.NetWorthRange;
import com.ayondo.academyapp.api.kyc.PercentNetWorthForInvestmentRange;
import com.ayondo.academyapp.api.kyc.TradingPerQuarter;
import com.ayondo.academyapp.api.market.Country;
import com.ayondo.academyapp.models.fastfill.Gender;
import com.ayondo.academyapp.models.fastfill.IdentityScannedDocumentType;
import com.ayondo.academyapp.models.fastfill.ResidenceScannedDocumentType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KYCAyondoFormOptionsDTO implements KYCFormOptionsDTO
{
    public static final String KEY_AYONDO_TYPE = "AYD";

    @NonNull public final List<Gender> genders;
    @NonNull public final List<Country> allowedMobilePhoneCountries;
    @NonNull public final List<Country> allowedNationalityCountries;
    @NonNull public final List<Country> allowedResidencyCountries;
    @NonNull public final List<AnnualIncomeRange> annualIncomeOptions;
    @NonNull public final List<NetWorthRange> netWorthOptions;
    @NonNull public final List<PercentNetWorthForInvestmentRange> percentNetWorthOptions;
    @NonNull public final List<EmploymentStatus> employmentStatusOptions;
    @NonNull public final List<TradingPerQuarter> tradingPerQuarterOptions;
    public final int maxAddressRequired;
    @NonNull public final List<IdentityScannedDocumentType> identityDocumentTypes;
    @NonNull public final List<ResidenceScannedDocumentType> residenceDocumentTypes;
    @NonNull public final String termsConditionsUrl;
    @NonNull public final String riskWarningDisclaimerUrl;
    @NonNull public final String dataSharingAgreementUrl;
    public final int minAge;
    @NonNull public final List<Currency> currencies;

    public KYCAyondoFormOptionsDTO(
            @JsonProperty("genders") @Nullable List<Gender> genders,
            @JsonProperty("allowedMobilePhoneCountries") @Nullable List<Country> allowedMobilePhoneCountries,
            @JsonProperty("allowedNationalityCountries") @Nullable List<Country> allowedNationalityCountries,
            @JsonProperty("allowedResidencyCountries") @Nullable List<Country> allowedResidencyCountries,
            @JsonProperty("annualIncomeOptions") @NonNull List<AnnualIncomeRange> annualIncomeOptions,
            @JsonProperty("netWorthOptions") @NonNull List<NetWorthRange> netWorthOptions,
            @JsonProperty("percentNetWorthOptions") @NonNull List<PercentNetWorthForInvestmentRange> percentNetWorthOptions,
            @JsonProperty("employmentStatusOptions") @NonNull List<EmploymentStatus> employmentStatusOptions,
            @JsonProperty("tradingPerQuarterOptions") @NonNull List<TradingPerQuarter> tradingPerQuarterOptions,
            @JsonProperty("maxAddressRequired") int maxAddressRequired,
            @JsonProperty("identityDocumentTypes") @NonNull List<IdentityScannedDocumentType> identityDocumentTypes,
            @JsonProperty("residenceDocumentTypes") @NonNull List<ResidenceScannedDocumentType> residenceDocumentTypes,
            @JsonProperty("termsConditionsUrl") @NonNull String termsConditionsUrl,
            @JsonProperty("riskWarningDisclosureUrl") @NonNull String riskWarningDisclosureUrl,
            @JsonProperty("dataSharingAgreementUrl") @NonNull String dataSharingAgreementUrl,
            @JsonProperty("minAge") int minAge,
            @JsonProperty("currencies") @NonNull List<Currency> currencies)
    {
        if (genders == null)
        {
            this.genders = Collections.unmodifiableList(Arrays.asList(Gender.values()));
        }
        else
        {
            this.genders = Collections.unmodifiableList(genders);
        }
        if (allowedMobilePhoneCountries == null)
        {
            this.allowedMobilePhoneCountries = Collections.unmodifiableList(Arrays.asList(Country.values()));
        }
        else
        {
            this.allowedMobilePhoneCountries = Collections.unmodifiableList(allowedMobilePhoneCountries);
        }
        if (allowedNationalityCountries == null)
        {
            this.allowedNationalityCountries = Collections.unmodifiableList(Arrays.asList(Country.values()));
        }
        else
        {
            this.allowedNationalityCountries = Collections.unmodifiableList(allowedNationalityCountries);
        }
        if (allowedResidencyCountries == null)
        {
            this.allowedResidencyCountries = Collections.unmodifiableList(Arrays.asList(Country.values()));
        }
        else
        {
            this.allowedResidencyCountries = Collections.unmodifiableList(allowedResidencyCountries);
        }
        this.annualIncomeOptions = Collections.unmodifiableList(annualIncomeOptions);
        this.netWorthOptions = Collections.unmodifiableList(netWorthOptions);
        this.percentNetWorthOptions = Collections.unmodifiableList(percentNetWorthOptions);
        this.employmentStatusOptions = Collections.unmodifiableList(employmentStatusOptions);
        this.tradingPerQuarterOptions = Collections.unmodifiableList(tradingPerQuarterOptions);
        this.maxAddressRequired = maxAddressRequired;
        this.identityDocumentTypes = Collections.unmodifiableList(identityDocumentTypes);
        this.residenceDocumentTypes = Collections.unmodifiableList(residenceDocumentTypes);
        this.termsConditionsUrl = termsConditionsUrl;
        this.riskWarningDisclaimerUrl = riskWarningDisclosureUrl;
        this.dataSharingAgreementUrl = dataSharingAgreementUrl;
        this.minAge = minAge;
        this.currencies = Collections.unmodifiableList(currencies);
    }

    @NonNull public List<IdentityScannedDocumentType> getIdentityDocumentTypes()
    {
        return identityDocumentTypes;
    }

    @Override public String toString()
    {
        return "KYCAyondoFormOptionsDTO{" +
                "genders=" + genders +
                ", allowedMobilePhoneCountries=" + allowedMobilePhoneCountries +
                ", allowedNationalityCountries=" + allowedNationalityCountries +
                ", allowedResidencyCountries=" + allowedResidencyCountries +
                ", annualIncomeOptions=" + annualIncomeOptions +
                ", netWorthOptions=" + netWorthOptions +
                ", percentNetWorthOptions=" + percentNetWorthOptions +
                ", employmentStatusOptions=" + employmentStatusOptions +
                ", tradingPerQuarterOptions=" + tradingPerQuarterOptions +
                ", maxAddressRequired=" + maxAddressRequired +
                ", identityDocumentTypes=" + identityDocumentTypes +
                ", residenceDocumentTypes=" + residenceDocumentTypes +
                ", termsConditionsUrl='" + termsConditionsUrl + '\'' +
                ", riskWarningDisclaimerUrl='" + riskWarningDisclaimerUrl + '\'' +
                ", dataSharingAgreementUrl='" + dataSharingAgreementUrl + '\'' +
                ", minAge=" + minAge +
                ", currencies=" + currencies +
                '}';
    }
}
