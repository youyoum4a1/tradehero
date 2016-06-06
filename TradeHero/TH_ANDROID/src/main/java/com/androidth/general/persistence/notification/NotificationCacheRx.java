package com.androidth.general.persistence.notification;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.persistence.prefs.IntPreference;
import com.androidth.general.api.notification.NotificationDTO;
import com.androidth.general.api.notification.NotificationKey;
import com.androidth.general.network.service.NotificationServiceWrapper;
import com.androidth.general.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class NotificationCacheRx extends BaseFetchDTOCacheRx<NotificationKey, NotificationDTO>
{
    @NonNull private final Lazy<NotificationServiceWrapper> notificationServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public NotificationCacheRx(
            @SingleCacheMaxSize IntPreference maxSize,
            @NonNull Lazy<NotificationServiceWrapper> notificationServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), dtoCacheUtil);
        this.notificationServiceWrapper = notificationServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<NotificationDTO> fetch(@NonNull NotificationKey key)
    {
        return notificationServiceWrapper.get().getNotificationDetailRx(key);
    }

    public void onNext(@NonNull List<? extends NotificationDTO> notificationDTOs)
    {
        for (NotificationDTO notificationDTO : notificationDTOs)
        {
            onNext(notificationDTO.getDTOKey(), notificationDTO);
        }
    }

    public void setUnread(@NonNull NotificationKey key, boolean unread)
    {
        NotificationDTO notificationDTO = getCachedValue(key);
        if (notificationDTO != null && notificationDTO.unread != unread)
        {
            notificationDTO.unread = unread;
            onNext(key, notificationDTO);
        }
    }

    public void setUnreadAll(boolean unread)
    {
        for (NotificationKey key : snapshot().keySet())
        {
            setUnread(key, unread);
        }
    }
}
