package com.tradehero.th.api.live;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.live.ayondo.KYCdAyondoFormOptionsDTO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "optionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KYCdAyondoFormOptionsDTO.class, name = KYCdAyondoFormOptionsDTO.KEY_AYONDO_TYPE),
})
public interface KYCdFormOptionsDTO extends DTO
{
}
