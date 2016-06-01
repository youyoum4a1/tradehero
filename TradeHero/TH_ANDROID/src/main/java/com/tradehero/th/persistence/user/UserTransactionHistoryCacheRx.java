package com.ayondo.academy.persistence.user;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.users.UserTransactionHistoryDTO;
import com.ayondo.academy.api.users.UserTransactionHistoryId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class UserTransactionHistoryCacheRx extends BaseDTOCacheRx<UserTransactionHistoryId, UserTransactionHistoryDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull List<UserTransactionHistoryDTO> transactionHistoryDTOs)
    {
        for (UserTransactionHistoryDTO transactionHistoryDTO : transactionHistoryDTOs)
        {
            onNext(transactionHistoryDTO.getUserTransactionHistoryId(), transactionHistoryDTO);
        }
    }
}
