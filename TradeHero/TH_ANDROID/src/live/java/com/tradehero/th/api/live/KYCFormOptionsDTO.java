package com.tradehero.th.api.live;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.live.ayondo.KYCAyondoFormOptionsDTO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "optionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KYCAyondoFormOptionsDTO.class, name = KYCAyondoFormOptionsDTO.KEY_AYONDO_TYPE),
})
public interface KYCFormOptionsDTO extends DTO
{
}
