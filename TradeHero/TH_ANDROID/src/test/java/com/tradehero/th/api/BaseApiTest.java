package com.tradehero.th.api;

abstract public class BaseApiTest
{
    protected String getPackagePath()
    {
        return '/' + getClass().getPackage().getName().replace('.', '/');
    }
}
