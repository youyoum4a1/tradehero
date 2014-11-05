package com.tradehero.th.persistence.notification;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class NotificationCacheRx extends BaseFetchDTOCacheRx<NotificationKey, NotificationDTO>
{
    @NotNull private final Lazy<NotificationServiceWrapper> notificationServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public NotificationCacheRx(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull Lazy<NotificationServiceWrapper> notificationServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), 5, 5, dtoCacheUtil);
        this.notificationServiceWrapper = notificationServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<NotificationDTO> fetch(@NotNull NotificationKey key)
    {
        return notificationServiceWrapper.get().getNotificationDetailRx(key);
    }

    public void onNext(@NotNull List<? extends NotificationDTO> notificationDTOs)
    {
        for (@NotNull NotificationDTO notificationDTO : notificationDTOs)
        {
            onNext(notificationDTO.getDTOKey(), notificationDTO);
        }
    }
}
