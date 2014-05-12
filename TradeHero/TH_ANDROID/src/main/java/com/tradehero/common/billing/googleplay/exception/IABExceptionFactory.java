package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.googleplay.IABConstants;
import javax.inject.Inject;
import javax.inject.Singleton;

public class IABExceptionFactory implements BillingExceptionFactory
{
    public IABExceptionFactory()
    {
        super();
    }

    public IABException create(int responseStatus)
    {
        return create(responseStatus, IABConstants.getStatusCodeDescription(responseStatus));
    }

    public IABException create(int responseStatus, String message)
    {
        switch (responseStatus)
        {
            case IABConstants.BILLING_RESPONSE_RESULT_OK: // 0
                throw new IllegalArgumentException(responseStatus + " does not map to an exception");

            case IABConstants.BILLING_RESPONSE_RESULT_USER_CANCELED: // 1
                return new IABUserCancelledBillingException(message);

            case IABConstants.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE: // 3
                return new IABBillingUnavailableException(message);

            case IABConstants.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE: // 4
                return new IABItemUnavailableException(message);

            case IABConstants.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR: // 5
                return new IABDeveloperErrorException(message);

            case IABConstants.BILLING_RESPONSE_RESULT_ERROR: // 6
                return new IABResultErrorException(message);

            case IABConstants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED: // 7
                return new IABItemAlreadyOwnedException(message);

            case IABConstants.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED: // 8
                return new IABItemNotOwnedException(message);

            case IABConstants.IABHELPER_ERROR_BASE: // -1000
                return new IABErrorBaseException(message);

            case IABConstants.IABHELPER_REMOTE_EXCEPTION: // -1001
                return new IABRemoteException(message);

            case IABConstants.IABHELPER_BAD_RESPONSE: // -1002
                return new IABBadResponseException(message);

            case IABConstants.IABHELPER_VERIFICATION_FAILED: // -1003
                return new IABVerificationFailedException(message);

            case IABConstants.IABHELPER_SEND_INTENT_FAILED: // -1004
                return new IABSendIntentException(message);

            case IABConstants.IABHELPER_USER_CANCELLED: // -1005
                return new IABUserCancelledException(message);

            case IABConstants.IABHELPER_UNKNOWN_PURCHASE_RESPONSE: // -1006
                return new IABUnknownPurchaseResponseException(message);

            case IABConstants.IABHELPER_MISSING_TOKEN: // -1007
                return new IABMissingTokenException(message);

            case IABConstants.IABHELPER_UNKNOWN_ERROR: // -1008
                return new IABUnknownErrorException(message);

            case IABConstants.IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE: // -1009
                return new IABSubscriptionUnavailableException(message);

            case IABConstants.IABHELPER_INVALID_CONSUMPTION: // -1010
                return new IABInvalidConsumptionException(message);

            default:
                return new IABException(responseStatus, message);
        }
    }
}
