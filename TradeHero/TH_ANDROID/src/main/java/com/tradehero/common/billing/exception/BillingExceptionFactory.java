package com.tradehero.common.billing.exception;

/**
 * Created by xavier on 2/21/14.
 */
public interface BillingExceptionFactory
{
    BillingException create(int responseStatus);
    BillingException create(int responseStatus, String message);
}
