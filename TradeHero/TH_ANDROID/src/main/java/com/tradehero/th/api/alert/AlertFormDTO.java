package com.tradehero.th.api.alert;


public class AlertFormDTO
{
    public static final String TAG = AlertFormDTO.class.getSimpleName();

    public int securityId;
    public boolean active;
    public Boolean upOrDown;
    public Double priceMovement;
    public double targetPrice;
}
