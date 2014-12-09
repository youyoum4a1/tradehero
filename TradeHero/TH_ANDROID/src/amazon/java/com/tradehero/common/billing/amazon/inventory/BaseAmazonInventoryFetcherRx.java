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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import rx.Observable;
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
                .doOnNext(this::reportUnavailable)
                .flatMap(response -> Observable.from(response.getProductData().entrySet()))
                .map(this::createResult);
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

    @NonNull private ProductInventoryResult<AmazonSKUType,
            AmazonProductDetailType> createResult(@NonNull Map.Entry<String, Product> pair)
    {
        AmazonSKUType sku = createAmazonSku(pair.getKey());
        return new ProductInventoryResult<>(getRequestCode(),
                sku,
                createAmazonProductDetail(sku, pair.getValue()));
    }

    @NonNull abstract protected AmazonSKUType createAmazonSku(@NonNull String skuId);

    @NonNull abstract protected AmazonProductDetailType createAmazonProductDetail(@NonNull AmazonSKUType amazonSKU, @NonNull Product product);
}
