package com.ayondo.academy.api.discussion.key;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.users.UserBaseKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MessageDiscussionListKeyTest extends MessageDiscussionListKeyTestBase
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
