package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.UserTransactionHistoryId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache
public class UserTransactionHistoryCacheRx extends BaseDTOCacheRx<UserTransactionHistoryId, UserTransactionHistoryDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
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
