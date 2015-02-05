package com.tradehero.common.billing.amazon.exception;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.ProductDataResponse;
import java.util.Set;

public class AmazonProductDataException extends AmazonException
{
    @NonNull public final Set<String> skus;
    @NonNull public final ProductDataResponse productDataResponse;

    public AmazonProductDataException(String message,
            @NonNull Set<String> skus,
            @NonNull ProductDataResponse productDataResponse)
    {
        super(message);
        this.skus = skus;
        this.productDataResponse = productDataResponse;
        if (!productDataResponse.getRequestStatus().equals(ProductDataResponse.RequestStatus.FAILED))
        {
            throw new IllegalArgumentException("ProductDataResponse status was " + productDataResponse.getRequestStatus());
        }
    }
}
