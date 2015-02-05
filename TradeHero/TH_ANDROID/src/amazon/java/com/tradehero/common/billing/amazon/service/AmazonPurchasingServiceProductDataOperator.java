package com.tradehero.common.billing.amazon.service;

import android.support.annotation.NonNull;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.UserDataResponse;
import com.tradehero.common.billing.amazon.exception.AmazonProductDataException;
import java.util.Set;
import rx.Observable;
import rx.Subscriber;

public class AmazonPurchasingServiceProductDataOperator implements Observable.OnSubscribe<ProductDataResponse>
{
    @NonNull private final AmazonPurchasingService amazonPurchasingService;
    @NonNull private final Set<String> skus;

    //<editor-fold desc="Constructors">
    public AmazonPurchasingServiceProductDataOperator(
            @NonNull AmazonPurchasingService amazonPurchasingService,
            @NonNull Set<String> skus)
    {
        this.amazonPurchasingService = amazonPurchasingService;
        this.skus = skus;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super ProductDataResponse > subscriber)
    {
        amazonPurchasingService.getProductData(
                skus,
                new PurchasingListener()
                {
                    @Override public void onUserDataResponse(UserDataResponse userDataResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onProductDataResponse for " + serialisedSkus()));
                    }

                    @Override public void onProductDataResponse(ProductDataResponse productDataResponse)
                    {
                        if (productDataResponse.getRequestStatus().equals(ProductDataResponse.RequestStatus.FAILED))
                        {
                            subscriber.onError(
                                    new AmazonProductDataException(
                                            "Failed getting ProductDataResponse for " + serialisedSkus(),
                                            skus,
                                            productDataResponse));
                        }
                        else
                        {
                            subscriber.onNext(productDataResponse);
                            subscriber.onCompleted();
                        }
                    }

                    @Override public void onPurchaseResponse(PurchaseResponse purchaseResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onProductDataResponse for " + serialisedSkus()));
                    }

                    @Override public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse)
                    {
                        subscriber.onError(new IllegalStateException("We expected only onProductDataResponse for " + serialisedSkus()));
                    }
                });
    }

    @NonNull private String serialisedSkus()
    {
        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (String sku : skus)
        {
            sb.append(separator).append(sku);
            separator = ", ";
        }
        return sb.toString();
    }
}
