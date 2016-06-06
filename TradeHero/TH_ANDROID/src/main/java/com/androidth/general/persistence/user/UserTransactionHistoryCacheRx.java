package com.androidth.general.persistence.user;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.users.UserTransactionHistoryDTO;
import com.androidth.general.api.users.UserTransactionHistoryId;
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
