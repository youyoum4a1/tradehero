package com.tradehero.common.billing;

import com.tradehero.common.persistence.StraightDTOCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
abstract public class ProductDetailCache<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailsType extends ProductDetail<ProductIdentifierType>,
            ProductTunerType extends ProductDetailTuner<ProductIdentifierType, ProductDetailsType>>
        extends StraightDTOCache<ProductIdentifierType, ProductDetailsType>
{
    private static final int DEFAULT_MAX_SIZE = 200;
    public static int latest = 0;

    protected ProductTunerType detailsTuner;
    protected int me = latest++;

    //<editor-fold desc="Constructors">
    public ProductDetailCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public ProductDetailCache(int defaultMaxSize)
    {
        super(defaultMaxSize);
        createDetailsTuner();
    }
    //</editor-fold>

    abstract protected void createDetailsTuner();

    @Override public ProductDetailsType get(ProductIdentifierType key)
    {
        Timber.d("THIABProductDetailCache get me " + me);
        return super.get(key);
    }

    @Override public ProductDetailsType put(ProductIdentifierType key, ProductDetailsType value)
    {
        Timber.d("THIABProductDetailCache put me " + me);
        detailsTuner.fineTune(value);
        return super.put(key, value);
    }

    public HashMap<ProductIdentifierType, ProductDetailsType> put(Map<ProductIdentifierType, ProductDetailsType> inventory)
    {
        Timber.d("THIABProductDetailCache putMap me " + me);
        if (inventory == null)
        {
            Timber.d("THIABProductDetailCache putMap null me " + me);
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
        Timber.d("THIABProductDetailCache putList me " + me);
        if (values == null)
        {
            Timber.d("THIABProductDetailCache putList null me " + me);
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
        Timber.d("THIABProductDetailCache getList me " + me);
        if (keys == null)
        {
            Timber.d("THIABProductDetailCache getList null me " + me);
            return null;
        }

        List<ProductDetailsType> skuDetails = new ArrayList<>();

        for (ProductIdentifierType key: keys)
        {
            skuDetails.add(get(key));
        }

        return skuDetails;
    }
}
