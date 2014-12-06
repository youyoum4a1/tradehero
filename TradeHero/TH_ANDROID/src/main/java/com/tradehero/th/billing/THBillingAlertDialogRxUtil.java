package com.tradehero.th.billing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.RequestCodeHolder;
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
                ProductDetailViewType>>
        extends AlertDialogRxUtil
{
    public static final int MAX_RANDOM_RETRIES = 50;

    @NonNull protected final Analytics analytics;
    @NonNull protected final ActivityUtil activityUtil;
    protected String storeName;

    //<editor-fold desc="Constructors">
    public THBillingAlertDialogRxUtil(
            @NonNull Analytics analytics,
            @NonNull ActivityUtil activityUtil)
    {
        super();
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

    @NonNull public Observable<Pair<DialogInterface, Integer>> popError(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        if (throwable instanceof UnknownServiceException)
        {
            return popBillingUnavailableAndHandleRx(activityContext);
        }
        return Observable.empty();
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
            return Observable.empty();
        }
        return Observable.just(pair);
    }

    public void goToCreateAccount(final Context context)
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

    @NonNull public Observable<THProductDetailType> popBuyDialog(
            @NonNull Activity activityContext,
            @NonNull ProductIdentifierDomain domain,
            @NonNull THProductDetailDomainInformerRxType domainInformer)
    {
        return getUnusedRequestCode(domainInformer)
                .flatMap(requestCode -> domainInformer.getDetailsOfDomain(requestCode, domain))
                .map(result -> result.detail)
                .toList()
                .flatMap(productDetails -> popBuyDialog(activityContext, domain, productDetails));
    }

    @NonNull public Observable<THProductDetailType> popBuyDialog(
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
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(pair -> {
                    if (pair.second >= 0)
                    {
                        return Observable.just(productDetails.get(pair.second));
                    }
                    return Observable.empty();
                });
    }
    //</editor-fold>

    public void sendSupportEmailPurchaseNotRestored(final Context context)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My purchase is not being handled even after restart");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public void sendSupportEmailBillingUnknownError(final Context context, final Throwable throwable)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                VersionUtils.getExceptionStringsAndTraceParameters(context, throwable));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an unidentified error");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public void sendSupportEmailCancelledPurchase(final Context context)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I cancelled the purchase");
        activityUtil.sendSupportEmail(context, emailIntent);
    }


}
