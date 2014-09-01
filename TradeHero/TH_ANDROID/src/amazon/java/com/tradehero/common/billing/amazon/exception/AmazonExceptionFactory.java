package com.tradehero.common.billing.amazon.exception;

import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.exception.BillingExceptionFactory;

public class AmazonExceptionFactory implements BillingExceptionFactory
{
    @Override public BillingException create(int responseStatus)
    {
        return null;
    }

    @Override public BillingException create(int responseStatus, String message)
    {
        return null;
    }

    public AmazonException create(ProductDataResponse.RequestStatus requestStatus, String message)
    {
        switch (requestStatus)
        {
            case FAILED:
                return new AmazonFetchInventoryFailedException(message);

            case NOT_SUPPORTED:
                return new AmazonFetchInventoryUnsupportedException(message);

            case SUCCESSFUL:
                throw new IllegalArgumentException("ProductDataResponse.RequestStatus.SUCCESSFUL is not an error");

            default:
                throw new IllegalArgumentException("Unhandled ProductDataResponse.RequestStatus." + requestStatus);
        }
    }

    public AmazonException create(PurchaseResponse.RequestStatus requestStatus, String message)
    {
        switch (requestStatus)
        {
            case ALREADY_PURCHASED:
                return new AmazonAlreadyPurchasedException(message);

            case INVALID_SKU:
                return new AmazonInvalidSkuException(message);

            case FAILED:
                return new AmazonPurchaseFailedException(message);

            case NOT_SUPPORTED:
                return new AmazonPurchaseUnsupportedException(message);

            case SUCCESSFUL:
                throw new IllegalArgumentException("PurchaseResponse.RequestStatus.SUCCESSFUL is not an error");

            default:
                throw new IllegalArgumentException("Unhandled PurchaseResponse.RequestStatus." + requestStatus);
        }
    }

    public AmazonException create(PurchaseUpdatesResponse.RequestStatus requestStatus, String message)
    {
        switch (requestStatus)
        {
            case FAILED:
                return new AmazonPurchaseUpdateFailedException(message);

            case NOT_SUPPORTED:
                return new AmazonPurchaseUpdateUnsupportedException(message);

            case SUCCESSFUL:
                throw new IllegalArgumentException("PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL is not an error");

            default:
                throw new IllegalArgumentException("Unhandled PurchaseUpdatesResponse.RequestStatus." + requestStatus);
        }
    }
}
