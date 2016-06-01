package com.ayondo.academy.api.kyc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.ayondo.academy.R;
import com.ayondo.academy.api.kyc.ayondo.KYCAyondoForm;
import com.ayondo.academy.api.kyc.ayondo.KYCAyondoFormUtil;
import com.ayondo.academy.api.users.UserProfileDTO;

public class KYCFormUtil
{
    private KYCFormUtil()
    {
        throw new IllegalArgumentException("No constructor");
    }

    @StringRes public static int getCallToActionText(@NonNull KYCForm kycForm)
    {
        if (kycForm instanceof KYCAyondoForm)
        {
            return R.string.live_call_to_action_string_ayondo;
        }
        else
        {
            return R.string.live_call_to_action_string_ayondo;
        }
    }

    public static boolean fillInBlanks(@Nullable KYCForm kycForm, @NonNull UserProfileDTO currentUserProfile)
    {
        boolean modified = false;
        if (kycForm instanceof KYCAyondoForm)
        {
            modified = KYCAyondoFormUtil.fillInBlanks((KYCAyondoForm) kycForm, currentUserProfile);
        }
        return modified;
    }

    public static KYCForm from(KYCForm other)
    {
        if (other instanceof KYCAyondoForm)
        {
            return new KYCAyondoForm();
        }
        else
        {
            return new EmptyKYCForm();
        }
    }
}
