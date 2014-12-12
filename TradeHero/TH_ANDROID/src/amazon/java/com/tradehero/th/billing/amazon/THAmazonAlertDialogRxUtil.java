package com.tradehero.th.billing.amazon;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonFetchInventoryFailedException;
import com.tradehero.common.billing.amazon.exception.AmazonFetchInventoryUnsupportedException;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseFailedException;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseUnsupportedException;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingAlertDialogRxUtil;
import com.tradehero.th.fragments.billing.THAmazonSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THAmazonStoreProductDetailView;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCacheRx;
import com.tradehero.th.rx.dialog.AlertDialogButtonConstants;
import com.tradehero.th.rx.dialog.AlertDialogButtonHandler;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.VersionUtils;
import java.util.HashMap;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public class THAmazonAlertDialogRxUtil extends THBillingAlertDialogRxUtil<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonLogicHolderRx,
        THAmazonStoreProductDetailView,
        THAmazonSKUDetailAdapter,
        THAmazonOrderId,
        THAmazonPurchase>
        implements AmazonAlertDialogRxUtil
{
    @NonNull protected final THAmazonPurchaseCacheRx thAmazonPurchaseCache;
    @NonNull protected final AmazonStoreUtils amazonStoreUtils;

    //<editor-fold desc="Constructors">
    @Inject public THAmazonAlertDialogRxUtil(
            @NonNull Analytics analytics,
            @NonNull ActivityUtil activityUtil,
            @NonNull THAmazonPurchaseCacheRx thAmazonPurchaseCache,
            @NonNull AmazonStoreUtils amazonStoreUtils)
    {
        super(analytics, activityUtil);
        this.thAmazonPurchaseCache = thAmazonPurchaseCache;
        this.amazonStoreUtils = amazonStoreUtils;
    }
    //</editor-fold>

    //<editor-fold desc="SKU related">
    @Override @NonNull protected THAmazonSKUDetailAdapter createProductDetailAdapter(
            @NonNull Activity activity,
            @NonNull ProductIdentifierDomain skuDomain)
    {
        return new THAmazonSKUDetailAdapter(activity, skuDomain);
    }

    @Override @NonNull public HashMap<ProductIdentifier, Boolean> getEnabledItems()
    {
        HashMap<ProductIdentifier, Boolean> enabledItems = new HashMap<>();
        for (THAmazonPurchase value : thAmazonPurchaseCache.getValues())
        {
            Timber.d("Disabling %s", value);
            enabledItems.put(value.getProductIdentifier(), false);
        }
        return enabledItems;
    }
    //</editor-fold>

    @NonNull @Override public Observable<Pair<DialogInterface, Integer>> popErrorAndHandle(
            @NonNull Context activityContext,
            @NonNull Throwable throwable)
    {
        if (throwable instanceof AmazonFetchInventoryFailedException)
        {
            return popInventoryFailedAndHandle(activityContext, throwable);
        }
        if (throwable instanceof AmazonFetchInventoryUnsupportedException)
        {
            return popInventoryNotSupportedAndHandle(activityContext, throwable);
        }
        if (throwable instanceof AmazonPurchaseFailedException)
        {
            return popPurchaseFailedAndHandle(activityContext, throwable);
        }
        if (throwable instanceof AmazonPurchaseUnsupportedException)
        {
            return popPurchaseUnsupportedAndHandle(activityContext, throwable);
        }
        return super.popErrorAndHandle(activityContext, throwable);
    }

    //<editor-fold desc="Inventory Fetch related">
    @Override @NonNull public Observable<Pair<DialogInterface, Integer>> popInventoryFailedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popInventoryFailed(activityContext)
                .flatMap(new AlertDialogButtonHandler(
                        AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX,
                        () -> sendSupportEmailBillingGenericError(activityContext, throwable)));
    }

    @Override @NonNull public Observable<Pair<DialogInterface, Integer>> popInventoryFailed(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.amazon_store_billing_inventory_failed_error_window_title)
                        .setMessage(R.string.amazon_store_billing_inventory_failed_error_window_description))
                .setPositiveButton(R.string.amazon_store_billing_inventory_failed_error_ok)
                .setNegativeButton(R.string.amazon_store_billing_inventory_failed_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }

    @Override @NonNull public Observable<Pair<DialogInterface, Integer>> popInventoryNotSupportedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popInventoryNotSupported(activityContext)
                .flatMap(new AlertDialogButtonHandler(
                        AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX,
                        () -> sendSupportEmailBillingGenericError(activityContext, throwable)));
    }

    @Override @NonNull public Observable<Pair<DialogInterface, Integer>> popInventoryNotSupported(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.amazon_store_billing_inventory_unsupported_error_window_title)
                        .setMessage(R.string.amazon_store_billing_inventory_unsupported_error_window_description))
                .setPositiveButton(R.string.amazon_store_billing_inventory_unsupported_error_ok)
                .setNegativeButton(R.string.amazon_store_billing_inventory_unsupported_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Related">
    @NonNull public Observable<Pair<DialogInterface, Integer>> popPurchaseFailedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popPurchaseFailed(activityContext)
                .flatMap(new AlertDialogButtonHandler(
                        AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX,
                        () -> sendSupportEmailBillingGenericError(activityContext, throwable)));
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popPurchaseFailed(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.amazon_store_billing_purchase_failed_error_window_title)
                        .setMessage(R.string.amazon_store_billing_purchase_failed_error_window_description))
                .setPositiveButton(R.string.amazon_store_billing_purchase_failed_error_ok)
                .setNegativeButton(R.string.amazon_store_billing_purchase_failed_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }

    @Override @NonNull public Observable<Pair<DialogInterface, Integer>> popPurchaseUnsupportedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popPurchaseUnsupported(activityContext)
                .flatMap(new AlertDialogButtonHandler(
                        AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX,
                        () -> sendSupportEmailBillingGenericError(activityContext, throwable)));
    }

    @Override @NonNull public Observable<Pair<DialogInterface, Integer>> popPurchaseUnsupported(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.amazon_store_billing_purchase_unsupported_error_window_title)
                        .setMessage(R.string.amazon_store_billing_purchase_unsupported_error_window_description))
                .setPositiveButton(R.string.amazon_store_billing_purchase_unsupported_error_ok)
                .setNegativeButton(R.string.amazon_store_billing_purchase_unsupported_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }
    //</editor-fold>

    //<editor-fold desc="Sandbox Related">
    @NonNull public Observable<Pair<DialogInterface, Integer>> popSandboxModeAndHandle(
            @NonNull final Context activityContext)
    {
        return popSandboxMode(activityContext)
                .flatMap(pair -> handlePopSandboxMode(
                        activityContext,
                        pair));
    }

    @Override @NonNull public Observable<Pair<DialogInterface, Integer>> popSandboxMode(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.amazon_store_billing_sandbox_window_title)
                        .setMessage(R.string.amazon_store_billing_sandbox_window_description))
                .setPositiveButton(R.string.amazon_store_billing_sandbox_window_ok)
                .setNegativeButton(R.string.amazon_store_billing_sandbox_window_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }

    @NonNull protected Observable<Pair<DialogInterface, Integer>> handlePopSandboxMode(
            @NonNull final Context activityContext,
            @NonNull Pair<DialogInterface, Integer> pair)
    {
        if (pair.second.equals(AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX))
        {
            sendSupportEmailBillingSandbox(activityContext);
            return Observable.empty();
        }
        return Observable.just(pair);
    }

    public void sendSupportEmailBillingSandbox(final Context context)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                VersionUtils.getSupportEmailTraceParameters(context, true));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Amazon Store in-app purchases are in sandbox mode");
        activityUtil.sendSupportEmail(context, emailIntent);
    }
    //</editor-fold>

    public void sendSupportEmailRestoreFailed(final Context context, Exception exception)
    {
        context.startActivity(Intent.createChooser(
                amazonStoreUtils.getSupportPurchaseRestoreEmailIntent(context, exception),
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
}
