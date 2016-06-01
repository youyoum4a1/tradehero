package com.ayondo.academy.api.kyc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ayondo.academy.api.kyc.ayondo.KYCAyondoForm;
import com.ayondo.academy.api.market.Country;
import com.ayondo.academy.models.fastfill.ScanReference;
import com.ayondo.academy.models.fastfill.ScannedDocument;
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
    @StringRes int getBrokerNameResId();

    @Nullable Country getCountry();

    @Nullable ScanReference getScanReference();

    void pickFrom(@NonNull ScannedDocument scannedDocument);

    void pickFrom(@NonNull KYCForm other);

    void setStepStatuses(@NonNull List<StepStatus> stepStatuses);

    @Nullable List<StepStatus> getStepStatuses();

    boolean equals(@Nullable Object other);
}
