package com.tradehero.common.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import java.util.Map;

abstract public class ProductIdentifierListCacheRx<
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListKeyType extends ProductIdentifierListKey,
            ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        extends BaseDTOCacheRx<ProductIdentifierListKeyType, ProductIdentifierListType>
{
    //<editor-fold desc="Constructors">
    public ProductIdentifierListCacheRx(int maxSize,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize, 5, dtoCacheUtil);
    }
    //</editor-fold>

    abstract public ProductIdentifierListKeyType getKeyForAll();

    public void onNext(Map<ProductIdentifierListKeyType, ProductIdentifierListType> typedLists)
    {
        for (Map.Entry<ProductIdentifierListKeyType, ProductIdentifierListType> entry : typedLists.entrySet())
        {
            onNext(entry.getKey(), entry.getValue());
        }
    }
}
