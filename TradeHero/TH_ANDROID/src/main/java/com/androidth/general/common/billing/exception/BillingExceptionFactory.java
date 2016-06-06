package com.androidth.general.common.billing.exception;

public interface BillingExceptionFactory
{
    BillingException create(int responseStatus);
    BillingException create(int responseStatus, String message);
}
