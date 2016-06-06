package com.androidth.general.common.billing.googleplay.consume;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.BaseIABServiceCaller;
import com.androidth.general.common.billing.googleplay.IABConstants;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABServiceResult;
import com.androidth.general.common.billing.googleplay.exception.IABException;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.billing.googleplay.exception.IABMissingTokenException;
import com.tradehero.th.BuildConfig;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

abstract public class BaseIABPurchaseConsumerRx<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends BaseIABServiceCaller
        implements IABPurchaseConsumerRx<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>
{
    @NonNull protected final IABPurchaseType purchase;

    //<editor-fold desc="Constructors">
    protected BaseIABPurchaseConsumerRx(
            int requestCode,
            @NonNull IABPurchaseType purchase,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
        this.purchase = purchase;
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseConsumeResult<IABSKUType, IABOrderIdType, IABPurchaseType>> get()
    {
        return getBillingServiceResult()
                .flatMap(new Func1<IABServiceResult, Observable<? extends IABPurchaseType>>()
                {
                    @Override public Observable<? extends IABPurchaseType> call(IABServiceResult result)
                    {
                        return BaseIABPurchaseConsumerRx.this.consume(result);
                    }
                })
                .map(new Func1<IABPurchaseType, PurchaseConsumeResult<IABSKUType, IABOrderIdType, IABPurchaseType>>()
                {
                    @Override public PurchaseConsumeResult<IABSKUType, IABOrderIdType, IABPurchaseType> call(IABPurchaseType purchase)
                    {
                        return new PurchaseConsumeResult<>(BaseIABPurchaseConsumerRx.this.getRequestCode(), purchase);
                    }
                });
    }

    protected Observable<IABPurchaseType> consume(@NonNull IABServiceResult serviceResult)
    {
        if (purchase.getType().equals(IABConstants.ITEM_TYPE_SUBS))
        {
            return Observable.just(purchase);
        }
        else if (purchase.getToken() == null)
        {
            return Observable.error(new IABMissingTokenException("Token cannot be null"));
        }
        try
        {
            return consumeEffectively(serviceResult);
        } catch (RemoteException e)
        {
            return Observable.error(e);
        }
    }

    private Observable<IABPurchaseType> consumeEffectively(@NonNull IABServiceResult serviceResult)
            throws RemoteException, IABException
    {
        String sku = this.purchase.getProductIdentifier().identifier;
        String token = this.purchase.getToken();
        Timber.d("Consuming sku: %s, token: %s", sku, token);
        int response = serviceResult.billingService.consumePurchase(
                TARGET_BILLING_API_VERSION3,
                BuildConfig.GOOGLE_PLAY_PACKAGE_NAME,
                token);
        if (response != IABConstants.BILLING_RESPONSE_RESULT_OK)
        {
            return Observable.error(iabExceptionFactory.create(response));
        }
        return Observable.just(purchase);
    }
}
