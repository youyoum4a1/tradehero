package com.androidth.general.api.social;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum FollowerState
{
    NOT_FOLLOWER(0),
    FREE_FOLLOWER(1),
    PREMIUM_FOLLOWER(2),
    ;

    private final int value;

    FollowerState(int value)
    {
        this.value = value;
    }

    @JsonCreator
    @NonNull public static FollowerState create(int value)
    {
        for (FollowerState candidate : values())
        {
            if (candidate.value == value)
            {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unknown FollowerState value: " + value);
    }
}
