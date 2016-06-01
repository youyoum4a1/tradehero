package com.ayondo.academy.api.discussion.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOKey;
import com.ayondo.academy.api.discussion.DiscussionType;

abstract public class DiscussionKey<T extends DiscussionKey>
        implements DTOKey, Comparable<T>
{
    static final String BUNDLE_KEY_TYPE = DiscussionKey.class.getName() + ".type";
    static final String BUNDLE_KEY_ID = DiscussionKey.class.getName() + ".id";
    public static final String BUNDLE_KEY_DISCUSSION_KEY_BUNDLE = DiscussionKey.class.getName() + ".bundle";

    public final Integer id;

    //<editor-fold desc="Constructors">
    protected DiscussionKey(@NonNull Integer id)
    {
        this.id = id;
    }

    protected DiscussionKey(@NonNull Bundle args)
    {
        if (!args.containsKey(DiscussionKey.BUNDLE_KEY_ID))
        {
            throw new IllegalStateException("Discussion bundle should contain id of the discussion");
        }
        this.id = args.getInt(BUNDLE_KEY_ID);
    }
    //</editor-fold>

    @NonNull abstract public DiscussionType getType();

    public Bundle getArgs()
    {
        Bundle bundle = new Bundle();
        putParameters(bundle);
        return bundle;
    }

    protected void putParameters(Bundle args)
    {
        args.putString(BUNDLE_KEY_TYPE, getType().name());
        args.putInt(BUNDLE_KEY_ID, id);
    }

    @Override public int hashCode()
    {
        DiscussionType type = getType();
        return (type == null ? 0 : type.hashCode()) ^
                id.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object other)
    {
        return equalClass(other) && equalFields((DiscussionKey) other);
    }

    public boolean equalClass(Object other)
    {
        return other != null && getClass().equals(other.getClass());
    }

    public boolean equalFields(DiscussionKey other)
    {
        return other != null &&
                (id == null ? other.id == null : id.equals(other.id));
    }

    @Override public String toString()
    {
        return "DiscussionKey{" +
                "id=" + id +
                '}';
    }


    @Override public int compareTo(T another)
    {
        if (another == null)
        {
            return 1;
        }
        return id - another.id;
    }
}
