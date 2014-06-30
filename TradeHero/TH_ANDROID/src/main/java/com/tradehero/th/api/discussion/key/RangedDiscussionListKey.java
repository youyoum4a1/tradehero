package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.pagination.RangedKey;
import java.util.Map;

public class RangedDiscussionListKey extends DiscussionListKey implements RangedKey
{
    public static final String MAX_COUNT_BUNDLE_KEY = RangedDiscussionListKey.class.getName() + ".maxCount";
    public static final String MAX_ID_BUNDLE_KEY = RangedDiscussionListKey.class.getName() + ".maxId";
    public static final String MIN_ID_BUNDLE_KEY = RangedDiscussionListKey.class.getName() + ".minId";

    public final Integer maxCount;
    public final Integer maxId;
    public final Integer minId;

    //<editor-fold desc="Constructors">
    public RangedDiscussionListKey(DiscussionType inReplyToType, int inReplyToId)
    {
        this(inReplyToType, inReplyToId, null);
    }

    public RangedDiscussionListKey(DiscussionType inReplyToType, int inReplyToId, Integer maxCount)
    {
        this(inReplyToType, inReplyToId, maxCount, null);
    }

    public RangedDiscussionListKey(DiscussionListKey discussionListKey, Integer maxCount, Integer maxId)
    {
        this(discussionListKey.inReplyToType, discussionListKey.inReplyToId, maxCount, maxId, null);
    }

    public RangedDiscussionListKey(DiscussionListKey discussionListKey, Integer maxCount, Integer maxId, Integer minId)
    {
        this(discussionListKey.inReplyToType, discussionListKey.inReplyToId, maxCount, maxId, minId);
    }

    public RangedDiscussionListKey(DiscussionListKey discussionListKey, Integer maxCount)
    {
        this(discussionListKey, maxCount, null);
    }

    public RangedDiscussionListKey(DiscussionType inReplyToType, int inReplyToId, Integer maxCount, Integer maxId)
    {
        this(inReplyToType, inReplyToId, maxCount, maxId, null);
    }

    public RangedDiscussionListKey(DiscussionType inReplyToType, int inReplyToId, Integer maxCount, Integer maxId, Integer minId)
    {
        super(inReplyToType, inReplyToId);
        this.maxCount = maxCount;
        this.maxId = maxId;
        this.minId = minId;
    }

    public RangedDiscussionListKey(Bundle args)
    {
        super(args);
        this.maxCount = args.containsKey(MAX_COUNT_BUNDLE_KEY) ? args.getInt(MAX_COUNT_BUNDLE_KEY) : null;
        this.maxId = args.containsKey(MAX_ID_BUNDLE_KEY) ? args.getInt(MAX_ID_BUNDLE_KEY) : null;
        this.minId = args.containsKey(MIN_ID_BUNDLE_KEY) ? args.getInt(MIN_ID_BUNDLE_KEY) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (maxCount == null ? 0 : maxCount.hashCode()) ^
                (maxId == null ? 0 : maxId.hashCode()) ^
                (minId == null ? 0 : minId.hashCode());
    }

    @Override protected boolean equalFields(DiscussionListKey other)
    {
        return super.equalFields(other) &&
                (other instanceof RangedDiscussionListKey) &&
                equalFields((RangedDiscussionListKey) other);
    }

    public boolean equalFields(RangedDiscussionListKey other)
    {
        return super.equalFields(other) &&
                (maxCount == null ? other.maxCount == null : maxCount.equals(other.maxCount)) &&
                (maxId == null ? other.maxId == null : maxId.equals(other.maxId)) &&
                (minId == null ? other.minId == null : minId.equals(other.minId));
    }

    //<editor-fold desc="FramedKey">
    @Override public Integer getMaxCount()
    {
        return maxCount;
    }

    @Override public Integer getMaxId()
    {
        return maxId;
    }

    @Override public Integer getMinId()
    {
        return minId;
    }
    //</editor-fold>

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
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

    @Override public Map<String, Object> toMap()
    {
        Map<String, Object> generatedMap = super.toMap();

        generatedMap.put(RangedKey.JSON_MAX_COUNT, maxCount);
        generatedMap.put(RangedKey.JSON_MAX_ID, maxId);
        generatedMap.put(RangedKey.JSON_MIN_ID, minId);
        return generatedMap;
    }
}
