package com.tradehero.common.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

abstract public class ProductDetailCacheRx<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailsType extends ProductDetail<ProductIdentifierType>,
            ProductTunerType extends ProductDetailTuner<ProductIdentifierType, ProductDetailsType>>
        extends BaseDTOCacheRx<ProductIdentifierType, ProductDetailsType>
{
    private static final int DEFAULT_MAX_SIZE = 200;
    public static int latest = 0;

    @NonNull protected final ProductTunerType detailsTuner;
    protected int me = latest++;

    //<editor-fold desc="Constructors">
    public ProductDetailCacheRx(
            @NonNull ProductTunerType detailsTuner,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        this(DEFAULT_MAX_SIZE, detailsTuner, dtoCacheUtil);
    }

    public ProductDetailCacheRx(int defaultMaxSize,
            @NonNull ProductTunerType detailsTuner,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(defaultMaxSize, 5, dtoCacheUtil);
        this.detailsTuner = detailsTuner;
    }
    //</editor-fold>

    @Override public void onNext(@NonNull ProductIdentifierType key, @NonNull ProductDetailsType value)
    {
        detailsTuner.fineTune(value);
        super.onNext(key, value);
    }

    public void onNext(Map<ProductIdentifierType, ProductDetailsType> inventory)
    {
        Timber.d("putMap me %d", me);
        if (inventory == null)
        {
            Timber.d("putMap null me %d", me);
            return;
        }

        for (Map.Entry<ProductIdentifierType, ProductDetailsType> entry : inventory.entrySet())
        {
            onNext(entry.getKey(), entry.getValue());
        }
    }

    public void onNext(List<ProductDetailsType> values)
    {
        Timber.d("putList me %d", me);
        if (values == null)
        {
            Timber.d("putList null me %d", me);
            return;
        }

        for (ProductDetailsType skuDetails: values)
        {
            onNext(skuDetails.getProductIdentifier(), skuDetails);
        }
    }

    public List<ProductDetailsType> getValues(List<ProductIdentifierType> keys)
    {
        Timber.d("getList me %d", me);
        if (keys == null)
        {
            Timber.d("getList null me %d", me);
            return null;
        }

        List<ProductDetailsType> skuDetails = new ArrayList<>();

        for (ProductIdentifierType key: keys)
        {
            skuDetails.add(getValue(key));
        }

        return skuDetails;
    }

    public HashMap<ProductIdentifierType, ProductDetailsType> getMap(Collection<ProductIdentifierType> ids)
    {
        if (ids == null)
        {
            return null;
        }

        HashMap<ProductIdentifierType, ProductDetailsType> map = new HashMap<>();
        for (ProductIdentifierType id : ids)
        {
            map.put(id, getValue(id));
        }
        return map;
    }
}
