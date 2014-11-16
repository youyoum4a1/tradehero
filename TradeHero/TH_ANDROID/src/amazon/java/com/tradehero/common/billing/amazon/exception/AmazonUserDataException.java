package com.tradehero.common.billing.amazon.exception;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.UserDataResponse;

public class AmazonUserDataException extends AmazonException
{
    @NonNull public final UserDataResponse userDataResponse;

    public AmazonUserDataException(String message, @NonNull UserDataResponse userDataResponse)
    {
        super(message);
        this.userDataResponse = userDataResponse;
        if (!userDataResponse.getRequestStatus().equals(UserDataResponse.RequestStatus.FAILED))
        {
            throw new IllegalArgumentException("UserDataResponse status was " + userDataResponse.getRequestStatus());
        }
    }
}
