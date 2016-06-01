package com.ayondo.academy.fragments.live.ayondo;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.models.fastfill.ResidenceScannedDocumentType;
import java.util.ArrayList;
import java.util.List;

class ResidenceDocumentDTO
{
    @NonNull public final ResidenceScannedDocumentType residenceScannedDocumentType;
    @NonNull public final String text;

    public ResidenceDocumentDTO(@NonNull Resources resources, @NonNull ResidenceScannedDocumentType residenceScannedDocumentType)
    {
        this(residenceScannedDocumentType, resources.getString(residenceScannedDocumentType.dropDownText));
    }

    public ResidenceDocumentDTO(@NonNull ResidenceScannedDocumentType residenceScannedDocumentType, @NonNull String text)
    {
        this.residenceScannedDocumentType = residenceScannedDocumentType;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull static List<ResidenceDocumentDTO> createList(@NonNull Resources resources,
            @NonNull List<ResidenceScannedDocumentType> residenceScannedDocumentTypes)
    {
        List<ResidenceDocumentDTO> created = new ArrayList<>();
        for (ResidenceScannedDocumentType residenceScannedDocumentType : residenceScannedDocumentTypes)
        {
            created.add(new ResidenceDocumentDTO(resources, residenceScannedDocumentType));
        }
        return created;
    }
}
