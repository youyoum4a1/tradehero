package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;


public interface IABProductIdentifierFetcherHolder<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>,
        IABExceptionType extends IABException>
    extends ProductIdentifierFetcherHolder<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType,
        IABExceptionType>
{
}
