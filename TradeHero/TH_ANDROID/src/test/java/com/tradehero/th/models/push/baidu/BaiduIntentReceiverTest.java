package com.tradehero.th.models.push.baidu;

import android.content.Intent;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.models.push.CommonNotificationBuilder;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricMavenTestRunner.class)
public class BaiduIntentReceiverTest
{
    private DashboardActivity activity;
    private BaiduIntentReceiver baiduIntentReceiver;

    /** we should use THNotificationBuilder interface here actually, however, that interface does not have method to create intent for testing **/
    @Inject CommonNotificationBuilder commonNotificationBuilder;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivity.class);
        baiduIntentReceiver = new BaiduIntentReceiver();
    }

    @Test public void shouldHandleOpenNotificationEventCorrectly()
    {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.replyableTypeId = DiscussionType.NEWS.value;
        notificationDTO.replyableId = 888; // 8 is luck
        notificationDTO.pushId = 123; // random

        Intent receivingIntent = commonNotificationBuilder.composeIntent(notificationDTO);
        baiduIntentReceiver.onReceive(activity, receivingIntent);

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent).isNotNull();
        assertThat(startedIntent.getComponent().getClassName()).isEqualToIgnoringCase(DashboardActivity.class.getName());
        assertThat(startedIntent.getExtras()).isNotNull();

        NotificationKey notificationKey = new NotificationKey(startedIntent.getExtras());
        assertThat(notificationKey.key).isEqualTo(notificationDTO.pushId);
    }
}