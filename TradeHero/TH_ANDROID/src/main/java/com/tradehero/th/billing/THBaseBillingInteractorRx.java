package com.tradehero.th.billing;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.billing.BaseBillingInteractorRx;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.tester.BillingTestResult;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Func1;

abstract public class THBaseBillingInteractorRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<
                ProductIdentifierType,
                THOrderIdType>,
        THBillingLogicHolderType extends THBillingLogicHolderRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType>,
        ProductDetailViewType extends ProductDetailView<
                ProductIdentifierType,
                THProductDetailType>,
        ProductDetailAdapterType extends ProductDetailAdapter<
                ProductIdentifierType,
                THProductDetailType,
                ProductDetailViewType>>
        extends BaseBillingInteractorRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        THProductDetailType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType,
        THBillingLogicHolderType>
        implements
        THBillingInteractorRx<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, THProductDetailType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType, THBillingLogicHolderType>
{
    public static final int ACTION_RESET_PORTFOLIO = 1;

    @NonNull protected final Provider<Activity> activityProvider;
    @NonNull protected final ProgressDialogUtil progressDialogUtil;
    @NonNull protected final THBillingAlertDialogRxUtil billingAlertDialogUtil;
    @NonNull protected final THBillingRequisitePreparer billingRequisitePreparer;

    //<editor-fold desc="Constructors">
    protected THBaseBillingInteractorRx(
            @NonNull THBillingLogicHolderType billingLogicHolder,
            @NonNull Provider<Activity> activityProvider,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull THBillingAlertDialogRxUtil<
                    ProductIdentifierType,
                    THProductDetailType,
                    THBillingLogicHolderType,
                    ProductDetailViewType,
                    ProductDetailAdapterType> billingAlertDialogUtil,
            @NonNull THBillingRequisitePreparer billingRequisitePreparer)
    {
        super(billingLogicHolder);
        this.activityProvider = activityProvider;
        this.progressDialogUtil = progressDialogUtil;
        this.billingAlertDialogUtil = billingAlertDialogUtil;
        this.billingAlertDialogUtil.setStoreName(getName());
        this.billingRequisitePreparer = billingRequisitePreparer;
    }
    //</editor-fold>

    @NonNull @Override public Observable<BillingTestResult> test()
    {
        int requestCode = billingLogicHolder.getUnusedRequestCode();
        //noinspection unchecked
        return billingLogicHolder.test(requestCode)
                .onErrorResumeNext(error -> billingAlertDialogUtil.popError(activityProvider.get(), error)
                        .flatMap(
                                (Func1<Pair<DialogInterface, Integer>, Observable<BillingTestResult>>)
                                        pair -> Observable.error(error) // We need to still pass on the error
                        ));
    }
}
