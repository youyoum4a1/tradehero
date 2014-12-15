package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.billing.GooglePlayPurchaseReportDTO;
import com.tradehero.th.billing.BillingUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.VersionUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

class GooglePlayUtils
    extends BillingUtils<
        IABSKU,
        THIABProductDetail,
        THIABOrderId,
        THIABPurchase>
{
    //<editor-fold desc="Constructors">
    @Inject public GooglePlayUtils()
    {
        super();
    }
    //</editor-fold>

    @Override @NonNull public String getStoreName()
    {
        return "GooglePlay";
    }

    @Override @NonNull protected List<String> getPurchaseReportStrings(@Nullable THIABPurchase purchase)
    {
        List<String> reported = new ArrayList<>();

        if (purchase != null)
        {
            GooglePlayPurchaseReportDTO googlePlayPurchaseDTO = purchase.getPurchaseReportDTO();
            reported.add("data:");
            reported.add(googlePlayPurchaseDTO.googlePlayData);
            reported.add("signature:");
            reported.add(googlePlayPurchaseDTO.googlePlaySignature);
            reported.add("-----");
        }
        return reported;
    }

    @NonNull public Intent getSupportAlreadyOwnedIntent(
            @NonNull Context context,
            @NonNull Throwable exception)
    {
        String deviceDetails = "\n\nI already own an SKU I am trying to purchase with " + getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", VersionUtils.getExceptionStringsAndTraceParameters(context, exception)) +
                "\n-----\n";
        Intent intent = getIncompleteSupportPurchaseEmailIntent(context);
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    @NonNull public Intent getSupportDeveloperErrorIntent(
            @NonNull Context context,
            @NonNull Throwable exception)
    {
        String deviceDetails = "\n\nDeveloper error reported by " + getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", VersionUtils.getExceptionStringsAndTraceParameters(context, exception)) +
                "\n-----\n";
        Intent intent = getIncompleteSupportPurchaseEmailIntent(context);
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    @NonNull public Intent getSupportPurchaseConsumeEmailIntent(
            @NonNull Context context,
            @NonNull Throwable exception)
    {
        String deviceDetails = "\n\nThere appears to have been a problem consuming my purchase with " + getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", VersionUtils.getExceptionStringsAndTraceParameters(context, exception)) +
                "\n-----\n";
        Intent intent = getIncompleteSupportPurchaseEmailIntent(context);
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }
}
