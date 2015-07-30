package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.tradehero.th.api.users.UserProfileDTO;

public class KYCAyondoFormUtil
{
    private KYCAyondoFormUtil()
    {
        throw new IllegalArgumentException("No constructor");
    }

    public static boolean fillInBlanks(@NonNull KYCAyondoForm kycForm, @NonNull UserProfileDTO currentUserProfile)
    {
        boolean modified = false;
        if(TextUtils.isEmpty(kycForm.getUserName()) && !TextUtils.isEmpty(currentUserProfile.displayName))
        {
            kycForm.setUserName(currentUserProfile.displayName);
            modified = true;
        }
        if (TextUtils.isEmpty(kycForm.getFullName()) && !(TextUtils.isEmpty(currentUserProfile.firstName) && TextUtils.isEmpty(
                currentUserProfile.lastName)))
        {
            String fullName = TextUtils.concat(currentUserProfile.firstName + " ", currentUserProfile.lastName).toString().trim();
            kycForm.setFullName(fullName);
            modified = true;
        }
        if (TextUtils.isEmpty(kycForm.getEmail()))
        {
            if (!TextUtils.isEmpty(currentUserProfile.email))
            {
                kycForm.setEmail(currentUserProfile.email);
                modified = true;
            }
            else if (!TextUtils.isEmpty(currentUserProfile.paypalEmailAddress))
            {
                kycForm.setEmail(currentUserProfile.paypalEmailAddress);
                modified = true;
            }
        }
        return modified;
    }
}
