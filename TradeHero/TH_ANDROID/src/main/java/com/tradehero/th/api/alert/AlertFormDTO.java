package com.tradehero.th.api.alert;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 1:00 PM To change this template use File | Settings | File Templates. */
public class AlertFormDTO
{
    public static final String TAG = AlertFormDTO.class.getSimpleName();

    public int securityId;
    public boolean active;
    public Boolean upOrDown;
    public Float priceMovement;
    public double targetPrice;

    public static boolean IsValid(AlertFormDTO dto)
    {
        return (dto != null) && (dto.securityId > 0) && (dto.upOrDown != null ^ dto.priceMovement != null) &&
                (dto.targetPrice > 0);
    }
}
