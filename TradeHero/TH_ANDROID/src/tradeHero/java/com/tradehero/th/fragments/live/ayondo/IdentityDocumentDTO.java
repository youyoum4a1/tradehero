package com.androidth.general.fragments.live.ayondo;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import java.util.ArrayList;
import java.util.List;

class IdentityDocumentDTO
{
    @NonNull public final IdentityScannedDocumentType identityScannedDocumentType;
    @NonNull public final String text;

    public IdentityDocumentDTO(@NonNull Resources resources, @NonNull IdentityScannedDocumentType identityScannedDocumentType)
    {
        this(identityScannedDocumentType, resources.getString(identityScannedDocumentType.dropDownText));
    }

    public IdentityDocumentDTO(@NonNull IdentityScannedDocumentType identityScannedDocumentType, @NonNull String text)
    {
        this.identityScannedDocumentType = identityScannedDocumentType;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull static List<IdentityDocumentDTO> createList(@NonNull Resources resources,
            @NonNull List<IdentityScannedDocumentType> identityScannedDocumentTypes)
    {
        List<IdentityDocumentDTO> created = new ArrayList<>();
        for (IdentityScannedDocumentType identityScannedDocumentType : identityScannedDocumentTypes)
        {
            created.add(new IdentityDocumentDTO(resources, identityScannedDocumentType));
        }
        return created;
    }
}
