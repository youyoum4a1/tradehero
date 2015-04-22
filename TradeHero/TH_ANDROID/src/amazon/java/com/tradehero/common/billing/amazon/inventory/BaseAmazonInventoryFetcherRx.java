package com.tradehero.common.billing.amazon.inventory;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.tradehero.common.billing.amazon.AmazonActor;
import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingServiceProductDataOperator;
import com.tradehero.common.billing.inventory.BaseBillingInventoryFetcherRx;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Product Identifier Fetcher and Inventory Fetcher are essentially making the same calls.
 */
abstract public class BaseAmazonInventoryFetcherRx<
        AmazonSKUType extends AmazonSKU,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>>
        extends BaseBillingInventoryFetcherRx<
        AmazonSKUType,
        AmazonProductDetailType>
        implements AmazonInventoryFetcherRx<
        AmazonSKUType,
        AmazonProductDetailType>,
        AmazonActor
{
    @NonNull protected final AmazonPurchasingService purchasingService;

    //<editor-fold desc="Constructors">
    public BaseAmazonInventoryFetcherRx(
            int request,
            @NonNull List<AmazonSKUType> productIdentifiers,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request, productIdentifiers);
        this.purchasingService = purchasingService;
    }
    //</editor-fold>

    @NonNull @Override public Observable<ProductInventoryResult<
            AmazonSKUType,
            AmazonProductDetailType>> get()
    {
        return Observable.create(
                new AmazonPurchasingServiceProductDataOperator(
                        purchasingService,
                        getSkuSet()))
                .flatMap(new Func1<ProductDataResponse, Observable<? extends ProductInventoryResult<
                        AmazonSKUType,
                        AmazonProductDetailType>>>()
                {
                    @Override public Observable<? extends ProductInventoryResult<
                            AmazonSKUType,
                            AmazonProductDetailType>> call(ProductDataResponse response)
                    {
                        reportUnavailable(response);
                        Map<AmazonSKUType, AmazonProductDetailType> mapped = new HashMap<>();
                        AmazonSKUType sku;
                        for (Map.Entry<String, Product> entry : response.getProductData().entrySet())
                        {
                            sku = createAmazonSku(entry.getKey());
                            mapped.put(sku, createAmazonProductDetail(sku, entry.getValue()));
                        }
                        return Observable.just(new ProductInventoryResult<>(getRequestCode(), mapped));
                    }
                });
    }

    @NonNull protected Set<String> getSkuSet()
    {
        Set<String> skus = new HashSet<>();
        for (AmazonSKUType sku : getProductIdentifiers())
        {
            skus.add(sku.skuId);
        }
        return skus;
    }

    private void reportUnavailable(@NonNull ProductDataResponse response)
    {
        if (response.getUnavailableSkus() != null)
        {
            reportUnavailable(response.getUnavailableSkus());
        }
    }

    private void reportUnavailable(@NonNull Set<String> unavailable)
    {
        if (unavailable.size() > 0)
        {
            StringBuilder sb = new StringBuilder();
            String separator = "";
            for (String value : unavailable)
            {
                sb.append(separator).append(value);
                separator = ", ";
            }
            Timber.e(new Exception("Unavailable Skus found"), "Unavailable Skus found %s", sb);
        }
    }

    @NonNull abstract protected AmazonSKUType createAmazonSku(@NonNull String skuId);

    @NonNull abstract protected AmazonProductDetailType createAmazonProductDetail(@NonNull AmazonSKUType amazonSKU, @NonNull Product product);
}
