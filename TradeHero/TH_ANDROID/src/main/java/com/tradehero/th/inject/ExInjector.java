package com.ayondo.academy.inject;

/**
 * An injector which can evolve itself to be a more powerful injector, who can provides more objects to the injecting subject,
 * additional objects are come from the modules specified in the parameter list
 */
public interface ExInjector extends Injector
{
    ExInjector plus(Object... modules);
}
