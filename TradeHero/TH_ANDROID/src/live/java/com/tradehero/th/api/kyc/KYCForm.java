package com.tradehero.th.api.kyc;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.fastfill.ScannedDocument;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
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
    @StringRes int getBrokerName();

    @NonNull Country getCountry();

    void pickFrom(@NonNull ScannedDocument scannedDocument);

    void pickFrom(@NonNull KYCForm other);

    void setStepStatuses(@NonNull List<StepStatus> stepStatuses);

    List<StepStatus> getStepStatuses();

    boolean hasSameFields(@NonNull KYCForm kycForm);
}