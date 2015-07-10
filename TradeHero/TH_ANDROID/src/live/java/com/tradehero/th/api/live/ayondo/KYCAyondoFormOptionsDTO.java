package com.tradehero.th.api.live.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.live.IdentityPromptInfoDTO;
import com.tradehero.th.api.live.KYCFormOptionsDTO;
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
    @NonNull public final List<String> annualIncomeOptions;
    @NonNull public final List<String> netWorthOptions;
    @NonNull public final List<String> percentNetWorthOptions;
    @NonNull public final List<String> employmentStatusIncomeOptions;

    public KYCAyondoFormOptionsDTO(
            @JsonProperty("identityPromptInfo") @NonNull IdentityPromptInfoDTO identityPromptInfo,
            @JsonProperty("allowedMobilePhoneCountries") @Nullable List<Country> allowedMobilePhoneCountries,
            @JsonProperty("allowedNationalityCountries") @Nullable List<Country> allowedNationalityCountries,
            @JsonProperty("allowedResidencyCountries") @Nullable List<Country> allowedResidencyCountries,
            @JsonProperty("annualIncomeOptions") @NonNull List<String> annualIncomeOptions,
            @JsonProperty("netWorthOptions") @NonNull List<String> netWorthOptions,
            @JsonProperty("percentNetWorthOptions") @NonNull List<String> percentNetWorthOptions,
            @JsonProperty("employmentStatusIncomeOptions") @NonNull List<String> employmentStatusIncomeOptions)
    {
        this.identityPromptInfo = identityPromptInfo;
        this.annualIncomeOptions = annualIncomeOptions;
        this.netWorthOptions = netWorthOptions;
        this.percentNetWorthOptions = percentNetWorthOptions;
        this.employmentStatusIncomeOptions = employmentStatusIncomeOptions;
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
    }

    @NonNull @Override public IdentityPromptInfoDTO getIdentityPromptInfo()
    {
        return identityPromptInfo;
    }
}
