package com.tradehero.th.api.purchase;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:31 PM To change this template use File | Settings | File Templates. */
public class RecurringProductsDTO
{
    public static final String TAG = RecurringProductsDTO.class.getSimpleName();

    public List<RecurringProductDTO> availableProducts;

    public RecurringProductsDTO()
    {
        super();
    }
}
