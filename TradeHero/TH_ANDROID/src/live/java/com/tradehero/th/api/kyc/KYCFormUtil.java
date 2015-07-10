package com.tradehero.th.api.kyc;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;

public class KYCFormUtil
{
    @StringRes public static int getCallToActionText(@NonNull KYCForm kycForm)
    {
        if (kycForm instanceof KYCAyondoForm)
        {
            return R.string.live_call_to_action_string_ayondo;
        }
        throw new IllegalArgumentException("Unknown call to action text for type: " + kycForm);
    }
}
