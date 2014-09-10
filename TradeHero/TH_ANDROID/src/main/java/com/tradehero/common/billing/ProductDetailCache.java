package com.tradehero.common.billing;

import com.tradehero.common.persistence.StraightDTOCacheNew;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

abstract public class ProductDetailCache<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailsType extends ProductDetail<ProductIdentifierType>,
            ProductTunerType extends ProductDetailTuner<ProductIdentifierType, ProductDetailsType>>
        extends StraightDTOCacheNew<ProductIdentifierType, ProductDetailsType>
{
    private static final int DEFAULT_MAX_SIZE = 200;
    public static int latest = 0;

    @NotNull protected final ProductTunerType detailsTuner;
    protected int me = latest++;

    //<editor-fold desc="Constructors">
    public ProductDetailCache(@NotNull ProductTunerType detailsTuner)
    {
        this(DEFAULT_MAX_SIZE, detailsTuner);
    }

    public ProductDetailCache(int defaultMaxSize, @NotNull ProductTunerType detailsTuner)
    {
        super(defaultMaxSize);
        this.detailsTuner = detailsTuner;
    }
    //</editor-fold>

    @Override @Nullable public ProductDetailsType get(@NotNull ProductIdentifierType key)
    {
        Timber.d("get me %d", me);
        return super.get(key);
    }

    @Override @Nullable public ProductDetailsType put(@NotNull ProductIdentifierType key, @NotNull ProductDetailsType value)
    {
        Timber.d("put me %d", me);
        detailsTuner.fineTune(value);
        return super.put(key, value);
    }

    public HashMap<ProductIdentifierType, ProductDetailsType> put(Map<ProductIdentifierType, ProductDetailsType> inventory)
    {
        Timber.d("putMap me %d", me);
        if (inventory == null)
        {
            Timber.d("putMap null me %d", me);
            return null;
        }

        HashMap<ProductIdentifierType, ProductDetailsType> previousValues = new HashMap<>();

        for (Map.Entry<ProductIdentifierType, ProductDetailsType> entry : inventory.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }

        return previousValues;
    }

    public List<ProductDetailsType> put(List<ProductDetailsType> values)
    {
        Timber.d("putList me %d", me);
        if (values == null)
        {
            Timber.d("putList null me %d", me);
            return null;
        }

        List<ProductDetailsType> previousValues = new ArrayList<>();

        for (ProductDetailsType skuDetails: values)
        {
            previousValues.add(put(skuDetails.getProductIdentifier(), skuDetails));
        }

        return previousValues;
    }

    public List<ProductDetailsType> get(List<ProductIdentifierType> keys)
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
            skuDetails.add(get(key));
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
            map.put(id, get(id));
        }
        return map;
    }
}
