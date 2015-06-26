package com.tradehero.th.models.kyc;

import android.support.annotation.NonNull;
import com.tradehero.th.models.fastfill.ScannedDocument;
import java.util.List;

public class EmptyKYCForm implements KYCForm
{
    public static final String KEY_EMPTY_TYPE = "EMPTY";

    @Override public void pickFrom(@NonNull ScannedDocument scannedDocument)
    {
    }

    @Override public void setStepStatuses(@NonNull List<StepStatus> stepStatuses)
    {
    }
}
