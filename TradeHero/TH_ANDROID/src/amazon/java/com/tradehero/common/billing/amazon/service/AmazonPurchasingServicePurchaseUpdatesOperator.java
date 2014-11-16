package com.tradehero.common.billing.amazon.service;

import android.support.annotation.NonNull;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.UserDataResponse;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseUpdatesException;
import rx.Observable;
import rx.Subscriber;

public class AmazonPurchasingServicePurchaseUpdatesOperator implements Observable.OnSubscribe<PurchaseUpdatesResponse>
{
    @NonNull private final AmazonPurchasingService amazonPurchasingService;

    //<editor-fold desc="Constructors">
    public AmazonPurchasingServicePurchaseUpdatesOperator(@NonNull AmazonPurchasingService amazonPurchasingService)
    {
        this.amazonPurchasingService = amazonPurchasingService;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super PurchaseUpdatesResponse> subscriber)
    {
        call(true, subscriber);
    }

    private void call(boolean reset, Subscriber<? super PurchaseUpdatesResponse> subscriber)
    {
        amazonPurchasingService.getPurchaseUpdates(
                reset,
                new PurchasingListener()
                {
                    @Override public void onUserDataResponse(UserDataResponse userDataResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onPurchaseUpdatesResponse with reset " + reset));
                    }

                    @Override public void onProductDataResponse(ProductDataResponse productDataResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onPurchaseUpdatesResponse with reset " + reset));
                    }

                    @Override public void onPurchaseResponse(PurchaseResponse purchaseResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onPurchaseUpdatesResponse with reset " + reset));
                    }

                    @Override public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse)
                    {
                        if (purchaseUpdatesResponse.getRequestStatus().equals(PurchaseUpdatesResponse.RequestStatus.FAILED))
                        {
                            subscriber.onError(new AmazonPurchaseUpdatesException("Failed getting purchase updates with reset " + reset, purchaseUpdatesResponse));
                        }
                        else
                        {
                            subscriber.onNext(purchaseUpdatesResponse);
                            if (purchaseUpdatesResponse.hasMore())
                            {
                                call(false, subscriber);
                            }
                            else
                            {
                                subscriber.onCompleted();
                            }
                        }
                    }
                });
    }
}
