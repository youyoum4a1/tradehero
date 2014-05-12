package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.exception.BillingExceptionFactory;


public class SamsungExceptionFactory
        implements BillingExceptionFactory
{
    @Override public SamsungException create(int responseStatus)
    {
        return create(responseStatus, null);
    }

    @Override public SamsungException create(int responseStatus, String message)
    {
        SamsungException exception = null;
        switch (responseStatus)
        {
            case SamsungIapHelper.IAP_RESPONSE_RESULT_OK: // 0
                throw new IllegalArgumentException("Cannot create an exception with result ok");

            case SamsungIapHelper.IAP_PAYMENT_IS_CANCELED: // 1
                exception = new SamsungPaymentCancelledException(message);
                break;

            case SamsungIapHelper.IAP_ERROR_INITIALIZATION: // -1000
                exception = new SamsungInitialisationException(message);
                break;

            case SamsungIapHelper.IAP_ERROR_NEED_APP_UPGRADE: // -1001
                exception = new SamsungNeedUpgradeException(message);
                break;

            case SamsungIapHelper.IAP_ERROR_COMMON: // -1002
                exception =  new SamsungCommonException(message);
                break;

            case SamsungIapHelper.IAP_ERROR_ALREADY_PURCHASED: // -1003
                exception = new SamsungAlreadyPurchasedException(message);
                break;

            case SamsungIapHelper.IAP_ERROR_WHILE_RUNNING: // -1004
                exception = new SamsungWhileRunningException(message);
                break;

            case SamsungIapHelper.IAP_ERROR_PRODUCT_DOES_NOT_EXIST: // -1005
                exception = new SamsungProductNotExistException(message);
                break;

            case SamsungIapHelper.IAP_ERROR_CONFIRM_INBOX: // -1006
                exception = new SamsungConfirmInboxException(message);
                break;
        }
        return exception;
    }
}
