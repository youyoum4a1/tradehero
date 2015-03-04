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
import com.tradehero.common.billing.restore.PurchaseRestoreResultWithError;
import com.tradehero.common.billing.restore.PurchaseRestoreTotalResult;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.billing.inventory.THProductDetailDomainInformerRx;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.VersionUtils;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;
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
    @NonNull protected final VersionUtils versionUtils;
    protected String storeName;

    //<editor-fold desc="Constructors">
    public THBillingAlertDialogRxUtil(
            @NonNull Analytics analytics,
            @NonNull VersionUtils versionUtils)
    {
        this.analytics = analytics;
        this.versionUtils = versionUtils;
    }
    //</editor-fold>

    @NonNull public Observable<Integer> getUnusedRequestCode(@NonNull final RequestCodeHolder requestCodeHolder)
    {
        return Observable.just(MAX_RANDOM_RETRIES)
                .subscribeOn(Schedulers.computation())
                .flatMap(new Func1<Integer, Observable<? extends Integer>>()
                {
                    @Override public Observable<? extends Integer> call(Integer retries)
                    {
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
                    }
                });
    }

    public void setStoreName(String storeName)
    {
        this.storeName = storeName;
    }

    @NonNull public Observable<OnDialogClickEvent> popErrorAndHandle(
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
    @NonNull public Observable<OnDialogClickEvent> popBillingUnavailableAndHandleRx(
            @NonNull final Context activityContext)
    {
        return popBillingUnavailableRx(activityContext)
                .flatMap(new Func1<OnDialogClickEvent, Observable<? extends OnDialogClickEvent>>()
                {
                    @Override public Observable<? extends OnDialogClickEvent> call(OnDialogClickEvent pair)
                    {
                        return THBillingAlertDialogRxUtil.this.handlePopBillingUnavailable(activityContext, pair);
                    }
                });
    }

    @NonNull public Observable<OnDialogClickEvent> popBillingUnavailableRx(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.store_billing_unavailable_window_title)
                .setMessage(activityContext.getString(R.string.store_billing_unavailable_window_description, storeName))
                .setPositiveButton(R.string.store_billing_unavailable_act)
                .setNegativeButton(R.string.store_billing_unavailable_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull protected Observable<OnDialogClickEvent> handlePopBillingUnavailable(
            @NonNull final Context activityContext,
            @NonNull OnDialogClickEvent event)
    {
        if (event.isPositive())
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
            @NonNull final Activity activityContext,
            @NonNull final ProductIdentifierDomain domain,
            @NonNull final THProductDetailDomainInformerRxType domainInformer)
    {
        return getUnusedRequestCode(domainInformer)
                .flatMap(new Func1<Integer, Observable<? extends ProductInventoryResult<ProductIdentifierType, THProductDetailType>>>()
                {
                    @Override public Observable<? extends ProductInventoryResult<ProductIdentifierType, THProductDetailType>> call(
                            Integer requestCode)
                    {
                        return domainInformer.getDetailsOfDomain(requestCode, domain);
                    }
                })
                .toList()
                .flatMap(
                        new Func1<List<ProductInventoryResult<ProductIdentifierType, THProductDetailType>>, Observable<? extends THProductDetailType>>()
                        {
                            @Override public Observable<? extends THProductDetailType> call(
                                    List<ProductInventoryResult<ProductIdentifierType, THProductDetailType>> productResults)
                            {
                                List<THProductDetailType> productDetails = new ArrayList<THProductDetailType>();
                                for (ProductInventoryResult<ProductIdentifierType, THProductDetailType> result : productResults)
                                {
                                    productDetails.add(result.detail);
                                }
                                return popBuyDialogAndHandle(activityContext, domain, productDetails);
                            }
                        });
    }

    @NonNull public Observable<ProductInventoryResult<ProductIdentifierType, THProductDetailType>> popBuyDialogAndHandle(
            @NonNull Activity activityContext,
            @NonNull ProductIdentifierDomain domain,
            @NonNull final List<ProductInventoryResult<ProductIdentifierType, THProductDetailType>> productDetails,
            @Nullable ProductInventoryResult<ProductIdentifierType, THProductDetailType> typeQualifier)
    {
        return popBuyDialog(
                activityContext,
                domain,
                CollectionUtils.map(
                        productDetails,
                        new Func1<ProductInventoryResult<ProductIdentifierType, THProductDetailType>, THProductDetailType>()
                        {
                            @Override public THProductDetailType call(
                                    ProductInventoryResult<ProductIdentifierType, THProductDetailType> result)
                            {
                                return result.detail;
                            }
                        }))
                .map(new PairGetSecond<DialogInterface, THProductDetailType>())
                .flatMap(new Func1<THProductDetailType, Observable<? extends ProductInventoryResult<ProductIdentifierType, THProductDetailType>>>()
                {
                    @Override public Observable<? extends ProductInventoryResult<ProductIdentifierType, THProductDetailType>> call(
                            THProductDetailType detail)
                    {
                        for (ProductInventoryResult<ProductIdentifierType, THProductDetailType> candidate : productDetails)
                        {
                            if (candidate.id.equals(detail.getProductIdentifier()))
                            {
                                return Observable.just(candidate);
                            }
                        }
                        return Observable.error(new IllegalStateException("Should have found result for " + detail.getProductIdentifier()));
                    }
                });
    }

    @NonNull public Observable<THProductDetailType> popBuyDialogAndHandle(
            @NonNull Activity activityContext,
            @NonNull ProductIdentifierDomain domain,
            @NonNull List<THProductDetailType> productDetails)
    {
        return popBuyDialog(activityContext, domain, productDetails)
                .map(new PairGetSecond<DialogInterface, THProductDetailType>());
    }

    @NonNull public Observable<Pair<DialogInterface, THProductDetailType>> popBuyDialog(
            @NonNull Activity activityContext,
            @NonNull ProductIdentifierDomain domain,
            @NonNull List<THProductDetailType> productDetails)
    {
        final ProductDetailAdapterType detailAdapter = createProductDetailAdapter(activityContext, domain);
        //detailAdapter.setEnabledItems(enabledItems); // FIXME
        detailAdapter.setProductDetailComparator(createProductDetailComparator());
        detailAdapter.setItems(productDetails);
        //noinspection unchecked
        return buildDefault(activityContext)
                .setTitle(domain.storeTitleResId)
                .setCanceledOnTouchOutside(true)
                .setSingleChoiceItems(detailAdapter, 0)
                .setNegativeButton(R.string.store_buy_virtual_dollar_window_button_cancel)
                .build()
                .flatMap(new Func1<OnDialogClickEvent, Observable<Pair<DialogInterface, THProductDetailType>>>()
                {
                    @Override public Observable<Pair<DialogInterface, THProductDetailType>> call(OnDialogClickEvent event)
                    {
                        if (event.which >= 0)
                        {
                            return Observable.just(Pair.create(event.dialog, (THProductDetailType) detailAdapter.getItem(event.which)));
                        }
                        return Observable.empty();
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Purchases Restored">
    @NonNull public Observable<OnDialogClickEvent> popRestoreResultAndHandle(
            @NonNull final Context activityContext,
            @NonNull final PurchaseRestoreTotalResult<
                    ProductIdentifierType,
                    THOrderIdType,
                    THProductPurchaseType> result)
    {
        Observable<OnDialogClickEvent> observable;
        if (result.getCount() == 0)
        {
            observable = buildDefault(activityContext)
                    .setTitle(R.string.iap_purchase_restored_none_title)
                    .setMessage(R.string.iap_purchase_restored_none_message)
                    .setNegativeButton(R.string.iap_purchase_restored_none_cancel)
                    .setCanceledOnTouchOutside(true)
                    .build();
        }
        else if (result.getFailedCount() > 0 && result.getSucceededCount() == 0)
        {
            observable = buildDefault(activityContext)
                    .setTitle(R.string.iap_send_support_email_restore_fail_title)
                    .setMessage(activityContext.getString(
                            R.string.iap_send_support_email_restore_fail_message,
                            result.getFailedCount()))
                    .setPositiveButton(R.string.iap_send_support_email_restore_fail_ok)
                    .setNegativeButton(R.string.iap_send_support_email_restore_fail_cancel)
                    .setCanceledOnTouchOutside(true)
                    .build()
                    .flatMap(new Func1<OnDialogClickEvent, Observable<? extends OnDialogClickEvent>>()
                    {
                        @Override public Observable<? extends OnDialogClickEvent> call(OnDialogClickEvent event)
                        {
                            if (event.isPositive())
                            {
                                THBillingAlertDialogRxUtil.this.sendSupportEmailPurchaseRestoreFailed(
                                        activityContext,
                                        CollectionUtils.map(
                                                result.restoredList,
                                                new Func1<PurchaseRestoreResultWithError<ProductIdentifierType, THOrderIdType, THProductPurchaseType>, Throwable>()
                                                {
                                                    @Override public Throwable call(
                                                            PurchaseRestoreResultWithError<ProductIdentifierType, THOrderIdType, THProductPurchaseType> restored)
                                                    {
                                                        return restored.throwable;
                                                    }
                                                }));
                            }
                            return Observable.empty();
                        }
                    });
        }
        else if (result.getFailedCount() > 0 && result.getSucceededCount() > 0)
        {
            observable = buildDefault(activityContext)
                    .setTitle(R.string.iap_send_support_email_restore_fail_partial_title)
                    .setMessage(activityContext.getString(
                            R.string.iap_send_support_email_restore_fail_partial_message,
                            result.getSucceededCount(),
                            result.getFailedCount()))
                    .setPositiveButton(R.string.iap_send_support_email_restore_fail_partial_ok)
                    .setNegativeButton(R.string.iap_send_support_email_restore_fail_partial_cancel)
                    .setCanceledOnTouchOutside(true)
                    .build()
                    .flatMap(new Func1<OnDialogClickEvent, Observable<? extends OnDialogClickEvent>>()
                    {
                        @Override public Observable<? extends OnDialogClickEvent> call(OnDialogClickEvent event)
                        {
                            if (event.isPositive())
                            {
                                THBillingAlertDialogRxUtil.this.sendSupportEmailPurchaseRestoreFailedPartial(
                                        activityContext,
                                        CollectionUtils.map(
                                                result.restoredList,
                                                new Func1<PurchaseRestoreResultWithError<ProductIdentifierType, THOrderIdType, THProductPurchaseType>, Throwable>()
                                                {
                                                    @Override public Throwable call(
                                                            PurchaseRestoreResultWithError<ProductIdentifierType, THOrderIdType, THProductPurchaseType> restored)
                                                    {
                                                        return restored.throwable;
                                                    }
                                                }));
                            }
                            return Observable.empty();
                        }
                    });
        }
        else
        {
            observable = buildDefault(activityContext)
                    .setTitle(R.string.iap_purchase_restored_title)
                    .setMessage(activityContext.getString(
                            R.string.iap_purchase_restored_message,
                            result.getSucceededCount()))
                    .setNegativeButton(R.string.iap_purchase_restored_cancel)
                    .setCanceledOnTouchOutside(true)
                    .build();
        }
        return observable.flatMap(new Func1<OnDialogClickEvent, Observable<? extends OnDialogClickEvent>>()
        {
            @Override public Observable<? extends OnDialogClickEvent> call(OnDialogClickEvent pair)
            {
                return Observable.empty();
            }
        }); // We do not want anything propagated
    }

    public void sendSupportEmailPurchaseRestoreFailed(
            @NonNull final Context context,
            @NonNull List<Throwable> throwables)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                versionUtils.getExceptionStringsAndTraceParameters(context, throwables));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an error restoring my purchases");
        ActivityUtil.sendSupportEmail(context, emailIntent);
    }

    public void sendSupportEmailPurchaseRestoreFailedPartial(
            @NonNull final Context context,
            @NonNull List<Throwable> throwables)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                versionUtils.getExceptionStringsAndTraceParameters(context, throwables));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an error restoring part of my purchases");
        ActivityUtil.sendSupportEmail(context, emailIntent);
    }

    @NonNull public Observable<OnDialogClickEvent> popRestoreFailedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popRestoreFailed(activityContext)
                .flatMap(new Func1<OnDialogClickEvent, Observable<? extends OnDialogClickEvent>>()
                {
                    @Override public Observable<? extends OnDialogClickEvent> call(OnDialogClickEvent pair)
                    {
                        return THBillingAlertDialogRxUtil.this.handlePopRestoreFailed(activityContext, throwable, pair);
                    }
                });
    }

    @NonNull public static Observable<OnDialogClickEvent> popRestoreFailed(
            @NonNull Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.iap_send_support_email_restore_fail_title)
                .setMessage(R.string.iap_send_support_email_restore_fail_message)
                .setPositiveButton(R.string.iap_send_support_email_restore_fail_ok)
                .setNegativeButton(R.string.iap_send_support_email_restore_fail_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull protected Observable<OnDialogClickEvent> handlePopRestoreFailed(
            @NonNull Context activityContext,
            @NonNull Throwable throwable,
            @NonNull OnDialogClickEvent event)
    {
        if (event.isPositive())
        {
            sendSupportEmailPurchaseNotRestored(activityContext, throwable);
        }
        return Observable.empty();
    }

    public void sendSupportEmailPurchaseNotRestored(@NonNull final Context context, @NonNull Throwable throwable)
    {
        Intent emailIntent = versionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My purchase is not being handled even after restart");
        ActivityUtil.sendSupportEmail(context, emailIntent);
    }
    //</editor-fold>

    public void sendSupportEmailBillingGenericError(final Context context, final Throwable throwable)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                versionUtils.getExceptionStringsAndTraceParameters(context, throwable));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an error");
        ActivityUtil.sendSupportEmail(context, emailIntent);
    }

    public void sendSupportEmailCancelledPurchase(final Context context)
    {
        Intent emailIntent = versionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I cancelled the purchase");
        ActivityUtil.sendSupportEmail(context, emailIntent);
    }
}
