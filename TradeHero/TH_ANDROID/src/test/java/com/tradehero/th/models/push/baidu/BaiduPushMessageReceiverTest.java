package com.tradehero.th.models.push.baidu;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.thm.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.persistence.notification.NotificationCache;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowPendingIntent;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedString;

import static com.tradehero.util.TestUtil.getResourceAsByteArray;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricMavenTestRunner.class)
public class BaiduPushMessageReceiverTest
{
    private static final String[] NOTIFICATION_TEST_MESSAGES = new String[] {
            "{\"title\":\"N0\",\"description\":\"message0\",\"custom_content\": {\"i\":\"0\", \"discussion-type\":\"4\"}}",
            "{\"title\":\"N1\",\"description\":\"message1\",\"custom_content\": {\"i\":\"1\", \"discussion-type\":\"4\"}}",
            "{\"title\":\"N2\",\"description\":\"message2\",\"custom_content\": {\"i\":\"2\", \"discussion-type\":\"4\"}}",
            "{\"title\":\"N3\",\"description\":\"message3\",\"custom_content\": {\"i\":\"3\", \"discussion-type\":\"4\"}}",
            "{\"title\":\"N4\",\"description\":\"message4\",\"custom_content\": {\"i\":\"4\", \"discussion-type\":\"4\"}}",
    };

    private DashboardActivity activity;
    private ShadowNotificationManager shadowNotificationManager;

    @Inject BaiduPushMessageReceiver baiduPushMessageReceiver;
    @Inject NotificationCache notificationCache;
    @Inject Converter converter;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivity.class);
        NotificationManager notificationManager = (NotificationManager) Robolectric.application.getSystemService(Context.NOTIFICATION_SERVICE);
        shadowNotificationManager = shadowOf(notificationManager);
    }

    @Test public void shouldAddOneNotificationOnReceivedAMessageFromBaidu() throws IOException, ConversionException
    {
        assertThat(shadowNotificationManager.size()).isEqualTo(0);

        String receivedMessage = new String(getResourceAsByteArray(BaiduPushMessageDTO.class, "baidu_push_dto.json"));
        TypedString typedString = new TypedString(receivedMessage);
        BaiduPushMessageDTO baiduPushMessageDTO = (BaiduPushMessageDTO) converter.fromBody(typedString, BaiduPushMessageDTO.class);

        assertThat(baiduPushMessageDTO.getId()).isEqualTo(8990773);
        NotificationDTO mockNotificationDTO = new NotificationDTO();
        mockNotificationDTO.text = "This is a fake message";
        notificationCache.put(new NotificationKey(baiduPushMessageDTO.getId()), mockNotificationDTO);

        baiduPushMessageReceiver.onMessage(activity, receivedMessage, null);

        assertThat(shadowNotificationManager.size()).isEqualTo(1);
    }

    @Test public void shouldGroupNotificationWhenMultipleMessagesWithSameReplyableTypeId()
    {
        assertThat(shadowNotificationManager.size()).isEqualTo(0);

        NotificationDTO[] notificationDTOs = new NotificationDTO[NOTIFICATION_TEST_MESSAGES.length];
        for (int i=0; i < notificationDTOs.length; ++i)
        {
            notificationDTOs[i] = new NotificationDTO();
            NotificationDTO notificationDTO = notificationDTOs[i];
            notificationDTO.replyableTypeId = DiscussionType.NEWS.value;
            notificationDTO.text = "N" + i;

            notificationCache.put(new NotificationKey(i), notificationDTOs[i]);
            baiduPushMessageReceiver.onMessage(activity, NOTIFICATION_TEST_MESSAGES[i], null);
        }

        // since they are all in one roup, there should be only one notification appear on notification center, and this one has to be in inbox style
        assertThat(shadowNotificationManager.size()).isEqualTo(1);
        Notification notification = shadowNotificationManager.getAllNotifications().get(0);
        assertThat(notification).isNotNull();
        assertThat(notification.number).isEqualTo(NOTIFICATION_TEST_MESSAGES.length);

        ShadowNotification shadowNotification = shadowOf(notification);
        // Content text should be the last notified message text
        assertThat(shadowNotification.getContentText()).isEqualTo(notificationDTOs[NOTIFICATION_TEST_MESSAGES.length-1].text);
        assertThat(shadowNotification.getContentTitle()).isEqualTo(activity.getString(R.string.app_name));

        ShadowPendingIntent shadowPendingIntent = shadowOf(notification.contentIntent);
        Intent savedIntent = shadowPendingIntent.getSavedIntent();
        assertThat(savedIntent).isNotNull();
        assertThat(savedIntent.getAction()).isEqualTo(PushConstants.ACTION_NOTIFICATION_CLICKED);
        assertThat(savedIntent.getExtras()).isNotNull();
        assertThat(savedIntent.getExtras().containsKey(PushConstants.KEY_NOTIFICATION_CONTENT)).isTrue();
        assertThat(savedIntent.getExtras().get(PushConstants.KEY_NOTIFICATION_CONTENT))
                .isEqualTo(notificationDTOs[NOTIFICATION_TEST_MESSAGES.length-1].text);
    }
}