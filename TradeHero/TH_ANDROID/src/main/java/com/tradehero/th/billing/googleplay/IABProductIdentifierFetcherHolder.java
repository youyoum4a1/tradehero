package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public interface IABProductIdentifierFetcherHolder<
        IABSKUType extends IABSKU,
        IABExceptionType extends IABException>
    extends ProductIdentifierFetcherHolder<IABSKUType, IABExceptionType>
{
}
