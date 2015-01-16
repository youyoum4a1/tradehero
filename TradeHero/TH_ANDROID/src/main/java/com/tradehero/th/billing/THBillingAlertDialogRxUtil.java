package com.tradehero.th.billing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.RequestCodeHolder;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.restore.PurchaseRestoreTotalResult;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.billing.inventory.THProductDetailDomainInformerRx;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.rx.dialog.AlertDialogButtonConstants;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.VersionUtils;
import java.net.UnknownServiceException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

abstract public class THBillingAlertDialogRxUtil<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THProductDetailDomainInformerRxType extends THProductDetailDomainInformerRx<
                ProductIdentifierType,
                THProductDetailType>,
        ProductDetailViewType extends ProductDetailView<
                ProductIdentifierType,
                THProductDetailType>,
        ProductDetailAdapterType extends ProductDetailAdapter<
                ProductIdentifierType,
                THProductDetailType,
                ProductDetailViewType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<
                ProductIdentifierType,
                THOrderIdType>>
        extends AlertDialogRxUtil
{
    public static final int MAX_RANDOM_RETRIES = 50;

    @NonNull protected final Analytics analytics;
    @NonNull protected final ActivityUtil activityUtil;
    protected String storeName;

    //<editor-fold desc="Constructors">
    public THBillingAlertDialogRxUtil(
            @NonNull Analytics analytics,
            @NonNull ActivityUtil activityUtil,
            @NonNull VersionUtils versionUtils)
    {
        super(versionUtils);
        this.analytics = analytics;
        this.activityUtil = activityUtil;
    }
    //</editor-fold>

    @NonNull public Observable<Integer> getUnusedRequestCode(@NonNull RequestCodeHolder requestCodeHolder)
    {
        return Observable.just(MAX_RANDOM_RETRIES)
                .subscribeOn(Schedulers.computation())
                .flatMap(retries -> {
                    int randomNumber;
                    while (retries-- > 0)
                    {
                        randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
                        if (requestCodeHolder.isUnusedRequestCode(randomNumber))
                        {
                            return Observable.just(randomNumber);
                        }
                    }
                    return Observable.error(
                            new IllegalStateException(String.format(
                                    "Could not find an unused requestCode after %d trials",
                                    MAX_RANDOM_RETRIES)));
                });
    }

    public void setStoreName(String storeName)
    {
        this.storeName = storeName;
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popErrorAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        if (throwable instanceof UnknownServiceException)
        {
            return popBillingUnavailableAndHandleRx(activityContext);
        }
        return Observable.error(throwable);
    }

    //<editor-fold desc="Billing Available">
    @NonNull public Observable<Pair<DialogInterface, Integer>> popBillingUnavailableAndHandleRx(
            @NonNull final Context activityContext)
    {
        return popBillingUnavailableRx(activityContext)
                .flatMap(pair -> handlePopBillingUnavailable(activityContext, pair));
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popBillingUnavailableRx(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.store_billing_unavailable_window_title)
                        .setMessage(activityContext.getString(R.string.store_billing_unavailable_window_description, storeName)))
                .setPositiveButton(R.string.store_billing_unavailable_act)
                .setNegativeButton(R.string.store_billing_unavailable_cancel)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull protected Observable<Pair<DialogInterface, Integer>> handlePopBillingUnavailable(
            @NonNull final Context activityContext,
            @NonNull Pair<DialogInterface, Integer> pair)
    {
        if (pair.second.equals(AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX))
        {
            goToCreateAccount(activityContext);
        }
        return Observable.empty();
    }

    public void goToCreateAccount(@NonNull final Context context)
    {
        Intent addAccountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Still cannot get it to go back to TradeHero with back button
        context.startActivity(addAccountIntent);
    }
    //</editor-fold>

    //<editor-fold desc="Product Detail Presentation">

    /**
     * By default, product identifiers that are not mentioned in the list are enabled.
     */
    @NonNull abstract public HashMap<ProductIdentifier, Boolean> getEnabledItems();

    @NonNull abstract protected ProductDetailAdapterType createProductDetailAdapter(
            @NonNull Activity activity,
            @NonNull ProductIdentifierDomain skuDomain);

    @NonNull protected Comparator<THProductDetailType> createProductDetailComparator()
    {
        return new THProductDetailDecreasingPriceComparator<>();
    }

    @NonNull public Observable<THProductDetailType> popBuyDialogAndHandle(
            @NonNull Activity activityContext,
            @NonNull ProductIdentifierDomain domain,
            @NonNull THProductDetailDomainInformerRxType domainInformer)
    {
        return getUnusedRequestCode(domainInformer)
                .flatMap(requestCode -> domainInformer.getDetailsOfDomain(requestCode, domain))
                .map(result -> result.detail)
                .toList()
                .flatMap(productDetails -> popBuyDialogAndHandle(activityContext, domain, productDetails));
    }

    @NonNull public Observable<ProductInventoryResult<ProductIdentifierType, THProductDetailType>> popBuyDialogAndHandle(
            @NonNull Activity activityContext,
            @NonNull ProductIdentifierDomain domain,
            @NonNull List<ProductInventoryResult<ProductIdentifierType, THProductDetailType>> productDetails,
            @Nullable ProductInventoryResult<ProductIdentifierType, THProductDetailType> typeQualifier)
    {
        return popBuyDialog(
                activityContext,
                domain,
                CollectionUtils.map(
                        productDetails,
                        result -> result.detail))
                .filter(pair -> pair.second >= 0)
                .map(pair -> productDetails.get(pair.second));
    }

    @NonNull public Observable<THProductDetailType> popBuyDialogAndHandle(
            @NonNull Activity activityContext,
            @NonNull ProductIdentifierDomain domain,
            @NonNull List<THProductDetailType> productDetails)
    {
        return popBuyDialog(activityContext, domain, productDetails)
                .filter(pair -> pair.second >= 0)
                .map(pair -> productDetails.get(pair.second));
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popBuyDialog(
            @NonNull Activity activityContext,
            @NonNull ProductIdentifierDomain domain,
            @NonNull List<THProductDetailType> productDetails)
    {
        final ProductDetailAdapterType detailAdapter = createProductDetailAdapter(activityContext, domain);
        //detailAdapter.setEnabledItems(enabledItems); // FIXME
        detailAdapter.setProductDetailComparator(createProductDetailComparator());
        detailAdapter.setItems(productDetails);
        return Observable.create(
                AlertDialogOnSubscribe.builder(
                        createDefaultDialogBuilder(activityContext)
                                .setTitle(domain.storeTitleResId))
                        .setCanceledOnTouchOutside(true)
                        .setSingleChoiceItems(detailAdapter, 0)
                        .setNegativeButton(R.string.store_buy_virtual_dollar_window_button_cancel)
                        .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
    //</editor-fold>

    //<editor-fold desc="Purchases Restored">
    @NonNull public Observable<Pair<DialogInterface, Integer>> popRestoreResultAndHandle(
            @NonNull Context activityContext,
            @NonNull PurchaseRestoreTotalResult<
                    ProductIdentifierType,
                    THOrderIdType,
                    THProductPurchaseType> result)
    {
        Observable<Pair<DialogInterface, Integer>> observable;
        if (result.getCount() == 0)
        {
            observable = Observable.create(AlertDialogOnSubscribe.builder(
                    createDefaultDialogBuilder(activityContext)
                            .setTitle(R.string.iap_purchase_restored_none_title)
                            .setMessage(R.string.iap_purchase_restored_none_message))
                    .setNegativeButton(R.string.iap_purchase_restored_none_cancel)
                    .setCanceledOnTouchOutside(true)
                    .build())
                    .subscribeOn(AndroidSchedulers.mainThread());
        }
        else if (result.getFailedCount() > 0 && result.getSucceededCount() == 0)
        {
            observable = Observable.create(AlertDialogOnSubscribe.builder(
                    createDefaultDialogBuilder(activityContext)
                            .setTitle(R.string.iap_send_support_email_restore_fail_title)
                            .setMessage(activityContext.getString(
                                    R.string.iap_send_support_email_restore_fail_message,
                                    result.getFailedCount())))
                    .setPositiveButton(R.string.iap_send_support_email_restore_fail_ok)
                    .setNegativeButton(R.string.iap_send_support_email_restore_fail_cancel)
                    .setCanceledOnTouchOutside(true)
                    .build())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .flatMap(pair -> {
                        if (pair.second.equals(AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX))
                        {
                            sendSupportEmailPurchaseRestoreFailed(
                                    activityContext,
                                    CollectionUtils.map(
                                            result.restoredList,
                                            restored -> restored.throwable));
                        }
                        return Observable.empty();
                    });
        }
        else if (result.getFailedCount() > 0 && result.getSucceededCount() > 0)
        {
            observable = Observable.create(AlertDialogOnSubscribe.builder(
                    createDefaultDialogBuilder(activityContext)
                            .setTitle(R.string.iap_send_support_email_restore_fail_partial_title)
                            .setMessage(activityContext.getString(
                                    R.string.iap_send_support_email_restore_fail_partial_message,
                                    result.getSucceededCount(),
                                    result.getFailedCount())))
                    .setPositiveButton(R.string.iap_send_support_email_restore_fail_partial_ok)
                    .setNegativeButton(R.string.iap_send_support_email_restore_fail_partial_cancel)
                    .setCanceledOnTouchOutside(true)
                    .build())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .flatMap(pair -> {
                        if (pair.second.equals(AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX))
                        {
                            sendSupportEmailPurchaseRestoreFailedPartial(
                                    activityContext,
                                    CollectionUtils.map(
                                            result.restoredList,
                                            restored -> restored.throwable));
                        }
                        return Observable.empty();
                    });
        }
        else
        {
            observable = Observable.create(AlertDialogOnSubscribe.builder(
                    createDefaultDialogBuilder(activityContext)
                            .setTitle(R.string.iap_purchase_restored_title)
                            .setMessage(activityContext.getString(
                                    R.string.iap_purchase_restored_message,
                                    result.getSucceededCount())))
                    .setNegativeButton(R.string.iap_purchase_restored_cancel)
                    .setCanceledOnTouchOutside(true)
                    .build())
                    .subscribeOn(AndroidSchedulers.mainThread());
        }
        return observable.flatMap(pair -> Observable.empty()); // We do not want anything propagated
    }

    public void sendSupportEmailPurchaseRestoreFailed(
            @NonNull final Context context,
            @NonNull List<Throwable> throwables)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                versionUtils.getExceptionStringsAndTraceParameters(context, throwables));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an error restoring my purchases");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public void sendSupportEmailPurchaseRestoreFailedPartial(
            @NonNull final Context context,
            @NonNull List<Throwable> throwables)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                versionUtils.getExceptionStringsAndTraceParameters(context, throwables));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an error restoring part of my purchases");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popRestoreFailedAndHandle(
            @NonNull Context activityContext,
            @NonNull Throwable throwable)
    {
        return popRestoreFailed(activityContext)
                .flatMap(pair -> handlePopRestoreFailed(activityContext, throwable, pair));
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popRestoreFailed(
            @NonNull Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.iap_send_support_email_restore_fail_title)
                        .setMessage(R.string.iap_send_support_email_restore_fail_message))
                .setPositiveButton(R.string.iap_send_support_email_restore_fail_ok)
                .setNegativeButton(R.string.iap_send_support_email_restore_fail_cancel)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull protected Observable<Pair<DialogInterface, Integer>> handlePopRestoreFailed(
            @NonNull Context activityContext,
            @NonNull Throwable throwable,
            @NonNull Pair<DialogInterface, Integer> pair)
    {
        if (pair.second.equals(AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX))
        {
            sendSupportEmailPurchaseNotRestored(activityContext, throwable);
        }
        return Observable.empty();
    }

    public void sendSupportEmailPurchaseNotRestored(@NonNull final Context context, @NonNull Throwable throwable)
    {
        Intent emailIntent = versionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My purchase is not being handled even after restart");
        activityUtil.sendSupportEmail(context, emailIntent);
    }
    //</editor-fold>

    public void sendSupportEmailBillingGenericError(final Context context, final Throwable throwable)
    {
        Intent emailIntent = versionUtils.getSupportEmailIntent(
                versionUtils.getExceptionStringsAndTraceParameters(context, throwable));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an error");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public void sendSupportEmailCancelledPurchase(final Context context)
    {
        Intent emailIntent = versionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I cancelled the purchase");
        activityUtil.sendSupportEmail(context, emailIntent);
    }
}
