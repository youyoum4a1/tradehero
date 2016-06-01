package com.ayondo.academy.api;

abstract public class BaseApiTestClass
{
    protected String getPackagePath()
    {
        return '/' + getRelPackagePath();
    }

    protected String getRelPackagePath()
    {
        return getClass().getPackage().getName().replace('.', '/');
    }
}
