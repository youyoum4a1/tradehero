package com.tradehero.common.billing.amazon.service;

import android.support.annotation.NonNull;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.UserDataResponse;
import com.tradehero.common.billing.amazon.exception.AmazonUserDataException;
import rx.Observable;
import rx.Subscriber;

public class AmazonPurchasingServiceUserDataOperator implements Observable.OnSubscribe<UserDataResponse>
{
    @NonNull private final AmazonPurchasingService amazonPurchasingService;

    //<editor-fold desc="Constructors">
    public AmazonPurchasingServiceUserDataOperator(@NonNull AmazonPurchasingService amazonPurchasingService)
    {
        this.amazonPurchasingService = amazonPurchasingService;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super UserDataResponse> subscriber)
    {
        amazonPurchasingService.getUserData(new PurchasingListener()
        {
            @Override public void onUserDataResponse(UserDataResponse userDataResponse)
            {
                if (userDataResponse.getRequestStatus().equals(UserDataResponse.RequestStatus.FAILED))
                {
                    subscriber.onError(
                            new AmazonUserDataException(
                                    "Failed getting UserDataResponse",
                                    userDataResponse));
                }
                else
                {
                    subscriber.onNext(userDataResponse);
                    subscriber.onCompleted();
                }
            }

            @Override public void onProductDataResponse(ProductDataResponse productDataResponse)
            {
                subscriber.onError(new IllegalStateException("We expected only onUserDataResponse"));
            }

            @Override public void onPurchaseResponse(PurchaseResponse purchaseResponse)
            {
                subscriber.onError(new IllegalStateException("We expected only onUserDataResponse"));
            }

            @Override public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse)
            {
                subscriber.onError(new IllegalStateException("We expected only onUserDataResponse"));
            }
        });
    }
}
