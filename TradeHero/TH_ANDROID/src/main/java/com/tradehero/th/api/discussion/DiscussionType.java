package com.tradehero.th.api.discussion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DiscussionType
{
    UNKNOWN(0, "Unknown"),
    COMMENT(1, "comment"),
    TIMELINE_ITEM(2, "timelineitem"),
    SECURITY(3, "security"),
    NEWS(4, "news"),
    PRIVATE_MESSAGE(5, "private-message"),
    BROADCAST_MESSAGE(6, "broadcast-message");

    public final int value;
    public final String description;

    DiscussionType(int value, String description)
    {
        this.value = value;
        this.description = description;
    }

    @JsonCreator public static DiscussionType getInstance(String o)
    {
        try
        {
            int value = Integer.parseInt(o);
            return fromValue(value);
        }
        catch (NumberFormatException ex)
        {
            return fromDescription(o);
        }
    }

    //@JsonCreator
    public static DiscussionType fromDescription(String description)
    {
        for (DiscussionType discussionType : values())
        {
            if (discussionType.description.equals(description))
            {
                return discussionType;
            }
        }
        return UNKNOWN;
    }

    //@JsonCreator
    public static DiscussionType fromValue(int value)
    {
        for (DiscussionType discussionType : values())
        {
            if (discussionType.value == value)
            {
                return discussionType;
            }
        }
        return UNKNOWN;
    }

    @JsonValue final String value()
    {
        return description;
    }

    @Override public String toString()
    {
        return description;
    }
}
