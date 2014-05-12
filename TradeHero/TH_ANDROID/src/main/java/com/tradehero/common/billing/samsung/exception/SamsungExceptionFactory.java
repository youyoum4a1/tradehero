package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.samsung.SamsungConstants;


public class SamsungExceptionFactory
        implements BillingExceptionFactory
{
    @Override public SamsungException create(int responseStatus)
    {
        return create(responseStatus, null);
    }

    public SamsungException create(ErrorVo errorVo)
    {
        return create(errorVo.getErrorCode(), errorVo.dump());
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

            case SamsungConstants.IAP_ERROR_ITEM_GROUP_DOES_NOT_EXIST: // -1007
                exception = new SamsungGroupNotExistException(message);
                break;

            case SamsungConstants.IAP_ERROR_NETWORK_NOT_AVAILABLE: // -1008
                exception = new SamsungNetworkUnavailableException(message);
                break;

            case SamsungConstants.IAP_ERROR_IOEXCEPTION_ERROR: // -1009
                exception = new SamsungIOException(message);
                break;

            case SamsungConstants.IAP_ERROR_SOCKET_TIMEOUT: // -1010
                exception = new SamsungSocketTimeoutException(message);
                break;

            case SamsungConstants.IAP_ERROR_CONNECT_TIMEOUT: // -1011
                exception = new SamsungConnectTimeoutException(message);
                break;
        }
        return exception;
    }
}
