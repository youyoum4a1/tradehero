package com.tradehero.common.billing.amazon.service;

import android.support.annotation.NonNull;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.UserDataResponse;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseException;
import rx.Observable;
import rx.Subscriber;

public class AmazonPurchasingServicePurchaseOperator implements Observable.OnSubscribe<PurchaseResponse>
{
    @NonNull private final AmazonPurchasingService amazonPurchasingService;
    @NonNull private final String sku;

    //<editor-fold desc="Constructors">
    public AmazonPurchasingServicePurchaseOperator(
            @NonNull AmazonPurchasingService amazonPurchasingService,
            @NonNull String sku)
    {
        this.amazonPurchasingService = amazonPurchasingService;
        this.sku = sku;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super PurchaseResponse> subscriber)
    {
        amazonPurchasingService.purchase(
                sku,
                new PurchasingListener()
                {
                    @Override public void onUserDataResponse(UserDataResponse userDataResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onPurchaseResponse for sku " + sku));
                    }

                    @Override public void onProductDataResponse(ProductDataResponse productDataResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onPurchaseResponse for sku " + sku));
                    }

                    @Override public void onPurchaseResponse(PurchaseResponse purchaseResponse)
                    {
                        if (purchaseResponse.getRequestStatus().equals(PurchaseResponse.RequestStatus.FAILED))
                        {
                            subscriber.onError(
                                    new AmazonPurchaseException(
                                            "Failed purchasing " + sku,
                                            sku,
                                            purchaseResponse));
                        }
                        else
                        {
                            subscriber.onNext(purchaseResponse);
                            subscriber.onCompleted();
                        }
                    }

                    @Override public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onPurchaseResponse for sku " + sku));
                    }
                });
    }
}
