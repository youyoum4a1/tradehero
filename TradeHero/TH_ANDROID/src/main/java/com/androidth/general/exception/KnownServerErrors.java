package com.androidth.general.exception;

import android.support.annotation.NonNull;

public class KnownServerErrors
{
    private static final String ALREADY_REGISTERED = "This Facebook user is already registered";
    private static final String ALREADY_LINKED = "Error, this social account has already been linked to another user";
    private static final String WRONG_DISCUSSION_TYPE = "Wrong Discussion Type";
    private static final String TRADING_OUTSIDE_HOURS_TYPE = "This only trades between";
    private static final String WATCHLIST_POSITION_ALREADY_CREATED = "Watchlist position is already created";
    private static final String CANNOT_SELL_WITHIN_60_MIN = "You cannot sell within 60 minutes from your last purchase on this position";

    public static boolean isAccountAlreadyRegistered(@NonNull String errorMessage)
    {
        return errorMessage.contains(ALREADY_REGISTERED);
    }

    public static boolean isAccountAlreadyLinked(@NonNull String errorMessage)
    {
        return errorMessage.contains(ALREADY_LINKED);
    }

    public static boolean isWrongDiscussionType(@NonNull String errorMessage)
    {
        return errorMessage.contains(WRONG_DISCUSSION_TYPE);
    }

    public static boolean isTradingOutsideHoursType(@NonNull String errorMessage)
    {
        return errorMessage.startsWith(TRADING_OUTSIDE_HOURS_TYPE);
    }

    public static boolean isWatchlistPositionAlreadyCreated(@NonNull String errorMessage)
    {
        return errorMessage.startsWith(WATCHLIST_POSITION_ALREADY_CREATED);
    }

    public static boolean isCannotSellWithin60Min(@NonNull String errorMessage)
    {
        return errorMessage.startsWith(CANNOT_SELL_WITHIN_60_MIN);
    }
}
