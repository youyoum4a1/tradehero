package com.androidth.general.models.fastfill;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jumio.nv.enums.EPassportStatus;
import com.neovisionaries.i18n.CountryCode;

import java.util.Date;

public interface ScannedDocument
{
    @NonNull
    ScanReference getScanReference();

    @Nullable String getFirstName();
    @Nullable String getMiddleName();
    @Nullable String getLastName();
    @Nullable String getFullName();

    @Nullable Gender getGender();
    @Nullable Date getDob();

    @Nullable IdentityScannedDocumentType getSelectedDocumentType();
    @Nullable String getPersonalNumber();
    @Nullable String getIdNumber();
    @Nullable CountryCode getSelectedCountry();
    @Nullable CountryCode getIssuingCountry();
    @Nullable CountryCode getOriginatingCountry();
    @Nullable Date getIssuingDate();
    @Nullable Date getExpiryDate();
    @Nullable EPassportStatus getEPassportStatus();
    //@Nullable NetverifyMrzData getMrzData(); // TODO better
    int getNameDistance();
    @Nullable String getOptionalData1();
    @Nullable String getOptionalData2();
    //@Nullable String getStreet();
    @Nullable String getCity();
    @Nullable String getPostalCode();
    //@Nullable String getState();

    boolean isNameMatch();
    @Nullable Boolean getLivenessDetected();
}