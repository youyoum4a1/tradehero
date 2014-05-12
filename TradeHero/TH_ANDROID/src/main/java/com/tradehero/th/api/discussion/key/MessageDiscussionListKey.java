package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.Map;

public class MessageDiscussionListKey extends DiscussionListKey
{
    public static final String SENDER_USER_BUNDLE_KEY = MessageDiscussionListKey.class.getName() + ".senderUser";
    public static final String RECIPIENT_USER_BUNDLE_KEY = MessageDiscussionListKey.class.getName() + ".recipientUser";
    public static final String MAX_COUNT_BUNDLE_KEY = MessageDiscussionListKey.class.getName() + ".maxCount";
    public static final String MAX_ID_BUNDLE_KEY = MessageDiscussionListKey.class.getName() + ".maxId";
    public static final String MIN_ID_BUNDLE_KEY = MessageDiscussionListKey.class.getName() + ".minId";

    public static final String QUERY_SENDER_USER = "senderUserId";
    public static final String QUERY_RECIPIENT_USER = "recipientUserId";
    public static final String QUERY_MAX_COUNT = "maxCount";
    public static final String QUERY_MAX_ID = "maxId";
    public static final String QUERY_MIN_ID = "minId";

    public final UserBaseKey senderUser;
    public final UserBaseKey recipientUser;
    public final Integer maxCount;
    public final Integer maxId;
    public final Integer minId;

    //<editor-fold desc="Constructors">
    public MessageDiscussionListKey(
            DiscussionType inReplyToType,
            int inReplyToId,
            UserBaseKey senderUser,
            UserBaseKey recipientUser,
            Integer maxCount,
            Integer maxId,
            Integer minId)
    {
        super(inReplyToType, inReplyToId);
        this.senderUser = senderUser;
        this.recipientUser = recipientUser;
        this.maxCount = maxCount;
        this.maxId = maxId;
        this.minId = minId;
    }

    public MessageDiscussionListKey(Bundle args)
    {
        super(args);
        if (!args.containsKey(SENDER_USER_BUNDLE_KEY))
        {
            throw new IllegalArgumentException("Missing SENDER_USER_BUNDLE_KEY");
        }
        if (!args.containsKey(RECIPIENT_USER_BUNDLE_KEY))
        {
            throw new IllegalArgumentException("Missing RECIPIENT_USER_BUNDLE_KEY");
        }
        this.senderUser = new UserBaseKey(args.getBundle(SENDER_USER_BUNDLE_KEY));
        this.recipientUser = new UserBaseKey(args.getBundle(RECIPIENT_USER_BUNDLE_KEY));
        if (args.containsKey(MAX_COUNT_BUNDLE_KEY))
        {
            this.maxCount = args.getInt(MAX_COUNT_BUNDLE_KEY);
        }
        else
        {
            this.maxCount = null;
        }
        if (args.containsKey(MAX_ID_BUNDLE_KEY))
        {
            this.maxId = args.getInt(MAX_ID_BUNDLE_KEY);
        }
        else
        {
            this.maxId = null;
        }
        if (args.containsKey(MIN_ID_BUNDLE_KEY))
        {
            this.minId = args.getInt(MIN_ID_BUNDLE_KEY);
        }
        else
        {
            this.minId = null;
        }
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (senderUser == null ? 0 : senderUser.hashCode()) ^
                (recipientUser == null ? 0 : recipientUser.hashCode()) ^
                (maxCount == null ? 0 : maxCount.hashCode()) ^
                (maxId == null ? 0 : maxId.hashCode()) ^
                (minId == null ? 0 : minId.hashCode());
    }

    @Override protected boolean equalFields(DiscussionListKey other)
    {
        return super.equalFields(other) &&
                (other instanceof MessageDiscussionListKey) &&
                equalFields((MessageDiscussionListKey) other);
    }

    public boolean equalFields(MessageDiscussionListKey other)
    {
        return super.equalFields(other) &&
                (senderUser == null ? other.senderUser == null : senderUser.equals(other.senderUser)) &&
                (recipientUser == null ? other.recipientUser == null : recipientUser.equals(other.recipientUser)) &&
                (maxCount == null ? other.maxCount == null : maxCount.equals(other.maxCount)) &&
                (maxId == null ? other.maxId == null : maxId.equals(other.maxId)) &&
                (minId == null ? other.minId == null : minId.equals(other.minId));
    }

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (senderUser != null)
        {
            args.putBundle(SENDER_USER_BUNDLE_KEY, senderUser.getArgs());
        }
        if (recipientUser != null)
        {
            args.putBundle(RECIPIENT_USER_BUNDLE_KEY, recipientUser.getArgs());
        }
        if (maxCount != null)
        {
            args.putInt(MAX_COUNT_BUNDLE_KEY, maxCount);
        }
        if (maxId != null)
        {
            args.putInt(MAX_ID_BUNDLE_KEY, maxId);
        }
        if (minId != null)
        {
            args.putInt(MIN_ID_BUNDLE_KEY, minId);
        }
    }

    public MessageDiscussionListKey next()
    {
        return new MessageDiscussionListKey(
                inReplyToType, inReplyToId,
                senderUser, recipientUser,
                maxCount,
                null, maxId);
    }

    public MessageDiscussionListKey prev()
    {
        return new MessageDiscussionListKey(
                inReplyToType, inReplyToId,
                senderUser, recipientUser,
                maxCount,
                minId, null);
    }

    @Override public Map<String, Object> toMap()
    {
        Map<String, Object> generatedMap = super.toMap();

        if (senderUser != null)
        {
            generatedMap.put(QUERY_SENDER_USER, senderUser.key);
        }
        if (recipientUser != null)
        {
            generatedMap.put(QUERY_RECIPIENT_USER, recipientUser.key);
        }
        if (maxCount != null)
        {
            generatedMap.put(QUERY_MAX_COUNT, maxCount);
        }
        if (maxId != null)
        {
            generatedMap.put(QUERY_MAX_ID, maxId);
        }
        if (minId != null)
        {
            generatedMap.put(QUERY_MIN_ID, minId);
        }
        return generatedMap;
    }

    @Override public String toString()
    {
        return "MessageDiscussionListKey{" +
                super.toString() +
                ", senderUser=" + senderUser +
                ", recipientUser=" + recipientUser +
                ", maxCount=" + maxCount +
                ", maxId=" + maxId +
                ", minId=" + minId +
                '}';
    }
}
