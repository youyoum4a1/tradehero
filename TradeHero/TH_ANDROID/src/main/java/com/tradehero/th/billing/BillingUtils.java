package com.tradehero.th.billing;

import android.content.Context;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.VersionUtils;

import java.util.List;

abstract public class BillingUtils<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
{
    @NonNull protected final VersionUtils versionUtils;

    //<editor-fold desc="Constructors">
    public BillingUtils(@NonNull VersionUtils versionUtils)
    {
        super();
        this.versionUtils = versionUtils;
    }
    //</editor-fold>

    @NonNull abstract public String getStoreName();

    @NonNull public Intent getSupportPurchaseReportEmailIntent(
            @NonNull Context context,
            @Nullable ProductPurchaseType purchase)
    {
        String deviceDetails = "\n\nThere appears to have been a problem reporting my purchase to TradeHero server\n\n-----\n" +
                StringUtils.join("\n", getAllPurchaseReportStrings(context, purchase)) +
                "\n-----\n";
        Intent intent = getIncompleteSupportPurchaseEmailIntent(context);
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    @NonNull public List<String> getAllPurchaseReportStrings(
            @NonNull Context context,
            @Nullable ProductPurchaseType purchase)
    {
        List<String> reported = getPurchaseReportStrings(purchase);
        reported.addAll(versionUtils.getSupportEmailTraceParameters(context, true));

        return reported;
    }

    @NonNull abstract protected List<String> getPurchaseReportStrings(@Nullable ProductPurchaseType purchase);

    @NonNull public Intent getIncompleteSupportPurchaseEmailIntent(@NonNull Context context)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@tradehero.mobi"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "TradeHero - " + getStoreName() + " Support");
        return intent;
    }


    @NonNull public Intent getSupportPurchaseRestoreEmailIntent(
            @NonNull Context context,
            @NonNull Exception exception)
    {
        String deviceDetails = "\n\nThere appears to have been a problem restoring my purchase with " + getStoreName() + "\n\n-----\n" +
                StringUtils.join("\n", versionUtils.getExceptionStringsAndTraceParameters(context,
                        exception)) +
                "\n-----\n";
        Intent intent = getIncompleteSupportPurchaseEmailIntent(context);
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }
}
