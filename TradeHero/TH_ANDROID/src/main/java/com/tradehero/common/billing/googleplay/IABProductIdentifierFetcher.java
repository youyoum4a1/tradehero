package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;


public interface IABProductIdentifierFetcher<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseProductIdentifierList<IABSKUType>,
        IABExceptionType extends IABException>
    extends ProductIdentifierFetcher<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType,
        IABExceptionType>
{
}
