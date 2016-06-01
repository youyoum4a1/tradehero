package com.ayondo.academy.billing.amazon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonFetchInventoryFailedException;
import com.tradehero.common.billing.amazon.exception.AmazonFetchInventoryUnsupportedException;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseFailedException;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseUnsupportedException;
import com.tradehero.metrics.Analytics;
import com.ayondo.academy.R;
import com.ayondo.academy.activities.ActivityUtil;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.billing.BaseBillingUtils;
import com.ayondo.academy.billing.THBillingAlertDialogRxUtil;
import com.ayondo.academy.persistence.billing.THAmazonPurchaseCacheRx;
import com.ayondo.academy.rx.dialog.AlertDialogButtonHandler;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import com.ayondo.academy.utils.VersionUtils;
import java.util.HashMap;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import timber.log.Timber;

public class THAmazonAlertDialogRxUtil extends THBillingAlertDialogRxUtil<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonLogicHolderRx,
        THAmazonOrderId,
        THAmazonPurchase>
        implements AmazonAlertDialogRxUtil
{
    @NonNull protected final THAmazonPurchaseCacheRx thAmazonPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THAmazonAlertDialogRxUtil(
            @NonNull CurrentUserId currentUserId,
            @NonNull Analytics analytics,
            @NonNull THAmazonPurchaseCacheRx thAmazonPurchaseCache)
    {
        super(currentUserId, analytics);
        this.thAmazonPurchaseCache = thAmazonPurchaseCache;
    }
    //</editor-fold>

    //<editor-fold desc="SKU related">
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

    @NonNull @Override public Observable<OnDialogClickEvent> popErrorAndHandle(
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
    @Override @NonNull public Observable<OnDialogClickEvent> popInventoryFailedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popInventoryFailed(activityContext)
                .flatMap(new AlertDialogButtonHandler(
                        DialogInterface.BUTTON_POSITIVE,
                        new Action0()
                        {
                            @Override public void call()
                            {
                                THAmazonAlertDialogRxUtil.this.sendSupportEmailBillingGenericError(activityContext, throwable);
                            }
                        }));
    }

    @Override @NonNull public Observable<OnDialogClickEvent> popInventoryFailed(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.amazon_store_billing_inventory_failed_error_window_title)
                .setMessage(R.string.amazon_store_billing_inventory_failed_error_window_description)
                .setPositiveButton(R.string.amazon_store_billing_inventory_failed_error_ok)
                .setNegativeButton(R.string.amazon_store_billing_inventory_failed_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @Override @NonNull public Observable<OnDialogClickEvent> popInventoryNotSupportedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popInventoryNotSupported(activityContext)
                .flatMap(new AlertDialogButtonHandler(
                        DialogInterface.BUTTON_POSITIVE,
                        new Action0()
                        {
                            @Override public void call()
                            {
                                THAmazonAlertDialogRxUtil.this.sendSupportEmailBillingGenericError(activityContext, throwable);
                            }
                        }));
    }

    @Override @NonNull public Observable<OnDialogClickEvent> popInventoryNotSupported(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.amazon_store_billing_inventory_unsupported_error_window_title)
                .setMessage(R.string.amazon_store_billing_inventory_unsupported_error_window_description)
                .setPositiveButton(R.string.amazon_store_billing_inventory_unsupported_error_ok)
                .setNegativeButton(R.string.amazon_store_billing_inventory_unsupported_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Related">
    @NonNull public Observable<OnDialogClickEvent> popPurchaseFailedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popPurchaseFailed(activityContext)
                .flatMap(new AlertDialogButtonHandler(
                        DialogInterface.BUTTON_POSITIVE,
                        new Action0()
                        {
                            @Override public void call()
                            {
                                THAmazonAlertDialogRxUtil.this.sendSupportEmailBillingGenericError(activityContext, throwable);
                            }
                        }));
    }

    @NonNull public Observable<OnDialogClickEvent> popPurchaseFailed(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.amazon_store_billing_purchase_failed_error_window_title)
                .setMessage(R.string.amazon_store_billing_purchase_failed_error_window_description)
                .setPositiveButton(R.string.amazon_store_billing_purchase_failed_error_ok)
                .setNegativeButton(R.string.amazon_store_billing_purchase_failed_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @Override @NonNull public Observable<OnDialogClickEvent> popPurchaseUnsupportedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popPurchaseUnsupported(activityContext)
                .flatMap(new AlertDialogButtonHandler(
                        DialogInterface.BUTTON_POSITIVE,
                        new Action0()
                        {
                            @Override public void call()
                            {
                                THAmazonAlertDialogRxUtil.this.sendSupportEmailBillingGenericError(activityContext, throwable);
                            }
                        }));
    }

    @Override @NonNull public Observable<OnDialogClickEvent> popPurchaseUnsupported(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.amazon_store_billing_purchase_unsupported_error_window_title)
                .setMessage(R.string.amazon_store_billing_purchase_unsupported_error_window_description)
                .setPositiveButton(R.string.amazon_store_billing_purchase_unsupported_error_ok)
                .setNegativeButton(R.string.amazon_store_billing_purchase_unsupported_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
    //</editor-fold>

    //<editor-fold desc="Sandbox Related">
    @NonNull public Observable<OnDialogClickEvent> popSandboxModeAndHandle(
            @NonNull final Context activityContext)
    {
        return popSandboxMode(activityContext)
                .flatMap(new Func1<OnDialogClickEvent, Observable<? extends OnDialogClickEvent>>()
                {
                    @Override public Observable<? extends OnDialogClickEvent> call(OnDialogClickEvent pair)
                    {
                        return THAmazonAlertDialogRxUtil.this.handlePopSandboxMode(
                                activityContext,
                                pair);
                    }
                });
    }

    @Override @NonNull public Observable<OnDialogClickEvent> popSandboxMode(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.amazon_store_billing_sandbox_window_title)
                .setMessage(R.string.amazon_store_billing_sandbox_window_description)
                .setPositiveButton(R.string.amazon_store_billing_sandbox_window_ok)
                .setNegativeButton(R.string.amazon_store_billing_sandbox_window_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull protected Observable<OnDialogClickEvent> handlePopSandboxMode(
            @NonNull final Context activityContext,
            @NonNull OnDialogClickEvent event)
    {
        if (event.isPositive())
        {
            sendSupportEmailBillingSandbox(activityContext);
            return Observable.empty();
        }
        return Observable.just(event);
    }

    public void sendSupportEmailBillingSandbox(final Context context)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                VersionUtils.getSupportEmailTraceParameters(context, currentUserId, true));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Amazon Store in-app purchases are in sandbox mode");
        ActivityUtil.sendSupportEmail(context, emailIntent);
    }
    //</editor-fold>

    public void sendSupportEmailRestoreFailed(final Context context, Exception exception)
    {
        context.startActivity(Intent.createChooser(
                BaseBillingUtils.getSupportPurchaseRestoreEmailIntent(context, currentUserId, exception),
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
}
