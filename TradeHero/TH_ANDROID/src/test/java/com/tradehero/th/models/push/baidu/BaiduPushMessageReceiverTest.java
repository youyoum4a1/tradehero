package com.tradehero.th.models.push.baidu;

import android.app.NotificationManager;
import android.content.Context;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.persistence.notification.NotificationCache;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedString;

import static com.tradehero.util.TestUtil.getResourceAsByteArray;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricMavenTestRunner.class)
public class BaiduPushMessageReceiverTest
{
    private DashboardActivity activity;
    private NotificationManager notificationManager;

    @Inject BaiduPushMessageReceiver baiduPushMessageReceiver;
    @Inject NotificationCache notificationCache;
    @Inject Converter converter;

    @Before public void setUp()
    {
        activity = Robolectric.setupActivity(DashboardActivity.class);
        notificationManager = (NotificationManager) Robolectric.application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Test public void shouldAddOneNotificationOnReceivedAMessageFromBaidu() throws IOException, ConversionException
    {
        assertThat(shadowOf(notificationManager).size()).isEqualTo(0);

        String receivedMessage = new String(getResourceAsByteArray(BaiduPushMessageDTO.class, "baidu_push_dto.json"));
        TypedString typedString = new TypedString(receivedMessage);
        BaiduPushMessageDTO baiduPushMessageDTO = (BaiduPushMessageDTO) converter.fromBody(typedString, BaiduPushMessageDTO.class);

        assertThat(baiduPushMessageDTO.getId()).isEqualTo(8990773);
        NotificationDTO mockNotificationDTO = new NotificationDTO();
        mockNotificationDTO.text = "This is a fake message";
        notificationCache.put(new NotificationKey(baiduPushMessageDTO.getId()), mockNotificationDTO);

        baiduPushMessageReceiver.onMessage(activity, receivedMessage, null);

        assertThat(shadowOf(notificationManager).size()).isEqualTo(1);
    }
}