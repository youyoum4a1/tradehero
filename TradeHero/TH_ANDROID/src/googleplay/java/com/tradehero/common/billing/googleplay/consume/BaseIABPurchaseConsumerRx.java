package com.tradehero.common.billing.googleplay.consume;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABServiceCaller;
import com.tradehero.common.billing.googleplay.BillingServiceBinderObservable;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABServiceResult;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.exception.IABMissingTokenException;
import com.tradehero.th.BuildConfig;
import rx.Observable;
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
            @NonNull IABExceptionFactory iabExceptionFactory,
            @NonNull BillingServiceBinderObservable billingServiceBinderObservable)
    {
        super(requestCode, context, iabExceptionFactory, billingServiceBinderObservable);
        this.purchase = purchase;
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseConsumeResult<IABSKUType, IABOrderIdType, IABPurchaseType>> get()
    {
        return getBillingServiceResult()
                .flatMap(this::consume)
                .map(purchase -> new PurchaseConsumeResult<>(getRequestCode(), purchase));
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
