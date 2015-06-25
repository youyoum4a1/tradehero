package com.tradehero.th.models.kyc.sgp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.models.kyc.KYCForm;

public class KYCSingaporeForm implements KYCForm
{
    public static final String KEY_SINGAPORE_TYPE = "SGP";

    @Nullable public String firstName;
    @Nullable public String lastName;
    @Nullable public String email;
    public boolean emailVerified;
    @JsonProperty("mobileCC")
    @Nullable public Integer mobileNumberCountryCode;
    @Nullable public Integer mobileNumber;
    public boolean mobileVerified;
    @Nullable public CountryCode nationality;

    @Override public void pickFrom(@NonNull ScannedDocument scannedDocument)
    {
        String firstName = scannedDocument.getFirstName();
        if (firstName != null)
        {
            this.firstName = firstName;
        }

        String lastName = scannedDocument.getLastName();
        if (lastName != null)
        {
            this.lastName = lastName;
        }

        CountryCode issuingCountry = scannedDocument.getIssuingCountry();
        if (issuingCountry != null)
        {
            this.nationality = issuingCountry;
        }
    }
}