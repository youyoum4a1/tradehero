package com.androidth.general.api.kyc;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.kyc.kenanga.KYCKenangaFormOptionsDTO;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "optionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KYCAyondoFormOptionsDTO.class, name = KYCAyondoFormOptionsDTO.KEY_AYONDO_TYPE),
        @JsonSubTypes.Type(value = KYCKenangaFormOptionsDTO.class, name = KYCKenangaFormOptionsDTO.KEY_KENANGA_TYPE),
})
public interface KYCFormOptionsDTO extends DTO
{
    @NonNull List<IdentityScannedDocumentType> getIdentityDocumentTypes();
}
