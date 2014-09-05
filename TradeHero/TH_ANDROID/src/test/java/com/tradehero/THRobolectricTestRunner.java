package com.tradehero;

import org.junit.runners.model.InitializationError;

public class THRobolectricTestRunner extends RobolectricGradleTestRunner
{
    public THRobolectricTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }
}
