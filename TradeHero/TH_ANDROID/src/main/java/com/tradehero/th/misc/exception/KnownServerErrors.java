package com.tradehero.th.misc.exception;

import org.jetbrains.annotations.NotNull;

public class KnownServerErrors
{
    private static final String ALREADY_REGISTERED = "This Facebook user is already registered";
    private static final String ALREADY_LINKED = "Error, this social account has already been linked to another user";
    private static final String WRONG_DISCUSSION_TYPE = "Wrong Discussion Type";
    private static final String TRADING_OUTSIDE_HOURS_TYPE = "This only trades between";

    public static boolean isAccountAlreadyRegistered(@NotNull String errorMessage)
    {
        return errorMessage.contains(ALREADY_REGISTERED);
    }

    public static boolean isAccountAlreadyLinked(@NotNull String errorMessage)
    {
        return errorMessage.contains(ALREADY_LINKED);
    }

    public static boolean isWrongDiscussionType(@NotNull String errorMessage)
    {
        return errorMessage.contains(WRONG_DISCUSSION_TYPE);
    }

    public static boolean isTradingOutsideHourseType(@NotNull String errorMessage)
    {
        return errorMessage.startsWith(TRADING_OUTSIDE_HOURS_TYPE);
    }
}
