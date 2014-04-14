package com.tradehero.th.api.discussion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by xavier on 3/7/14.
 */
public enum VoteDirection
{
    DownVote(-1, "down"),
    UnVote(0, "cancel"),
    UpVote(1, "up");

    public final int value;
    public final String description;

    VoteDirection(int value, String description)
    {
        this.value = value;
        this.description = description;
    }

    @JsonCreator public static VoteDirection fromDescription(String description)
    {
        for (VoteDirection voteDirection : values())
        {
            if (voteDirection.description.equals(description))
            {
                return voteDirection;
            }
        }
        throw new IllegalArgumentException("Description " + description + " does not map to a VoteDirection");
    }

    @JsonCreator public static VoteDirection fromValue(int value)
    {
        for (VoteDirection voteDirection : values())
        {
            if (voteDirection.value == value)
            {
                return voteDirection;
            }
        }
        throw new IllegalArgumentException("Value " + value + " does not map to a VoteDirection");
    }

    @JsonValue public String getDescription()
    {
        return description;
    }

    // For serialize in path with @Path annotation (retrofit)
    @Override public String toString()
    {
        return description;
    }
}
