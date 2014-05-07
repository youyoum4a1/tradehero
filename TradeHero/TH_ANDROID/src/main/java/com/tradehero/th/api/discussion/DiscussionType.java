package com.tradehero.th.api.discussion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DiscussionType
{
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

    @JsonCreator public static DiscussionType fromDescription(String description)
    {
        for (DiscussionType discussionType : values())
        {
            if (discussionType.description.equals(description))
            {
                return discussionType;
            }
        }
        throw new IllegalArgumentException("Description " + description + " does not map to a DiscussionType");
    }

    @JsonCreator public static DiscussionType fromValue(int value)
    {
        for (DiscussionType discussionType : values())
        {
            if (discussionType.value == value)
            {
                return discussionType;
            }
        }
        throw new IllegalArgumentException("Value " + value + " does not map to a DiscussionType");
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
