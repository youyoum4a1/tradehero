package com.ayondo.academy.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ayondo.academy.api.kyc.AnnualIncomeRange;
import com.ayondo.academy.api.kyc.EmploymentStatus;
import com.ayondo.academy.api.kyc.KYCAddress;
import com.ayondo.academy.api.kyc.NetWorthRange;
import com.ayondo.academy.api.kyc.PercentNetWorthForInvestmentRange;
import com.ayondo.academy.api.kyc.StepStatus;
import com.ayondo.academy.api.kyc.StepStatusesDTO;
import com.ayondo.academy.api.kyc.TradingPerQuarter;
import com.ayondo.academy.api.live.CountryUtil;
import java.util.Arrays;

public class DummyKYCAyondoUtil
{
    @NonNull public static StepStatusesDTO getSteps(@NonNull KYCAyondoForm kycForm)
    {
        return new StepStatusesDTO(Arrays.asList(
                getStep1(kycForm),
                getStep2(kycForm),
                getStep3(kycForm),
                getStep4(kycForm),
                getStep5(kycForm)));
    }

    @NonNull public static StepStatus getStep1(@NonNull KYCAyondoForm kycForm)
    {
        return (kycForm.getGender() != null
                && kycForm.getFirstName() != null
                && kycForm.getLastName() != null
                && kycForm.getEmail() != null
                && kycForm.getPhonePrimaryCountryCode() != null
                && kycForm.getMobileNumber() != null
                && kycForm.getVerifiedMobileNumberDialingPrefix() != null
                && kycForm.getVerifiedMobileNumber() != null
                && Integer.valueOf(CountryUtil.getPhoneCodePlusLeadingDigits(kycForm.getPhonePrimaryCountryCode()))
                .equals(kycForm.getVerifiedMobileNumberDialingPrefix())
                && kycForm.getMobileNumber().equals(kycForm.getVerifiedMobileNumber())
                && kycForm.getNationality() != null
                && kycForm.getResidency() != null
                && kycForm.getDob() != null)
                ? StepStatus.COMPLETE
                : StepStatus.UNSTARTED;
    }

    @NonNull public static StepStatus getStep2(@NonNull KYCAyondoForm kycForm)
    {
        return (kycForm.getAnnualIncomeRange() != null
                && !kycForm.getAnnualIncomeRange().equals(AnnualIncomeRange.EMPTY)
                && kycForm.getNetWorthRange() != null
                && !kycForm.getNetWorthRange().equals(NetWorthRange.EMPTY)
                && kycForm.getPercentNetWorthForInvestmentRange() != null
                && !kycForm.getPercentNetWorthForInvestmentRange().equals(PercentNetWorthForInvestmentRange.EMPTY)
                && kycForm.getEmploymentStatus() != null
                && !kycForm.getEmploymentStatus().equals(EmploymentStatus.EMPTY))
                ? StepStatus.COMPLETE
                : StepStatus.UNSTARTED;
    }

    @NonNull public static StepStatus getStep3(@NonNull KYCAyondoForm kycForm)
    {
        return (kycForm.getTradingPerQuarter() != null
                && !kycForm.getTradingPerQuarter().equals(TradingPerQuarter.EMPTY))
                ? StepStatus.COMPLETE
                : StepStatus.UNSTARTED;
    }

    @NonNull public static StepStatus getStep4(@NonNull KYCAyondoForm kycForm)
    {
        StepStatus stepStatus = StepStatus.UNSTARTED;
        if (kycForm.getAddresses() != null)
        {
            if (kycForm.getAddresses().size() > 0)
            {
                KYCAddress address1 = kycForm.getAddresses().get(0);
                boolean complete = !TextUtils.isEmpty(address1.addressLine1)
                        && !TextUtils.isEmpty(address1.city)
                        && !TextUtils.isEmpty(address1.postalCode);

                if (address1.lessThanAYear)
                {
                    if (kycForm.getAddresses().size() > 1)
                    {
                        KYCAddress address2 = kycForm.getAddresses().get(1);
                        complete &= !TextUtils.isEmpty(address2.addressLine1)
                                && !TextUtils.isEmpty(address2.city)
                                && !TextUtils.isEmpty(address2.postalCode);
                    }
                    else
                    {
                        complete = false;
                    }
                }
                stepStatus = complete ? StepStatus.COMPLETE : StepStatus.UNSTARTED;
            }
        }
        return stepStatus;
    }

    @NonNull public static StepStatus getStep5(@NonNull KYCAyondoForm kycForm)
    {
        return ((kycForm.getScanReference() != null) || (kycForm.getIdentityDocumentType() != null
                && kycForm.getIdentityDocumentUrl() != null))
                && ((kycForm.getNeedResidencyDocument() != null && !kycForm.getNeedResidencyDocument()) || (kycForm.getResidenceDocumentType()
                != null && kycForm.getResidenceDocumentUrl() != null))
                && (kycForm.isAgreeTermsConditions() != null && kycForm.isAgreeTermsConditions().equals(true))
                && (kycForm.isAgreeRisksWarnings() != null && kycForm.isAgreeRisksWarnings().equals(true))
                && (kycForm.isAgreeDataSharing() != null && kycForm.isAgreeDataSharing().equals(true))
                ? StepStatus.COMPLETE
                : StepStatus.UNSTARTED;
    }
}
