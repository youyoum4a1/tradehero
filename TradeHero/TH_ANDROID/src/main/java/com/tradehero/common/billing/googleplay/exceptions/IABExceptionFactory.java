package com.tradehero.common.billing.googleplay.exceptions;

import com.tradehero.common.billing.googleplay.Constants;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 6:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class IABExceptionFactory
{
    public static final String TAG = IABExceptionFactory.class.getSimpleName();

    @Inject public IABExceptionFactory()
    {
        super();
    }

    public IABException create(int responseStatus)
    {
        return create(responseStatus, Constants.getStatusCodeDescription(responseStatus));
    }

    public IABException create(int responseStatus, String message)
    {
        switch (responseStatus)
        {
            case Constants.BILLING_RESPONSE_RESULT_OK:
                throw new IllegalArgumentException(responseStatus + " does not map to an exception");

            case Constants.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
                return new IABBillingUnavailableException(message);

            case Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
                return new IABAlreadyOwnedException(message);

            case Constants.IABHELPER_REMOTE_EXCEPTION:
                return new IABRemoteException(message);

            case Constants.IABHELPER_BAD_RESPONSE:
                return new IABBadResponseException(message);

            case Constants.IABHELPER_VERIFICATION_FAILED:
                return new IABVerificationFailedException(message);

            case Constants.IABHELPER_SEND_INTENT_FAILED:
                return new IABSendIntentException(message);

            case Constants.IABHELPER_USER_CANCELLED:
                return new IABUserCancelledException(message);

            case Constants.IABHELPER_UNKNOWN_PURCHASE_RESPONSE:
                return new IABUnknownPurchaseResponseException(message);

            case Constants.IABHELPER_UNKNOWN_ERROR:
                return new IABUnknownErrorException(message);

            case Constants.IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE:
                return new IABSubscriptionUnavailableException(message);

            default:
                return new IABException(responseStatus, message);
        }
    }
}
