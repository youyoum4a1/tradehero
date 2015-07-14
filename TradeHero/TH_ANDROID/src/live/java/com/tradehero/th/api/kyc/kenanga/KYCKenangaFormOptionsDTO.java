package com.tradehero.th.api.kyc.kenanga;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.kyc.IdentityPromptInfoDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.market.Country;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KYCKenangaFormOptionsDTO implements KYCFormOptionsDTO
{
    public static final String KEY_KENANGA_TYPE = "KNG";

    @NonNull public final IdentityPromptInfoDTO identityPromptInfo;
    @NonNull public final List<Country> allowedMobilePhoneCountries;
    @NonNull public final List<Country> allowedNationalityCountries;
    @NonNull public final List<Country> allowedResidencyCountries;

    public KYCKenangaFormOptionsDTO(
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
