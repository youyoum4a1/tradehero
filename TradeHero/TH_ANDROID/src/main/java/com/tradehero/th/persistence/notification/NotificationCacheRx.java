package com.ayondo.academy.persistence.notification;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.ayondo.academy.api.notification.NotificationDTO;
import com.ayondo.academy.api.notification.NotificationKey;
import com.ayondo.academy.network.service.NotificationServiceWrapper;
import com.ayondo.academy.persistence.SingleCacheMaxSize;
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
