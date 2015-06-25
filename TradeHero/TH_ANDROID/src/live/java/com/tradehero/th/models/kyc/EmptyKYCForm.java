package com.tradehero.th.models.kyc;

import android.support.annotation.NonNull;
import com.tradehero.th.models.fastfill.ScannedDocument;

public class EmptyKYCForm implements KYCForm
{
    public static final String KEY_EMPTY_TYPE = "EMPTY";

    @Override public void pickFrom(@NonNull ScannedDocument scannedDocument)
    {
    }
}
