package com.tradehero.common.billing.amazon.identifier;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonSKUList;
import com.tradehero.common.billing.identifier.BaseProductIdentifierFetcherRx;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

abstract public class BaseAmazonProductIdentifierFetcherRx<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>>
        extends BaseProductIdentifierFetcherRx<
        AmazonSKUListKeyType,
        AmazonSKUType,
        AmazonSKUListType>
        implements AmazonProductIdentifierFetcherRx<
        AmazonSKUListKeyType,
        AmazonSKUType,
        AmazonSKUListType>
{
    public static final int FIRST_ITEM_NUM = 1;

    //<editor-fold desc="Constructors">
    public BaseAmazonProductIdentifierFetcherRx(int requestCode)
    {
        super(requestCode);
    }
    //</editor-fold>

    @NonNull @Override public Observable<ProductIdentifierListResult<
            AmazonSKUListKeyType,
            AmazonSKUType,
            AmazonSKUListType>> get()
    {
        List<ProductIdentifierListResult<
                        AmazonSKUListKeyType,
                        AmazonSKUType,
                        AmazonSKUListType>> typeList = new ArrayList<>();
        for (ProductType productType : ProductType.values())
        {
            AmazonSKUListType idList = createAmazonSKUList();
            populate(idList, productType);
            typeList.add(new ProductIdentifierListResult<>(
                    getRequestCode(),
                    createAmazonListKey(productType),
                    idList));
        }
        return Observable.from(typeList);
    }

    @NonNull abstract protected AmazonSKUListType createAmazonSKUList();

    abstract protected void populate(@NonNull AmazonSKUListType list, @NonNull ProductType productType);

    @NonNull abstract protected AmazonSKUListKeyType createAmazonListKey(@NonNull ProductType productType);
}
