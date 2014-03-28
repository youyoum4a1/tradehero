package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.BillingUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.VersionUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 10:55 AM To change this template use File | Settings | File Templates. */
public class GooglePlayUtils
    extends BillingUtils<
        IABSKU,
        THIABProductDetail,
        THIABOrderId,
        THIABPurchase>
{
    @Inject public GooglePlayUtils()
    {
        super();
    }

    @Override public String getStoreName()
    {
        return "GooglePlay";
    }

    @Override protected List<String> getPurchaseReportStrings(THIABPurchase purchase)
    {
        List<String> reported = new ArrayList<>();

        if (purchase != null)
        {
            GooglePlayPurchaseDTO googlePlayPurchaseDTO = purchase.getGooglePlayPurchaseDTO();
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
