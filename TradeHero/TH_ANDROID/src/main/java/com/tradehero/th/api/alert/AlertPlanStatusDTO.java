package com.tradehero.th.api.alert;

/**
 * Created by xavier on 2/12/14.
 */
public class AlertPlanStatusDTO
{
    public static final String TAG = AlertPlanStatusDTO.class.getSimpleName();

    public String product_id;
    public String productId;
    public boolean isYours;

    // TODO remove this HACK when the server has been newly deployed
    public String getProductId()
    {
        return productId != null ? productId : product_id;
    }
}
