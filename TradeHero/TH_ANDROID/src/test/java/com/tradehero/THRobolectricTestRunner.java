package com.tradehero;

import org.junit.runners.model.InitializationError;

public class THRobolectricTestRunner extends RobolectricMavenTestRunner
{
    public THRobolectricTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }
}
