package com.androidth.general.common.billing;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
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
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(Map<ProductIdentifierListKeyType, ProductIdentifierListType> typedLists)
    {
        for (Map.Entry<ProductIdentifierListKeyType, ProductIdentifierListType> entry : typedLists.entrySet())
        {
            onNext(entry.getKey(), entry.getValue());
        }
    }
}
