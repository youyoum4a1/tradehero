package com.ayondo.academy.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.ayondo.academy.api.kyc.KYCAddress;
import com.ayondo.academy.api.users.UserProfileDTO;
import java.util.List;

public class KYCAyondoFormUtil
{
    private KYCAyondoFormUtil()
    {
        throw new IllegalArgumentException("No constructor");
    }

    public static boolean fillInBlanks(@NonNull KYCAyondoForm kycForm, @NonNull UserProfileDTO currentUserProfile)
    {
        boolean modified = false;

        if(!TextUtils.isEmpty(currentUserProfile.firstName))
        {
            kycForm.setFirstName(currentUserProfile.firstName);
        }

        if(!TextUtils.isEmpty(currentUserProfile.lastName))
        {
            kycForm.setLastName(currentUserProfile.lastName);
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

    public static boolean isValidToCreateAccount(@NonNull KYCAyondoForm form)
    {
        return form.getFirstName() != null
                && form.getLastName() != null
                && form.getMiddleName() != null
                && form.getAyondoGender() != null
                && form.getEmail() != null
                && form.getMobileNumber() != null
                && form.getNationality() != null
                && form.getDob() != null
                && form.isWorkedInFinance1Year() != null
                && form.isAttendedSeminarAyondo() != null
                && form.isHaveOtherQualification() != null
                && form.getAnnualIncomeRange() != null
                && form.getNetWorthRange() != null
                && form.getPercentNetWorthForInvestmentRange() != null
                && form.getEmploymentStatus() != null
                && form.isEmployerRegulatedFinancial() != null
                && form.getTradingPerQuarter() != null
                // We do not check for leveraged product trading experience
                && form.getIdentityDocumentType() != null
                && form.getIdentificationNumber() != null
                && areAddressValid(form.getAddresses())
                && form.isSubscribeOffers() != null
                && form.isSubscribeTradeNotifications() != null
                ;
    }

    public static boolean areAddressValid(@Nullable List<KYCAddress> addresses)
    {
        boolean valid = addresses != null;
        if (valid)
        {
            for (KYCAddress address : addresses)
            {
                valid &= address != null
                        && address.city != null
                        && address.country != null
                        && address.addressLine1 != null
                        && address.addressLine2 != null
                        && address.postalCode != null;
            }
        }
        return valid;
    }
}
