package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.users.UserBaseKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MessageDiscussionListKeyTest extends BaseMessageDiscussionListKeyTest
{
    @Test public void equalItself()
    {
        MessageDiscussionListKey key1 = new MessageDiscussionListKey(
                DiscussionType.PRIVATE_MESSAGE,
                13,
                new UserBaseKey(14),
                new UserBaseKey(15),
                20,
                500,
                400
        );
        assertTrue(key1.equals(key1));
    }

    @Test public void equalSameValues()
    {
        MessageDiscussionListKey key1 = new MessageDiscussionListKey(
                DiscussionType.PRIVATE_MESSAGE,
                13,
                new UserBaseKey(14),
                new UserBaseKey(15),
                20,
                500,
                400
        );
        MessageDiscussionListKey key1Again = new MessageDiscussionListKey(
                DiscussionType.PRIVATE_MESSAGE,
                13,
                new UserBaseKey(14),
                new UserBaseKey(15),
                20,
                500,
                400
        );
        assertTrue(key1.equals(key1Again));
        assertTrue(key1Again.equals(key1));
    }

    @Test public void equalSameValuesWhenNulls()
    {
        MessageDiscussionListKey key1 = new MessageDiscussionListKey(
                DiscussionType.PRIVATE_MESSAGE,
                13,
                new UserBaseKey(14),
                new UserBaseKey(15),
                null,
                null,
                null
        );
        MessageDiscussionListKey key1Again = new MessageDiscussionListKey(
                DiscussionType.PRIVATE_MESSAGE,
                13,
                new UserBaseKey(14),
                new UserBaseKey(15),
                null,
                null,
                null
        );
        assertTrue(key1.equals(key1Again));
        assertTrue(key1Again.equals(key1));
    }
}
