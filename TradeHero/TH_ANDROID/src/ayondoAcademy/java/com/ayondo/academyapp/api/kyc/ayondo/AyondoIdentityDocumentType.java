package com.ayondo.academyapp.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import com.ayondo.academyapp.models.fastfill.IdentityScannedDocumentType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AyondoIdentityDocumentType
{
    PASSPORT(R.string.identity_document_type_passport, "Passport", IdentityScannedDocumentType.PASSPORT),
    NATIONAL_ID(R.string.identity_document_type_id_card, "NationalId", IdentityScannedDocumentType.IDENTITY_CARD);

    public static final Map<String, AyondoIdentityDocumentType> filedAyondoIdentityDocumentTypesPerServerCode;
    public static final Map<IdentityScannedDocumentType, AyondoIdentityDocumentType> filedAyondoIdentityDocumentTypesPerScannedType;
    @StringRes public final int dropDownText;
    @NonNull public final String fromServer;
    @NonNull public final IdentityScannedDocumentType scannedDocumentType;

    AyondoIdentityDocumentType(@StringRes int dropDownText, @NonNull String fromServer, @NonNull IdentityScannedDocumentType scannedDocumentType)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
        this.scannedDocumentType = scannedDocumentType;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static AyondoIdentityDocumentType getAyondoIdentityDocumentType(@NonNull String fromServer)
    {
        AyondoIdentityDocumentType candidate = filedAyondoIdentityDocumentTypesPerServerCode.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any AyondoIdentityDocumentType");
        }
        return candidate;
    }

    static
    {
        Map<String, AyondoIdentityDocumentType> codeMap = new HashMap<>();
        Map<IdentityScannedDocumentType, AyondoIdentityDocumentType> scannedMap = new HashMap<>();
        for (AyondoIdentityDocumentType candidate : values())
        {
            if (codeMap.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            codeMap.put(candidate.fromServer, candidate);

            if (scannedMap.get(candidate.scannedDocumentType) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.scannedDocumentType + "' a second time");
            }
            scannedMap.put(candidate.scannedDocumentType, candidate);
        }
        filedAyondoIdentityDocumentTypesPerServerCode = Collections.unmodifiableMap(codeMap);
        filedAyondoIdentityDocumentTypesPerScannedType = Collections.unmodifiableMap(scannedMap);
    }

    @NonNull public static AyondoIdentityDocumentType getAyondoIdentityDocumentType(@NonNull IdentityScannedDocumentType scannedDocumentType)
    {
        AyondoIdentityDocumentType candidate = filedAyondoIdentityDocumentTypesPerScannedType.get(scannedDocumentType);
        if (candidate == null)
        {
            throw new IllegalArgumentException(scannedDocumentType + " does not match any AyondoIdentityDocumentType");
        }
        return candidate;
    }

    @SuppressWarnings("unused")
    @JsonValue @NonNull @Override public String toString()
    {
        return fromServer;
    }
}
