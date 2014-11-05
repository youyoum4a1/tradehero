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
import android.support.annotation.NonNull;
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
        super(maxSize.get(), 5, 5, dtoCacheUtil);
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
}
