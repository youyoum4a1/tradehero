package com.androidth.general.models.fastfill;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum IdentityScannedDocumentType
{
    PASSPORT(R.string.identity_document_type_passport, 1),
    DRIVER_LICENSE(R.string.identity_document_type_driver_license, 2),
    IDENTITY_CARD(R.string.identity_document_type_id_card, 3),
    ;

    public static final Map<Integer, IdentityScannedDocumentType> filedIdentityScannedDocumentTypes;
    @StringRes public final int dropDownText;
    public final int fromServer;

    IdentityScannedDocumentType(@StringRes int dropDownText, int fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static IdentityScannedDocumentType getIdentityScannedDocumentType(int fromServer)
    {
        IdentityScannedDocumentType candidate = filedIdentityScannedDocumentTypes.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any ScannedDocumentType");
        }
        return candidate;
    }

    static
    {
        Map<Integer, IdentityScannedDocumentType> map = new HashMap<>();
        for (IdentityScannedDocumentType candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedIdentityScannedDocumentTypes = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused")
    @JsonValue int getFromServerCode()
    {
        return fromServer;
    }
}
