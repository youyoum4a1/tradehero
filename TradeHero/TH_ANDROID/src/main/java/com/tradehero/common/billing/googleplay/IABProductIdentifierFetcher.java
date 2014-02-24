package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public interface IABProductIdentifierFetcher<
        IABSKUType extends IABSKU,
        IABExceptionType extends IABException>
    extends ProductIdentifierFetcher<
        IABSKUType,
        IABExceptionType>
{
}
