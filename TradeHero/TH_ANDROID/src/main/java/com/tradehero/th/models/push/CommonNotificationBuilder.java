package com.tradehero.th.models.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.thm.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.persistence.notification.NotificationCache;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Expect notificationDTO with specified id is already there somewhere in the notificationCache
 */
public class CommonNotificationBuilder implements THNotificationBuilder
{
    private final Context context;
    private final IntPreference maxGroupNotifications;
    private final NotificationCache notificationCache;
    private final NotificationGroupHolder notificationGroupHolder;

    @Inject public CommonNotificationBuilder(
            Context context,
            NotificationCache notificationCache,
            NotificationGroupHolder notificationGroupHolder,
            @MaxGroupNotifications IntPreference maxGroupNotifications)
    {
        this.context = context;
        this.notificationCache = notificationCache;
        this.maxGroupNotifications = maxGroupNotifications;
        this.notificationGroupHolder = notificationGroupHolder;
    }

    @Override public Notification buildNotification(String message, int notificationId)
    {
        NotificationDTO notificationDTO = notificationCache.get(new NotificationKey(notificationId));
        if (notificationDTO == null)
        {
            NotificationKey key = new NotificationKey(notificationId);
            notificationCache.register(key, new NotificationFetchTaskListener());
            notificationCache.getOrFetchAsync(key, false);

            return null;
        }
        else
        {
            return buildNotification(notificationDTO);
        }
    }

    private Notification buildNotification(NotificationDTO notificationDTO)
    {
        int groupId = uniquifyNotificationId(notificationDTO);

        List<NotificationDTO> notificationDTOs = notificationGroupHolder.get(groupId);
        boolean firstMessageOfTheGroup = notificationDTOs == null || notificationDTOs.isEmpty();

        String title = context.getString(R.string.app_name);

        NotificationCompat.Builder notificationBuilder = getCommonNotificationBuilder();
        notificationBuilder.setContentTitle(title);

        Notification notification;
        if (firstMessageOfTheGroup)
        {
            notificationDTOs = new ArrayList<>();
            notificationDTOs.add(notificationDTO);
            notificationGroupHolder.put(groupId, notificationDTOs);
            notification = notificationBuilder
                    .setContentText(notificationDTO.text)
                    .build();
        }
        else
        {
            notificationDTOs.add(notificationDTO);

            int totalUnreadCount = notificationDTOs.size();
            String message = getNotificationContextText(notificationDTO);
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle(
                    notificationBuilder
                            .setContentText(message)
                            .setNumber(totalUnreadCount)
            );

            // Add the incoming alert as the first line in bold
            style.addLine(Html.fromHtml("<b>" + message + "</b>"));

            // Add any extra messages to the notification style
            int extraMessages = Math.min(maxGroupNotifications.get(), totalUnreadCount);
            for (int i = totalUnreadCount -1; i >= totalUnreadCount - extraMessages; --i)
            {
                style.addLine(notificationDTOs.get(i).text);
            }

            // If we have more messages to show then the EXTRA_MESSAGES_TO_SHOW, add a summary
            if (totalUnreadCount > maxGroupNotifications.get())
            {
                style.setSummaryText(context.getString(R.string.inbox_summary, totalUnreadCount - maxGroupNotifications.get()));
            }

            notification = style.build();
        }

        notification.contentIntent = PendingIntent.getBroadcast(context, notificationDTO.pushId, composeIntent(notificationDTO), 0);
        return notification;
    }

    public Intent composeIntent(NotificationDTO notificationDTO)
    {
        Intent intent = new Intent(PushConstants.ACTION_NOTIFICATION_CLICKED);

        intent.putExtra(PushConstants.KEY_PUSH_ID, String.valueOf(notificationDTO.pushId));
        intent.putExtra(PushConstants.KEY_PUSH_GROUP_ID, uniquifyNotificationId(notificationDTO));
        intent.putExtra(PushConstants.KEY_NOTIFICATION_ID, notificationDTO.pushId);
        intent.putExtra(PushConstants.KEY_NOTIFICATION_CONTENT, notificationDTO.text);
        return intent;
    }

    @Override public int getNotifyId(int notificationId)
    {
        NotificationDTO notificationDTO = notificationCache.get(new NotificationKey(notificationId));

        if (notificationDTO != null)
        {
            return uniquifyNotificationId(notificationDTO);
        }

        return notificationId;
    }

    private String getNotificationContextText(NotificationDTO notificationDTO)
    {
        String message = notificationDTO.text;

        List<NotificationDTO> notificationGroup = notificationGroupHolder.get(notificationDTO.pushId);
        int size = notificationGroup != null ? notificationGroup.size() : 0;
        DiscussionType discussionType = DiscussionType.fromValue(notificationDTO.replyableTypeId);
        switch (discussionType)
        {
            case NEWS:
            case SECURITY:
            case TIMELINE_ITEM:
                if (size > 0)
                {
                    message = context.getString(R.string.notification_new_activities, size);
                    // TODO what should be the message? ex: There are %d people commenting on your ...
                }
                break;
            case BROADCAST_MESSAGE:
            case PRIVATE_MESSAGE:
                if (size > 0)
                {
                    message = context.getString(R.string.notification_unread_messages, size);
                }
                break;
        }
        return message;
    }

    private int uniquifyNotificationId(NotificationDTO notificationDTO)
    {
        Integer characteristicId = notificationDTO.replyableTypeId;
        int modulo = DiscussionType.values().length + 1;
        int moduloId = 0;
        if (characteristicId == null)
        {
            characteristicId = getUniquePushNotificationIdentifier();
        }
        else
        {
            DiscussionType discussionType = DiscussionType.fromValue(characteristicId);
            moduloId = discussionType.value;

            switch (discussionType)
            {
                case NEWS:
                case SECURITY:
                case TIMELINE_ITEM:
                    characteristicId = notificationDTO.replyableId;
                    break;
                case BROADCAST_MESSAGE:
                case PRIVATE_MESSAGE:
                    characteristicId = notificationDTO.referencedUserId;
                    if (characteristicId == null)
                    {
                        characteristicId = notificationDTO.threadId;
                    }
                    break;
            }

            if (characteristicId == null)
            {
                characteristicId = notificationDTO.pushId;
            }
        }
        return (modulo * characteristicId) + moduloId;
    }

    private Integer getUniquePushNotificationIdentifier()
    {
        return (int) System.currentTimeMillis();
    }

    private NotificationCompat.Builder getCommonNotificationBuilder()
    {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_status_icon);
        return new NotificationCompat.Builder(context)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.th_logo)
                        .setAutoCancel(true);
    }

    private class NotificationFetchTaskListener implements DTOCacheNew.Listener<NotificationKey,NotificationDTO>
    {
        @Override public void onDTOReceived(NotificationKey key, NotificationDTO value)
        {
            Notification notification = buildNotification(value);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(getNotifyId(value.pushId), notification);
        }

        @Override public void onErrorThrown(NotificationKey key, Throwable error)
        {
            Timber.d("There is a problem fetching notification: id=%d", key.key);
        }
    }
}
