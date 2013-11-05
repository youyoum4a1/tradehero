package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifier;

/**
 * Created by julien on 4/11/13
 */
public class SKU implements ProductIdentifier
{
    public final String identifier;

    public SKU(String id) {
        identifier = id;
    }
}
