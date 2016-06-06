package com.ayondo.academyapp.models.fastfill;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ResidenceScannedDocumentType
{
    UTILITY_BILL(R.string.residence_document_type_utility_bill, 1),
    BANK_STATEMENT(R.string.residence_document_type_bank_statement, 2)
    ;

    public static final Map<Integer, ResidenceScannedDocumentType> filedResidenceScannedDocumentTypes;
    @StringRes public final int dropDownText;
    public final int fromServer;

    ResidenceScannedDocumentType(@StringRes int dropDownText, int fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static ResidenceScannedDocumentType getResidenceScannedDocumentType(int fromServer)
    {
        ResidenceScannedDocumentType candidate = filedResidenceScannedDocumentTypes.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any ScannedDocumentType");
        }
        return candidate;
    }

    static
    {
        Map<Integer, ResidenceScannedDocumentType> map = new HashMap<>();
        for (ResidenceScannedDocumentType candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedResidenceScannedDocumentTypes = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused")
    @JsonValue int getFromServerCode()
    {
        return fromServer;
    }
}
