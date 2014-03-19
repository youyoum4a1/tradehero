package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 10:51 AM To change this template use File | Settings | File Templates. */
public interface ProductDetailDomainInformer<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>>
{
    List<ProductDetailType> getDetailsOfDomain(String domain);
}
