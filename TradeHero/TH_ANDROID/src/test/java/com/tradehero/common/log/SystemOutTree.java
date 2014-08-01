package com.tradehero.common.log;

import javax.inject.Inject;
import timber.log.Timber;

public class SystemOutTree implements Timber.Tree
{
    //<editor-fold desc="Constructors">
    @Inject public SystemOutTree()
    {
        super();
    }
    //</editor-fold>

    @Override public void v(String message, Object... args)
    {
        System.out.println("v: " + String.format(message, args));
    }

    @Override public void v(Throwable t, String message, Object... args)
    {
        System.out.println("v: " + String.format(message, args));
        t.printStackTrace(System.out);
    }

    @Override public void d(String message, Object... args)
    {
        System.out.println("d: " + String.format(message, args));
    }

    @Override public void d(Throwable t, String message, Object... args)
    {
        System.out.println("d: " + String.format(message, args));
        t.printStackTrace(System.out);
    }

    @Override public void i(String message, Object... args)
    {
        System.out.println("i: " + String.format(message, args));
    }

    @Override public void i(Throwable t, String message, Object... args)
    {
        System.out.println("i: " + String.format(message, args));
        t.printStackTrace(System.out);
    }

    @Override public void w(String message, Object... args)
    {
        System.out.println("w: " + String.format(message, args));
    }

    @Override public void w(Throwable t, String message, Object... args)
    {
        System.out.println("w: " + String.format(message, args));
        t.printStackTrace(System.out);
    }

    @Override public void e(String message, Object... args)
    {
        System.out.println("e: " + String.format(message, args));
    }

    @Override public void e(Throwable t, String message, Object... args)
    {
        System.out.println("e: " + String.format(message, args));
        t.printStackTrace(System.out);
    }
}
