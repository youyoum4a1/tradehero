package com.androidth.general.billing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.billing.GooglePlayPurchaseReportDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.utils.StringUtils;
import com.androidth.general.utils.VersionUtils;
import java.util.ArrayList;
import java.util.List;

public class BillingUtils
{
    @NonNull public static String getStoreName()
    {
        return "GooglePlay";
    }

    @NonNull protected static List<String> getPurchaseReportStrings(@Nullable THProductPurchase purchase)
    {
        List<String> reported = new ArrayList<>();

        if (purchase != null)
        {
            GooglePlayPurchaseReportDTO googlePlayPurchaseDTO = ((THIABPurchase) purchase).getPurchaseReportDTO();
            reported.add("data:");
            reported.add(googlePlayPurchaseDTO.googlePlayData);
            reported.add("signature:");
            reported.add(googlePlayPurchaseDTO.googlePlaySignature);
            reported.add("-----");
        }
        return reported;
    }

    @NonNull public static Intent getSupportAlreadyOwnedIntent(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @NonNull Throwable exception)
    {
        String deviceDetails = "\n\nI already own an SKU I am trying to purchase with " + getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", VersionUtils.getExceptionStringsAndTraceParameters(context, currentUserId, exception)) +
                "\n-----\n";
        Intent intent = BaseBillingUtils.getIncompleteSupportPurchaseEmailIntent();
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    @NonNull public static Intent getSupportDeveloperErrorIntent(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @NonNull Throwable exception)
    {
        String deviceDetails = "\n\nDeveloper error reported by " + getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", VersionUtils.getExceptionStringsAndTraceParameters(context, currentUserId, exception)) +
                "\n-----\n";
        Intent intent = BaseBillingUtils.getIncompleteSupportPurchaseEmailIntent();
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    @NonNull public static Intent getSupportPurchaseConsumeEmailIntent(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @NonNull Throwable exception)
    {
        String deviceDetails = "\n\nThere appears to have been a problem consuming my purchase with " + getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", VersionUtils.getExceptionStringsAndTraceParameters(context, currentUserId, exception)) +
                "\n-----\n";
        Intent intent = BaseBillingUtils.getIncompleteSupportPurchaseEmailIntent();
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }
}
