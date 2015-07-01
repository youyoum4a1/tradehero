package com.tradehero.th.models.kyc;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;

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

    @LayoutRes public static int getCallToActionLayout(@NonNull KYCForm kycForm)
    {
        if(kycForm.getCountry().equals(Country.AU))
        {
            return R.layout.activity_identity_prompt_au;
        }
        else if(kycForm.getCountry().equals(Country.SG))
        {
            return R.layout.activity_identity_prompt_sg;
        }
        throw new IllegalArgumentException("Unknown call to action layout for type: " + kycForm);
    }
}
