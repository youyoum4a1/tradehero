package com.androidth.general.common.billing.samsung.exception;

import com.androidth.general.common.billing.samsung.SamsungConstants;

public class SamsungGroupNotExistException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungConstants.IAP_ERROR_ITEM_GROUP_DOES_NOT_EXIST;

    //<editor-fold desc="Constructors">
    public SamsungGroupNotExistException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
