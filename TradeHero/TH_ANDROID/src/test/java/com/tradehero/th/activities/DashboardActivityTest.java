package com.tradehero.th.activities;

import android.content.Intent;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationType;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.persistence.notification.NotificationCache;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowProgressDialog;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class DashboardActivityTest
{
    private DashboardActivity activity;

    @Inject NotificationCache notificationCache;

    @Before public void setUp()
    {
        activity = Robolectric.buildActivity(DashboardActivity.class).create().start().resume().get();
    }

    @Test public void pressBackButtonTwiceWillExitTheApp() throws Exception
    {
        activity.onBackPressed();
        assertThat(activity.isFinishing()).isFalse();

        activity.onBackPressed();
        assertThat(activity.isFinishing()).isTrue();
    }

    @Ignore("Robolectric does not work well with custom attribute, therefor it is failing while inflating layout fragment_private_message ("
            + "because listItemLayout is always 0 with Robolectric)! If we set listItemLayout manually inside PrivateDiscussionView, "
            + "this test will pass, however it is not recommended")
    @Test public void onNewIntentTest()
    {
        ShadowProgressDialog.reset();
        NotificationKey mockNotificationKey = new NotificationKey(123);
        NotificationDTO mockNotificationDTO = new NotificationDTO();
        mockNotificationDTO.pushTypeId = NotificationType.PrivateMessage.getTypeId();
        mockNotificationDTO.replyableTypeId = DiscussionType.PRIVATE_MESSAGE.value;
        mockNotificationDTO.referencedUserId = 108805;
        notificationCache.put(mockNotificationKey, mockNotificationDTO);

        Intent intent = new Intent();
        intent.putExtras(mockNotificationKey.getArgs());
        activity.onNewIntent(intent);

        assertThat(activity.getDashboardNavigator().getCurrentFragment())
                .isInstanceOf(ReplyPrivateMessageFragment.class);
    }
}