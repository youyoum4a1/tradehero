package com.tradehero.th.network.service;

import com.android.internal.util.Predicate;
import com.tradehero.AbstractTestBase;
import java.util.ArrayList;

abstract public class AbstractServiceTestBase extends AbstractTestBase
{
    public ArrayList<Class<?>> getAllServices()
    {
        return getClassesForPackage(
                AlertPlanService.class.getPackage(),
                new Predicate<Class<?>>()
                {
                    @Override public boolean apply(Class<?> aClass)
                    {
                        return aClass.isInterface() && aClass.getSimpleName().matches("^.*Service$");
                    }
                });
    }

    public ArrayList<Class<?>> getAllServiceAsyncs()
    {
        return getClassesForPackage(
                AlertPlanService.class.getPackage(),
                new Predicate<Class<?>>()
                {
                    @Override public boolean apply(Class<?> aClass)
                    {
                        return aClass.isInterface() && aClass.getSimpleName().matches("^.*ServiceAsync$");
                    }
                });
    }

    public ArrayList<Class<?>> getAllServiceWrappers()
    {
        return getClassesForPackage(
                AlertPlanService.class.getPackage(),
                new Predicate<Class<?>>()
                {
                    @Override public boolean apply(Class<?> aClass)
                    {
                        return !aClass.isInterface() && aClass.getSimpleName().matches("^.*ServiceWrapper$");
                    }
                });
    }
}
