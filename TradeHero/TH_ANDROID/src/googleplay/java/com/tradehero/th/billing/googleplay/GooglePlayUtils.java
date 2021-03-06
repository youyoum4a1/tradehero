package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.content.Intent;

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

    @Override public String getStoreName()
    {
        return "GooglePlay";
    }

    @Override protected List<String> getPurchaseReportStrings(THIABPurchase purchase)
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

    public Intent getSupportPurchaseConsumeEmailIntent(Context context, Exception exception)
    {
        String deviceDetails = "\n\nThere appears to have been a problem consuming my purchase with " + getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", VersionUtils.getExceptionStringsAndTraceParameters(context, exception)) +
                "\n-----\n";
        Intent intent = getIncompleteSupportPurchaseEmailIntent(context);
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }
}
