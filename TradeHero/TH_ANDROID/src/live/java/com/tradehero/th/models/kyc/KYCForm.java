package com.tradehero.th.models.kyc;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.th.models.kyc.sgp.KYCSingaporeForm;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "formType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KYCSingaporeForm.class, name = KYCSingaporeForm.KEY_SINGAPORE_TYPE),
})public interface KYCForm
{
}
