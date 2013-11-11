package com.tradehero.th.billing.googleplay;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 10:51 AM To change this template use File | Settings | File Templates. */
public interface SKUDomainInformer
{
    List<THSKUDetails> getDetailsOfDomain(String domain);
}
