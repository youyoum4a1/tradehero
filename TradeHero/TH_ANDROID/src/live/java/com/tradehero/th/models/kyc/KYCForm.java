package com.tradehero.th.models.kyc;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "formType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmptyKYCForm.class, name = EmptyKYCForm.KEY_EMPTY_TYPE),
        @JsonSubTypes.Type(value = KYCAyondoForm.class, name = KYCAyondoForm.KEY_AYONDO_TYPE),
}) public interface KYCForm
{
    void pickFrom(@NonNull ScannedDocument scannedDocument);

    void setStepStatuses(@NonNull List<StepStatus> stepStatuses);
}
