package com.ayondo.academy.api.kyc;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.ayondo.academy.api.kyc.kenanga.KYCKenangaFormOptionsDTO;
import com.ayondo.academy.models.fastfill.IdentityScannedDocumentType;
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
