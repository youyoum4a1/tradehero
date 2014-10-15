package com.tradehero.th.misc.exception;

import org.jetbrains.annotations.NotNull;

public class KnownServerErrors
{
    private static final String ALREADY_LINKED = "Error, this social account has already been linked to another user";

    public static boolean isAccountAlreadyLinked(@NotNull String errorMessage)
    {
        return errorMessage.contains(ALREADY_LINKED);
    }
}
