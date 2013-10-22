package com.tradehero.th.api.purchase;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:32 PM To change this template use File | Settings | File Templates. */
public class NonRecurringProductsDTO
{
    public static final String TAG = NonRecurringProductsDTO.class.getSimpleName();

    public List<NonRecurringProductDTO> availableProducts;

    public NonRecurringProductsDTO()
    {
        super();
    }
}
