package com.tradehero.th.api.kyc.kenanga;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.fastfill.IdentityScannedDocumentType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KYCKenangaFormOptionsDTO implements KYCFormOptionsDTO
{
    public static final String KEY_KENANGA_TYPE = "KNG";

    @NonNull public final List<Country> allowedMobilePhoneCountries;
    @NonNull public final List<Country> allowedNationalityCountries;
    @NonNull public final List<Country> allowedResidencyCountries;
    @NonNull public final List<IdentityScannedDocumentType> identityDocumentTypes;

    public KYCKenangaFormOptionsDTO(
            @JsonProperty("allowedMobilePhoneCountries") @Nullable List<Country> allowedMobilePhoneCountries,
            @JsonProperty("allowedNationalityCountries") @Nullable List<Country> allowedNationalityCountries,
            @JsonProperty("allowedResidencyCountries") @Nullable List<Country> allowedResidencyCountries,
            @JsonProperty("identityDocumentTypes") @NonNull List<IdentityScannedDocumentType> identityDocumentTypes)
    {
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
        this.identityDocumentTypes = Collections.unmodifiableList(identityDocumentTypes);
    }

    @Override @NonNull public List<IdentityScannedDocumentType> getIdentityDocumentTypes()
    {
        return identityDocumentTypes;
    }
}
