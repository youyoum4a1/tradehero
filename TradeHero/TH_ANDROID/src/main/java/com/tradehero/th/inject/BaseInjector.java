package com.ayondo.academy.inject;

import dagger.ObjectGraph;

/**
 * BaseInjector is the basic extendable injector, which can both inject and extend itself with provided modules
 */
public class BaseInjector implements ExInjector
{
    private final ObjectGraph objectGraph;

    public BaseInjector(ObjectGraph objectGraph)
    {
        this.objectGraph = objectGraph;
    }

    @Override public ExInjector plus(Object... modules)
    {
        return new BaseInjector(objectGraph.plus(modules));
    }

    @Override public void inject(Object o)
    {
        objectGraph.inject(o);
    }
}
