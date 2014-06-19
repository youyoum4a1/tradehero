package com.tradehero.routable;

/**
 * Thrown if no context has been found.
 */
public class ContextNotProvided extends RuntimeException
{
    public ContextNotProvided(String message)
    {
        super(message);
    }
}
