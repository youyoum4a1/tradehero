package com.tradehero.th.models.kyc;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.models.kyc.sgp.KYCSingaporeForm;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "formType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmptyKYCForm.class, name = EmptyKYCForm.KEY_EMPTY_TYPE),
        @JsonSubTypes.Type(value = KYCSingaporeForm.class, name = KYCSingaporeForm.KEY_SINGAPORE_TYPE),
}) public interface KYCForm
{
    void pickFrom(@NonNull ScannedDocument scannedDocument);
}
