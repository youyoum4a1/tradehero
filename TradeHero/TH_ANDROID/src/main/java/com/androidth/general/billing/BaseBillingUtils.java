package com.androidth.general.billing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.utils.StringUtils;
import com.androidth.general.utils.VersionUtils;
import java.util.List;

public class BaseBillingUtils
{
    @NonNull public static Intent getSupportPurchaseReportEmailIntent(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @Nullable THProductPurchase purchase)
    {
        String deviceDetails = "\n\nThere appears to have been a problem reporting my purchase to TradeHero server\n\n-----\n" +
                StringUtils.join("\n", getAllPurchaseReportStrings(context, currentUserId, purchase)) +
                "\n-----\n";
        Intent intent = getIncompleteSupportPurchaseEmailIntent();
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    @NonNull public static List<String> getAllPurchaseReportStrings(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @Nullable THProductPurchase purchase)
    {
        List<String> reported = BillingUtils.getPurchaseReportStrings(purchase);
        reported.addAll(VersionUtils.getSupportEmailTraceParameters(context, currentUserId, true));

        return reported;
    }

    @NonNull public static Intent getIncompleteSupportPurchaseEmailIntent()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@tradehero.mobi"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "TradeHero - " + BillingUtils.getStoreName() + " Support");
        return intent;
    }


    @NonNull public static Intent getSupportPurchaseRestoreEmailIntent(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @NonNull Exception exception)
    {
        String deviceDetails = "\n\nThere appears to have been a problem restoring my purchase with " + BillingUtils.getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", VersionUtils.getExceptionStringsAndTraceParameters(context, currentUserId,
                        exception)) +
                "\n-----\n";
        Intent intent = getIncompleteSupportPurchaseEmailIntent();
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }
}
