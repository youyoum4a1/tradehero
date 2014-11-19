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
import com.tradehero.th.R;
import com.tradehero.th.billing.inventory.THProductDetailDomainInformerRx;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import com.tradehero.th.utils.AlertDialogRxUtil;
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

    //<editor-fold desc="Constructors">
    public THBillingAlertDialogRxUtil()
    {
        super();
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

    //<editor-fold desc="Billing Available">
    @NonNull public Observable<Pair<DialogInterface, Integer>> popBillingUnavailableRx(
            @NonNull final Context activityContext,
            @NonNull String storeName)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.store_billing_unavailable_window_title)
                        .setMessage(activityContext.getString(R.string.store_billing_unavailable_window_description, storeName)))
                .setPositiveButton(R.string.store_billing_unavailable_act)
                .setNegativeButton(R.string.store_billing_unavailable_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
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
}
