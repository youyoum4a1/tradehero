package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.kyc.AnnualIncomeRange;
import com.tradehero.th.api.kyc.EmploymentStatus;
import com.tradehero.th.api.kyc.IdentityPromptInfoDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.NetWorthRange;
import com.tradehero.th.api.kyc.PercentNetWorthForInvestmentRange;
import com.tradehero.th.api.market.Country;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KYCAyondoFormOptionsDTO implements KYCFormOptionsDTO
{
    public static final String KEY_AYONDO_TYPE = "Ayondo";

    @NonNull public final IdentityPromptInfoDTO identityPromptInfo;
    @NonNull public final List<Country> allowedMobilePhoneCountries;
    @NonNull public final List<Country> allowedNationalityCountries;
    @NonNull public final List<Country> allowedResidencyCountries;
    @NonNull public final List<AnnualIncomeRange> annualIncomeOptions;
    @NonNull public final List<NetWorthRange> netWorthOptions;
    @NonNull public final List<PercentNetWorthForInvestmentRange> percentNetWorthOptions;
    @NonNull public final List<EmploymentStatus> employmentStatusOptions;

    public KYCAyondoFormOptionsDTO(
            @JsonProperty("identityPromptInfo") @NonNull IdentityPromptInfoDTO identityPromptInfo,
            @JsonProperty("allowedMobilePhoneCountries") @Nullable List<Country> allowedMobilePhoneCountries,
            @JsonProperty("allowedNationalityCountries") @Nullable List<Country> allowedNationalityCountries,
            @JsonProperty("allowedResidencyCountries") @Nullable List<Country> allowedResidencyCountries,
            @JsonProperty("annualIncomeOptions") @NonNull List<AnnualIncomeRange> annualIncomeOptions,
            @JsonProperty("netWorthOptions") @NonNull List<NetWorthRange> netWorthOptions,
            @JsonProperty("percentNetWorthOptions") @NonNull List<PercentNetWorthForInvestmentRange> percentNetWorthOptions,
            @JsonProperty("employmentStatusOptions") @NonNull List<EmploymentStatus> employmentStatusOptions)
    {
        this.identityPromptInfo = identityPromptInfo;
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
    }

    @NonNull @Override public IdentityPromptInfoDTO getIdentityPromptInfo()
    {
        return identityPromptInfo;
    }
}
