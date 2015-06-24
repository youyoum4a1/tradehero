package com.tradehero.th.models.fastfill;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.neovisionaries.i18n.CountryCode;
import java.util.Date;

public interface ScannedDocument
{
    @NonNull String getScanReference();

    @Nullable String getFirstName();
    @Nullable String getMiddleName();
    @Nullable String getLastName();

    @Nullable Gender getGender();
    @Nullable Date getDob();

    @Nullable ScannedDocumentType getSelectedDocumentType();
    @Nullable String getPersonalNumber();
    @Nullable String getIdNumber();
    @Nullable CountryCode getSelectedCountry();
    @Nullable CountryCode getIssuingCountry();
    @Nullable CountryCode getOriginatingCountry();
    @Nullable Date getIssuingDate();
    @Nullable Date getExpiryDate();
    //@Nullable NetverifyMrzData getMrzData(); // TODO better
    int getNameDistance();
    @Nullable String getOptionalData1();
    @Nullable String getOptionalData2();
    @Nullable String getStreet();
    @Nullable String getCity();
    @Nullable String getPostalCode();
    @Nullable String getState();

    boolean isNameMatch();
    @Nullable Boolean getLivenessDetected();
}
