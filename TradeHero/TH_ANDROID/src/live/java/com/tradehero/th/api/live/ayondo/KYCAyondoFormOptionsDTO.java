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

    public KYCAyondoFormOptionsDTO(
            @JsonProperty("identityPromptInfo") @NonNull IdentityPromptInfoDTO identityPromptInfo,
            @JsonProperty("allowedMobilePhoneCountries") @Nullable List<Country> allowedMobilePhoneCountries,
            @JsonProperty("allowedNationalityCountries") @Nullable List<Country> allowedNationalityCountries,
            @JsonProperty("allowedResidencyCountries") @Nullable List<Country> allowedResidencyCountries)
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
    }

    @NonNull @Override public IdentityPromptInfoDTO getIdentityPromptInfo()
    {
        return identityPromptInfo;
    }
}
